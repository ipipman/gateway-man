package cn.ipman.gateway;

import cn.ipman.rpc.core.api.RegistryCenter;
import cn.ipman.rpc.core.meta.InstanceMeta;
import cn.ipman.rpc.core.meta.ServiceMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/5/25 08:27
 */
@Component
public class GatewayHandler {

    @Autowired
    RegistryCenter rc;

    Mono<ServerResponse> handler(ServerRequest request) {
        // 1. 通过请求路径获取服务名
        String service = request.path().substring(4);
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .name(service)
                .app("app1")
                .env("dev")
                .namespace("public")
                .version("1.0")
                .build();

        // 2. 通过注册中心, 拿到所有有效的服务实例
        List<InstanceMeta> instanceMetas = rc.fetchAll(serviceMeta);

        // 3. 先简化处理, 拿到第一个实例的url
        String url = instanceMetas.get(0).toHttpUrl();
        System.out.println(url);

        // 4. 拿到请求的报文
        Mono<String> requestMono = request.bodyToMono(String.class);
        return requestMono.flatMap(x -> {
            // 5. 通过webclient 发送post请求
            WebClient client = WebClient.create(url);
            Mono<ResponseEntity<String>> entity = client.post()
                    .header("Content-Type", "application/json")
                    .bodyValue(x)
                    .retrieve()
                    .toEntity(String.class);

            // 6. 通过entity 获取响应报文
            Mono<String> body = entity.mapNotNull(ResponseEntity::getBody);
            body.subscribe(source -> System.out.println("response:" + source));

            // 7. 组装响应报文
            return ServerResponse.ok()
                    .header("Content-Type", "application/json")
                    .header("ipman.gw.version", "v1.0.0")
                    .body(body, String.class);
        });
    }
}
