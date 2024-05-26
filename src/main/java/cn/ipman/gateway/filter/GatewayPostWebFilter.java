package cn.ipman.gateway.filter;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 网关后置过滤器类，用于在请求处理完成后执行一些额外的操作。
 *
 * @Author IpMan
 * @Date 2024/5/26 10:02
 */
@Component
public class GatewayPostWebFilter implements WebFilter {

    /**
     * 对请求进行过滤处理。
     * 在请求处理完成后，执行一些清理或记录日志的操作。
     *
     * @param exchange 服务器web交换机，提供关于当前请求和响应的信息。
     * @param chain    web过滤器链，用于将请求传递给下一个过滤器或处理程序。
     * @return Mono<Void> 表示异步处理完成的信号。
     */
    @Override
    public @NotNull Mono<Void> filter(@NotNull ServerWebExchange exchange, WebFilterChain chain) {
        // 通过WebFilterChain处理请求，完成后执行额外操作
        return chain.filter(exchange).doFinally(s -> {
            System.out.println("post filter ...");
            exchange.getAttributes().forEach((k, v) -> System.out.println(k + " : " + v));
        });
    }
}
