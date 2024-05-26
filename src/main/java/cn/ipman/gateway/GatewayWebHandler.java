package cn.ipman.gateway;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

/**
 * gateway web handler
 *
 * @Author IpMan
 * @Date 2024/5/26 07:18
 */
@Component("gatewayWebHandler")
public class GatewayWebHandler implements WebHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        return exchange.getResponse().writeWith(Mono.just(
                exchange.getResponse().bufferFactory().wrap("hello gateway".getBytes())));

    }
}
