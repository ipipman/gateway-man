package cn.ipman.gateway;

import cn.ipman.rpc.core.api.RegistryCenter;
import cn.ipman.rpc.core.registry.ipman.IpManRegistryCenter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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


}
