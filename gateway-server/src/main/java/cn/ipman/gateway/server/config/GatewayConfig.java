package cn.ipman.gateway.server.config;

import cn.ipman.rpc.core.api.RegistryCenter;
import cn.ipman.rpc.core.registry.ipman.IpManRegistryCenter;
import cn.ipman.rpc.core.registry.zk.ZkRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import java.util.Properties;

import static cn.ipman.gateway.server.plugin.GatewayPlugin.GATEWAY_PREFIX;

/**
 * 网关配置类，用于配置注册中心和应用启动时的处理器映射。
 *
 * @Author IpMan
 * @Date 2024/5/25 09:42
 */
@Configuration
public class GatewayConfig {

    /**
     * 创建注册中心实例，默认为ZkRegistryCenter。
     * @return RegistryCenter 注册中心实例
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "rpcman.zk", value = "enabled", havingValue = "true")
    public RegistryCenter zkRc() {
        return new ZkRegistryCenter();
    }


    /**
     * 创建注册中心实例，registry-man, @linkUrl: <a href="https://github.com/ipipman/registry-man">...</a>
     * @return RegistryCenter 注册中心实例
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnProperty(prefix = "registry-ipman", value = "enabled", havingValue = "true")
    public RegistryCenter ipManRc() {
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
    @Qualifier("simpleUrlHandlerMapping")
    ApplicationRunner runner(@Autowired ApplicationContext context) {
        return args -> {
            // 获取SimpleUrlHandlerMapping实例，并设置路径到处理器的映射
            SimpleUrlHandlerMapping handlerMapping = context.getBean(SimpleUrlHandlerMapping.class);
            Properties mappings = new Properties();
            mappings.put(GATEWAY_PREFIX + "/**", "gatewayWebHandler");
            handlerMapping.setMappings(mappings);
            handlerMapping.initApplicationContext();
            System.out.println("GatewayConfig.runner");
        };
    }
}
