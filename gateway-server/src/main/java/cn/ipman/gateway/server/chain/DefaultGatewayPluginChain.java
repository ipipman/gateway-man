package cn.ipman.gateway.server.chain;

import cn.ipman.gateway.server.plugin.GatewayPlugin;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;


/**
 * 默认的网关插件链实现类。
 * 负责按顺序执行一系列网关插件的处理逻辑。
 *
 * @Author IpMan
 * @Date 2024/6/2 15:58
 */
public class DefaultGatewayPluginChain implements GatewayPluginChain {

    // 存储待执行的网关插件列表
    List<GatewayPlugin> plugins;

    // 当前插件链执行的索引
    int index = 0;

    // 标记是否命中了路由映射
    Boolean hitMapping = false;

    /**
     * 构造函数，初始化插件链。
     *
     * @param plugins 网关插件列表
     */
    public DefaultGatewayPluginChain(List<GatewayPlugin> plugins) {
        this.plugins = plugins;
    }

    /**
     * 处理服务Web exchange 的主方法。
     * 通过Deferred方式异步执行插件链中的插件处理逻辑。
     *
     * @param exchange 服务 exchange
     * @return Mono<Void> 表示异步操作的结果
     */
    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        return Mono.defer(() -> {
            // 如果当前索引小于插件列表长度，说明还有插件未执行
            if (index < plugins.size()) {
                return plugins.get(index++).handle(exchange, this);
            }

            // 如果没有命中路由映射且没有更多插件执行，则返回模拟的响应
            if (!hitMapping) {
                String mock = """
                        {"result": "no supported plugin"}
                        """;
                exchange.getResponse().getHeaders().add("Content-Type", "application/json");
                exchange.getResponse().getHeaders().add("ipman.gw.version", "v1.0.0");
                return exchange.getResponse()
                        .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(mock.getBytes())));
            }
            return Mono.empty();
        });
    }

    /**
     * 设置是否命中了路由映射。
     * 此方法主要用于在插件执行过程中动态调整插件链的行为。
     *
     * @param isMapping 命中路由映射的标记
     */
    @Override
    public void setHitMapping(Boolean isMapping) {
        this.hitMapping = isMapping;
    }
}
