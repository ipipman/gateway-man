package cn.ipman.gateway.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * GatewayFilter接口定义了网关过滤器的行为。
 * 过滤器用于在请求处理链中添加自定义逻辑，可以用于认证、日志记录、限流等目的。
 *
 * @Author IpMan
 * @Date 2024/6/2 16:39
 */
@Component("pretreatmentFilter")
public class PretreatmentFilter implements GatewayFilter {


    /**
     * 执行过滤器逻辑。
     *
     * @param exchange 表示当前请求和响应交换的信息，可以通过它访问请求参数、头等信息，并修改响应。
     * @return Mono<Void> 表示异步操作的结果，此处返回Void表示过滤器仅关注操作的完成，不关心具体返回值。
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange) {
        System.out.println(" =====>>>> filters: pre filter ...");
        exchange.getRequest().getHeaders().toSingleValueMap()
                .forEach((k, v) -> System.out.println("key: " + k + " value: " + v));
        return Mono.empty();
    }
}
