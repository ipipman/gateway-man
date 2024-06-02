package cn.ipman.gateway.chain;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface GatewayPluginChain {

    Mono<Void> handle(ServerWebExchange exchange);

}
