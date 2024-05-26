package cn.ipman.gateway.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/5/25 08:07
 */
@Component
public class GatewayRouter {

//    @Autowired
//    private HelloHandler helloHandler;
//
//    @Autowired
//    private GatewayHandler gatewayHandler;
//
//    @Autowired
//    private GatewayWebHandler gatewayWebHandler;
//
//    @Bean
//    public RouterFunction<?> helloRouterFunction() {
//        return route(GET("/hello"), helloHandler::handler);
//    }
//
//    @Bean
//    public RouterFunction<?> gwRouterFunction() {
//        return route(GET("/gw").or(POST("/gw/**")), gatewayHandler::handler);
//    }
//
//    @Bean
//    public RouterFunction<?> gatewayWebRouterFunction() {
//        return route(GET("/ga").or(POST("/ga/**")), gatewayWebHandler::handle);
//    }


}
