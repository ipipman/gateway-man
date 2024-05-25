package cn.ipman.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/5/25 08:07
 */
@Component
public class GatewayRouter {

    @Bean
    public RouterFunction<?> helloRouterFunction() {
        return route(GET("/hello"),
                request -> ServerResponse.ok()
                        .body(Mono.just("hello, gateway"), String.class));
    }
}
