package cn.ipman.gateway;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * GatewayWebFilter类实现了WebFilter接口，用于在网关层对请求进行过滤和处理
 *
 * @Author IpMan
 * @Date 2024/5/26 09:53
 */
@Component
public class GatewayWebFilter implements WebFilter {


    /**
     * 对通过网关的每个请求进行过滤处理。
     *
     * @param exchange 代表当前服务器和客户端之间交互的请求-响应周期。
     * @param chain 提供了继续或终止过滤链的能力。
     * @return 返回一个Mono<Void>，表示异步处理完成。
     */
    @Override
    public @NotNull Mono<Void> filter(ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        System.out.println("===>>> IpMan Gateway web filter ...");
        // 检查请求参数中是否含有"mock"，若无则正常处理请求
        if (exchange.getRequest().getQueryParams().getFirst("mock") == null) {
            return chain.filter(exchange);
        }

        // 当请求包含"mock"参数时，返回一个模拟的JSON响应
        String mock = """
                {"result": "mock"}
                """;
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        exchange.getResponse().getHeaders().add("ipman.gw.version", "v1.0.0");
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(mock.getBytes())));
    }
}
