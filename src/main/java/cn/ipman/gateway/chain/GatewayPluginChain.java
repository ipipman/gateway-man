package cn.ipman.gateway.chain;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关插件链接口类
 * 负责按顺序执行一系列网关插件的处理逻辑。
 *
 * @Author IpMan
 * @Date 2024/6/2 15:58
 */
public interface GatewayPluginChain {

    /**
     * 处理服务 exchange 的主方法。
     * 通过Deferred方式异步执行插件链中的插件处理逻辑。
     *
     * @param exchange 服务 exchange
     * @return Mono<Void> 表示异步操作的结果
     */
    Mono<Void> handle(ServerWebExchange exchange);

    /**
     * 设置是否命中了路由映射。
     * 此方法主要用于在插件执行过程中动态调整插件链的行为。
     *
     * @param isMapping 命中路由映射的标记
     */
    void setHitMapping(Boolean isMapping);
}
