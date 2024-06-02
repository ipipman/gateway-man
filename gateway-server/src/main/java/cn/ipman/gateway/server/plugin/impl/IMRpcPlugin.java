package cn.ipman.gateway.server.plugin.impl;

import cn.ipman.gateway.server.chain.GatewayPluginChain;
import cn.ipman.gateway.server.plugin.AbstractGatewayPlugin;
import cn.ipman.rpc.core.api.LoadBalancer;
import cn.ipman.rpc.core.api.RegistryCenter;
import cn.ipman.rpc.core.cluster.RoundRibonLoadBalancer;
import cn.ipman.rpc.core.meta.InstanceMeta;
import cn.ipman.rpc.core.meta.ServiceMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * RPC插件实现类，用于处理RPC请求的路由与转发。
 *
 * @Author IpMan
 * @Date 2024/6/2 13:57
 */
@Component("rpcman")
public class IMRpcPlugin extends AbstractGatewayPlugin {

    // 插件名称常量
    public static final String NAME = "rpcman";
    // 请求路径前缀
    private static final String prefix = GATEWAY_PREFIX + "/" + NAME + "/";

    // 注册中心，用于获取服务实例信息
    @Autowired
    RegistryCenter rc;

    // 负载均衡器实例，用于选择服务实例
    // 负载均衡器，用于选择服务实例
    LoadBalancer<InstanceMeta> loadBalancer = new RoundRibonLoadBalancer<>();

    /**
     * 根据exchange信息获取RPC服务的实例元数据。
     *
     * @param exchange 服务器Web exchange对象，用于获取请求信息。
     * @return RPC服务实例的元数据。
     */
    private InstanceMeta getRpcInstanceMeta(ServerWebExchange exchange) {
        // 1. 通过请求路径获取服务名
        String service = exchange.getRequest().getPath().value().substring(prefix.length());
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .name(service)
                .app("app1").env("dev").namespace("public").version("1.0")
                .build();

        // 2. 通过注册中心, 拿到所有有效的服务实例
        List<InstanceMeta> instanceMetas = rc.fetchAll(serviceMeta);

        // 3. 通过负载均衡, 拿到一个实例的url
        System.out.println("instanceMetas -> " + instanceMetas);
        return loadBalancer.choose(instanceMetas);
    }

    /**
     * 处理RPC请求的主逻辑方法。
     *
     * @param exchange 服务器Web exchange对象，包含请求和响应信息。
     * @param chain    网关插件链，用于继续或结束插件链的处理。
     * @return Mono<Void>，表示异步处理结果。
     */
    @Override
    public Mono<Void> doHandle(ServerWebExchange exchange, GatewayPluginChain chain) {
        System.out.println(" =====>>>> [IpMan RPC Plugin] IpMan Gateway web handler ...");
        // 根据service信息获取RPC服务实例的元数据
        InstanceMeta instanceMeta = getRpcInstanceMeta(exchange);
        // 构建目标RPC服务的URL
        String url = instanceMeta.toHttpUrl();
        System.out.println("loadBalance to url -> " + url);
        System.out.println("loadBalance to instanceMeta -> " + instanceMeta);

        // 4. 拿到请求的报文
        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();

        // 5. 通过webclient 发送post请求
        // 6. 通过entity 获取响应报文
        Mono<String> body = post(url, requestBody);

        // 7. 组装响应报文
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        exchange.getResponse().getHeaders().add("ipman.gw.version", "v1.0.0");
        exchange.getResponse().getHeaders().add("ipman.gw.plugin", NAME);

        // 将响应体写入响应流，并继续处理插件链
        return body.flatMap(x -> exchange.getResponse()
                        .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(x.getBytes()))))
                .then(chain.handle(exchange));

    }

    /**
     * 判断当前插件是否支持处理给定的exchange请求。
     *
     * @param exchange 服务器Web exchange对象。
     * @return boolean，如果支持处理则返回true，否则返回false。
     */
    @Override
    public boolean doSupport(ServerWebExchange exchange) {
        // 判断请求路径是否匹配插件处理规则
        String path = exchange.getRequest().getPath().value();
        return (path.startsWith(prefix) ||
                removeLastChar(prefix, '/').equals(removeLastChar(path, '/')));
    }

    /**
     * 返回插件名称。
     *
     * @return String，插件名称。
     */
    @Override
    public String getName() {
        return NAME;
    }
}
