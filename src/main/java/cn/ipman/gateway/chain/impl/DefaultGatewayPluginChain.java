package cn.ipman.gateway.chain.impl;

import cn.ipman.gateway.chain.GatewayPluginChain;
import cn.ipman.gateway.plugin.GatewayPlugin;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Default GatewayPluginChain.
 *
 * @Author IpMan
 * @Date 2024/6/2 15:58
 */
public class DefaultGatewayPluginChain implements GatewayPluginChain {

    List<GatewayPlugin> plugins;
    int index = 0;

    public DefaultGatewayPluginChain(List<GatewayPlugin> plugins) {
        this.plugins = plugins;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        return Mono.defer(() -> {
            if (index < plugins.size()) {
                return plugins.get(index++).handle(exchange, this);
            }
            return Mono.empty();
        });
    }
}
