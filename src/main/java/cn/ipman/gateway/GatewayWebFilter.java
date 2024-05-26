package cn.ipman.gateway;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/5/26 09:53
 */
@Component
public class GatewayWebFilter implements WebFilter {

    @Override
    public @NotNull Mono<Void> filter(ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        System.out.println("===>>> IpMan Gateway web filter ...");
        if (exchange.getRequest().getQueryParams().getFirst("mock") == null) {
            return chain.filter(exchange);
        }
        String mock = """
                {"result": "mock"}
                """;
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        exchange.getResponse().getHeaders().add("ipman.gw.version", "v1.0.0");
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(mock.getBytes())));
    }
}
