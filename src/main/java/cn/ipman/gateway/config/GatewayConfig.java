package cn.ipman.gateway.config;

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
 * 网关配置类，用于配置注册中心和应用启动时的处理器映射。
 *
 * @Author IpMan
 * @Date 2024/5/25 09:42
 */
@Configuration
public class GatewayConfig {

    /**
     * 根据属性配置创建注册中心实例。
     * 仅当registry-ipman.enabled属性为true时，该配置生效。
     *
     * @return 返回IpManRegistryCenter的实例，用于服务的注册与发现。
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnProperty(prefix = "registry-ipman", value = "enabled", havingValue = "true")
    public RegistryCenter rc() {
        return new IpManRegistryCenter();
    }


    /**
     * 配置应用启动时的处理器映射，并初始化。
     * 主要用于将特定路径映射到指定的处理器上。
     *
     * @param context 应用上下文，用于从BeanFactory中获取SimpleUrlHandlerMapping实例。
     * @return 返回ApplicationRunner的实例，其run方法在应用启动时执行。
     */
    @Bean
    ApplicationRunner runner(@Autowired ApplicationContext context) {
        return args -> {
            // 获取SimpleUrlHandlerMapping实例，并设置路径到处理器的映射
            SimpleUrlHandlerMapping handlerMapping = context.getBean(SimpleUrlHandlerMapping.class);
            Properties mappings = new Properties();
            mappings.put("/ga/**", "gatewayWebHandler");
            handlerMapping.setMappings(mappings);
            handlerMapping.initApplicationContext();
            System.out.println("GatewayConfig.runner");
        };
    }
}
