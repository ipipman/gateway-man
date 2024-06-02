package cn.ipman.gateway.web.handler;

import cn.ipman.rpc.core.api.LoadBalancer;
import cn.ipman.rpc.core.api.RegistryCenter;
import cn.ipman.rpc.core.cluster.RoundRibonLoadBalancer;
import cn.ipman.rpc.core.meta.InstanceMeta;
import cn.ipman.rpc.core.meta.ServiceMeta;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Gateway Web Handler，负责处理网关的请求转发逻辑
 *
 * @Author IpMan
 * @Date 2024/5/26 07:18
 */
@Component("gatewayWebHandler")
public class GatewayWebHandler implements WebHandler {

    // 注册中心，用于获取服务实例信息
    @Autowired
    RegistryCenter rc;

    // 负载均衡器，用于选择服务实例
    LoadBalancer<InstanceMeta> loadBalancer = new RoundRibonLoadBalancer<>();

    /**
     * 处理客户端请求，实现请求的转发。
     *
     * @param exchange 服务器与客户端之间的交互接口，包含请求和响应信息。
     * @return 返回一个Mono<Void>，表示异步处理完成。
     */
    @Override
    public @NotNull Mono<Void> handle(ServerWebExchange exchange) {
        System.out.println("===>>> IpMan Gateway web handler ...");

        // 1. 通过请求路径获取服务名
        String service = exchange.getRequest().getPath().value().substring(4);
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


}
