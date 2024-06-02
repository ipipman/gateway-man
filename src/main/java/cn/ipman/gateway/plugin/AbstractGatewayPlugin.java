package cn.ipman.gateway.plugin;

import cn.ipman.gateway.chain.GatewayPluginChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/6/2 13:49
 */
public abstract class AbstractGatewayPlugin implements GatewayPlugin {

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, GatewayPluginChain chain) {
        boolean isSupported = support(exchange);
        System.out.println(" =====>>>> plugin[" + this.getName() + "], support=" + isSupported);
        return isSupported ? doHandle(exchange, chain) : chain.handle(exchange);
    }

    @Override
    public boolean support(ServerWebExchange exchange) {
        return doSupport(exchange);
    }

    public abstract Mono<Void> doHandle(ServerWebExchange exchange, GatewayPluginChain chain);

    public abstract boolean doSupport(ServerWebExchange exchange);

    public String removeLastChar(String str, char lastChar) {
        if (str.endsWith(String.valueOf(lastChar))) {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }
}
