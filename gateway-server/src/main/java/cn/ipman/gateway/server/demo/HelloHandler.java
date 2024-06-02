package cn.ipman.gateway.server.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/5/25 08:25
 */
@Component
public class HelloHandler {

    Mono<ServerResponse> handler(@SuppressWarnings("unused") ServerRequest request) {

        String url = "http://192.168.31.232:9081/rpcman";
        String requestJson = """
                {
                  "service": "cn.ipman.rpc.demo.api.UserService",
                  "methodSign": "findById@1_int",
                  "args": [100]
                }
                """;

        WebClient client = WebClient.create(url);
        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .bodyValue(requestJson)
                .retrieve()
                .toEntity(String.class);

        Mono<String> body = entity.mapNotNull(ResponseEntity::getBody);
        body.subscribe(source -> System.out.println("response:" + source));

        return ServerResponse.ok()
                .header("Content-Type", "application/json")
                .header("ipman.gw.version", "v1.0.0")
                .body(body, String.class);
    }
}
