package cn.ipman.gateway.plugin;

import cn.ipman.gateway.AbstractGatewayPlugin;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
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
        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();

        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        exchange.getResponse().getHeaders().add("ipman.gw.version", "v1.0.0");
        exchange.getResponse().getHeaders().add("ipman.gw.plugin", NAME);

        if (backend == null || backend.isEmpty()){
            return requestBody.flatMap(x -> exchange.getResponse().writeWith(Mono.just(x))).then();
        }

        // 5. 通过webclient 发送post请求
        WebClient client = WebClient.create(backend);
        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .body(requestBody, DataBuffer.class)
                .retrieve()
                .toEntity(String.class);

        // 6. 通过entity 获取响应报文
        Mono<String> body = entity.mapNotNull(ResponseEntity::getBody);

        // 7. 组装响应报文
        return body.flatMap(x -> exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(x.getBytes()))));

    }

    @Override
    public boolean doSupport(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();
        return (path.startsWith(prefix) ||
                removeLastChar(path, '/').equals(removeLastChar(path, '/')));
    }

    @Override
    public String getName() {
        return NAME;
    }
}
