package cn.ipman.gateway.plugin.impl;

import cn.ipman.gateway.chain.GatewayPluginChain;
import cn.ipman.gateway.plugin.AbstractGatewayPlugin;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 直接代理插件，实现对请求的直接转发处理。
 * 该插件会将请求直接转发到指定的后端服务，并将后端服务的响应返回给客户端。
 *
 * @Author IpMan
 * @Date 2024/6/2 14:04
 */
@Component("direct")
public class DirectPlugin extends AbstractGatewayPlugin {

    // 插件名称
    public static final String NAME = "direct";
    // 插件对应的URL前缀
    private static final String prefix = GATEWAY_PREFIX + "/" + NAME + "/";

    /**
     * 处理请求的逻辑。
     * 首先检查请求是否需要直接代理，如果是，则通过WebClient向后端服务发送请求，并将后端服务的响应返回给客户端。
     * 如果请求不需要直接代理，则直接向下传递处理链。
     *
     * @param exchange 当前的exchange对象，包含请求和响应信息
     * @param chain    网关插件链，用于向下传递处理链
     * @return Mono<Void>，表示异步处理结果
     */
    @Override
    public Mono<Void> doHandle(ServerWebExchange exchange, GatewayPluginChain chain) {
        System.out.println(" =====>>>> [Direct Plugin] IpMan Gateway web handler ...");
        // 从请求参数中获取后端服务地址
        String backend = exchange.getRequest().getQueryParams().getFirst("backend");
        // 获取请求体
        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();

        // 设置响应头
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        exchange.getResponse().getHeaders().add("ipman.gw.version", "v1.0.0");
        exchange.getResponse().getHeaders().add("ipman.gw.plugin", NAME);

        // 如果没有指定后端服务地址，则直接向下传递处理链
        if (backend == null || backend.isEmpty()) {
            return requestBody.flatMap(x -> exchange.getResponse().writeWith(Mono.just(x)))
                    .then(chain.handle(exchange));
        }

        // 5. 通过webclient 发送post请求
        // 6. 通过entity 获取响应报文
        Mono<String> body = post(backend, requestBody);

        // 7. 组装响应报文
        return body.flatMap(x -> exchange.getResponse()
                        .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(x.getBytes()))))
                .then(chain.handle(exchange));

    }

    /**
     * 判断当前插件是否支持处理当前请求。
     * 支持的条件是请求的路径匹配插件的URL前缀。
     *
     * @param exchange 当前的exchange对象
     * @return boolean，表示当前插件是否支持处理当前请求
     */
    @Override
    public boolean doSupport(ServerWebExchange exchange) {
        // 判断请求路径是否匹配插件的URL前缀
        String path = exchange.getRequest().getPath().value();
        return (path.startsWith(prefix) ||
                removeLastChar(prefix, '/').equals(removeLastChar(path, '/')));
    }


    /**
     * 获取插件的名称。
     *
     * @return String，表示插件的名称
     */
    @Override
    public String getName() {
        return NAME;
    }
}
