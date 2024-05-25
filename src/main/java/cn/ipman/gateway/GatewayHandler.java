package cn.ipman.gateway;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/5/25 08:27
 */
@Component
public class GatewayHandler {

    Mono<ServerResponse> handler(ServerRequest request) {
        return ServerResponse.ok().bodyValue("Hello, Gateway");
    }
}
