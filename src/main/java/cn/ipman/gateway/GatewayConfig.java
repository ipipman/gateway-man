package cn.ipman.gateway;

import cn.ipman.rpc.core.api.RegistryCenter;
import cn.ipman.rpc.core.registry.ipman.IpManRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import java.util.Properties;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/5/25 09:42
 */
@Configuration
public class GatewayConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnProperty(prefix = "registry-ipman", value = "enabled", havingValue = "true")
    public RegistryCenter rc() {
        return new IpManRegistryCenter();
    }


    @Bean
    ApplicationRunner runner(@Autowired ApplicationContext context) {
        return args -> {
            SimpleUrlHandlerMapping handlerMapping = context.getBean(SimpleUrlHandlerMapping.class);
            Properties mappings = new Properties();
            mappings.put("/ga/**", "gatewayWebHandler");
            handlerMapping.setMappings(mappings);
            handlerMapping.initApplicationContext();
            System.out.println("GatewayConfig.runner");
        };
    }
}
