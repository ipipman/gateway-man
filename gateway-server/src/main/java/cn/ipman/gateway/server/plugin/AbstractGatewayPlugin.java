package cn.ipman.gateway.server.plugin;

import cn.ipman.gateway.server.chain.GatewayPluginChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 抽象网关插件类，提供网关插件的基本实现。
 * 网关插件用于在请求通过网关时进行各种处理，如认证、限流等。
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

    /**
     * 处理网关请求。
     * 根据当前插件是否支持该请求，决定是进行特定处理还是将请求传递给下一个插件。
     *
     * @param exchange 网关请求交换信息。
     * @param chain    网关插件链，用于调用下一个插件或进行默认处理。
     * @return Mono<Void>，表示异步处理结果。
     */
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, GatewayPluginChain chain) {
        boolean isSupported = support(exchange);
        System.out.println(" =====>>>> plugin[" + this.getName() + "], support=" + isSupported);
        if (isSupported) {
            chain.setHitMapping(true);
            return doHandle(exchange, chain);
        }
        return chain.handle(exchange);
    }

    /**
     * 判断当前插件是否支持请求的处理
     *
     * @param exchange 网关请求交换信息。
     * @return boolean，如果支持处理则返回true，否则返回false。
     */
    @Override
    public boolean support(ServerWebExchange exchange) {
        return doSupport(exchange);
    }

    /**
     * 子类实现此方法以进行实际的请求处理。
     *
     * @param exchange 网关请求交换信息。
     * @param chain    网关插件链，用于调用下一个插件或进行默认处理。
     * @return Mono<Void>，表示异步处理结果。
     */
    public abstract Mono<Void> doHandle(ServerWebExchange exchange, GatewayPluginChain chain);

    /**
     * 子类实现此方法以判断是否支持请求的处理
     *
     * @param exchange 网关请求交换信息。
     * @return boolean，如果支持处理则返回true，否则返回false。
     */
    public abstract boolean doSupport(ServerWebExchange exchange);

    /**
     * 移除字符串末尾的特定字符。
     *
     * @param str      原始字符串。
     * @param lastChar 需要移除的末尾字符。
     * @return 移除末尾字符后的字符串。
     */
    public String removeLastChar(String str, char lastChar) {
        if (str.endsWith(String.valueOf(lastChar))) {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }

    /**
     * 使用WebClient发送POST请求。
     *
     * @param path     请求路径。
     * @param requestBody 请求体。
     * @return Mono<String>，表示异步处理结果。
     */
    public Mono<String> post(String path, Flux<DataBuffer> requestBody) {
        // 5. 通过webclient 发送post请求
        WebClient client = WebClient.create(path);
        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .body(requestBody, DataBuffer.class)
                .retrieve()
                .toEntity(String.class);

        // 6. 通过entity 获取响应报文
        return entity.mapNotNull(ResponseEntity::getBody);
    }

}
