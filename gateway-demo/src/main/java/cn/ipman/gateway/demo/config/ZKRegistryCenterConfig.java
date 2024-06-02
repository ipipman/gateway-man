package cn.ipman.gateway.demo.config;

import cn.ipman.rpc.core.api.RegistryCenter;
import cn.ipman.rpc.core.registry.zk.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description for this class
 *
 * @Author IpMan
 * @Date 2024/6/2 18:53
 */
@Configuration
@Slf4j
public class ZKRegistryCenterConfig {

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

}
