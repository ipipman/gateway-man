package cn.ipman.gateway.plugin;

import cn.ipman.gateway.chain.GatewayPluginChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface GatewayPlugin {

    String GATEWAY_PREFIX = "/gw";

    void start();
    void stop();

    String getName();

    boolean support(ServerWebExchange exchange);

    Mono<Void> handle(ServerWebExchange exchange, GatewayPluginChain chain);
}
