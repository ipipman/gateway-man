package cn.ipman.gateway;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface GatewayPlugin {

    void start();
    void stop();

    String getName();

    boolean support(ServerWebExchange exchange);

    Mono<Void> handle(ServerWebExchange exchange);
}
