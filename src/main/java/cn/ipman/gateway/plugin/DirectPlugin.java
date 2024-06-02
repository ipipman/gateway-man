package cn.ipman.gateway.plugin;

import cn.ipman.gateway.AbstractGatewayPlugin;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * direct proxy plugin.
 *
 * @Author IpMan
 * @Date 2024/6/2 14:04
 */
@Component("direct")
public class DirectPlugin extends AbstractGatewayPlugin {

    public static final String NAME = "direct";
    private static final String prefix = GATEWAY_PREFIX + "/" + NAME + "/";

    @Override
    public Mono<Void> doHandle(ServerWebExchange exchange) {
        System.out.println(" =====>>>> [Direct Plugin] IpMan Gateway web handler ...");
        String backend = exchange.getRequest().getQueryParams().getFirst("backend");

//        if (backend == null || backend.isEmpty())

        return null;
    }

    @Override
    public boolean doSupport(ServerWebExchange exchange) {
        return exchange.getRequest().getPath().value().startsWith(prefix);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
