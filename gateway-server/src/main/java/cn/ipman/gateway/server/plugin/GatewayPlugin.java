package cn.ipman.gateway.server.plugin;

import cn.ipman.gateway.server.chain.GatewayPluginChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关插件接口。
 * 定义了网关插件的行为规范，包括插件的启动、停止、名称获取、支持的请求判断以及实际的请求处理逻辑。
 */
public interface GatewayPlugin {

    /**
     * 网关请求的前缀路径。
     * 用于标识网关处理的请求路径特征。
     */
    String GATEWAY_PREFIX = "/gw";

    /**
     * 启动插件。
     * 实现该方法以在插件启动时执行必要的初始化工作。
     */
    @SuppressWarnings("unused")
    void start();

    /**
     * 停止插件。
     * 实现该方法以在插件停止时执行必要的资源释放或清理工作。
     */
    @SuppressWarnings("unused")
    void stop();

    /**
     * 获取插件名称。
     * @return 插件的唯一名称。
     */
    String getName();

    /**
     * 判断插件是否支持处理给定的请求。
     * @param exchange 当前的服务，用于获取请求信息。
     * @return 如果插件支持处理该请求，则返回true；否则返回false。
     */
    boolean support(ServerWebExchange exchange);

    /**
     * 处理网关请求。
     * 插件通过该方法实现对请求的处理，或者将请求传递给下一个插件（或链）进行处理。
     * @param exchange 当前的服务器exchange，包含请求和响应的信息。
     * @param chain 网关插件链，用于将请求传递给下一个插件或结束请求处理。
     * @return Mono<Void>，表示异步处理结果。
     */
    Mono<Void> handle(ServerWebExchange exchange, GatewayPluginChain chain);
}
