package cn.ipman.gateway.plugin;

import cn.ipman.gateway.AbstractGatewayPlugin;
import cn.ipman.rpc.core.api.LoadBalancer;
import cn.ipman.rpc.core.api.RegistryCenter;
import cn.ipman.rpc.core.cluster.RoundRibonLoadBalancer;
import cn.ipman.rpc.core.meta.InstanceMeta;
import cn.ipman.rpc.core.meta.ServiceMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/6/2 13:57
 */
@Component("rpcman")
public class IMRpcPlugin extends AbstractGatewayPlugin {

    public static final String NAME = "rpcman";
    private static final String prefix = GATEWAY_PREFIX + "/" + NAME + "/";

    // 注册中心，用于获取服务实例信息
    @Autowired
    RegistryCenter rc;

    // 负载均衡器，用于选择服务实例
    LoadBalancer<InstanceMeta> loadBalancer = new RoundRibonLoadBalancer<>();

    @Override
    public Mono<Void> doHandle(ServerWebExchange exchange) {
        System.out.println(" =====>>>> [IpMan RPC Plugin] IpMan Gateway web handler ...");

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
        InstanceMeta instanceMeta = loadBalancer.choose(instanceMetas);
        String url = instanceMeta.toHttpUrl();
        System.out.println("loadBalance to url -> " + url);
        System.out.println("loadBalance to instanceMeta -> " + instanceMeta);

        // 4. 拿到请求的报文
        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();

        // 5. 通过webclient 发送post请求
        WebClient client = WebClient.create(url);
        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .body(requestBody, DataBuffer.class)
                .retrieve()
                .toEntity(String.class);

        // 6. 通过entity 获取响应报文
        Mono<String> body = entity.mapNotNull(ResponseEntity::getBody);

        // 7. 组装响应报文
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        exchange.getResponse().getHeaders().add("ipman.gw.version", "v1.0.0");
        return body.flatMap(x -> exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(x.getBytes()))));

    }

    @Override
    public boolean doSupport(ServerWebExchange exchange) {
        return exchange.getRequest().getPath().value().startsWith(prefix);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
