package cn.ipman.gateway.web.handler;

import cn.ipman.gateway.chain.DefaultGatewayPluginChain;
import cn.ipman.gateway.filter.GatewayFilter;
import cn.ipman.gateway.plugin.GatewayPlugin;
import org.jetbrains.annotations.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

import java.util.List;


/**
 * Gateway Web Handler，负责处理网关的请求转发逻辑
 *
 * @Author IpMan
 * @Date 2024/5/26 07:18
 */
@Component("gatewayWebHandler")
public class GatewayWebHandler implements WebHandler {

    @Autowired
    List<GatewayPlugin> plugins;

    @Autowired
    List<GatewayFilter> filters;

    /**
     * 处理客户端请求，实现请求的转发。
     *
     * @param exchange 服务器与客户端之间的交互接口，包含请求和响应信息。
     * @return 返回一个Mono<Void>，表示异步处理完成。
     */
    @Override
    public @NotNull Mono<Void> handle(@NotNull ServerWebExchange exchange) {
        System.out.println(" =====>>>> IpMan Gateway web handler ... ");

        if (plugins == null || plugins.isEmpty()) {
            String mock = """
                    {"result": "no plugin"}
                    """;
            exchange.getResponse().getHeaders().add("Content-Type", "application/json");
            exchange.getResponse().getHeaders().add("ipman.gw.version", "v1.0.0");
            return exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(mock.getBytes())));
        }

        // 执行过滤
        for (GatewayFilter filter : filters) {
            filter.filter(exchange);
        }
        return new DefaultGatewayPluginChain(plugins).handle(exchange);
    }

}
