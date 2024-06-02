package cn.ipman.gateway.demo;

import cn.ipman.gateway.demo.pojo.User;
import cn.ipman.rpc.core.api.RegistryCenter;
import cn.ipman.rpc.core.api.RpcRequest;
import cn.ipman.rpc.core.api.RpcResponse;
import cn.ipman.rpc.core.meta.InstanceMeta;
import cn.ipman.rpc.core.meta.ServiceMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class GatewayDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayDemoApplication.class, args);
    }

    /**
     * 处理RPC请求的接口方法.
     *
     * @param request RPC请求对象
     * @return RPC响应对象
     */
    @RequestMapping("/rpcman")
    public RpcResponse<Object> invoke(@RequestBody RpcRequest request) {
        RpcResponse<Object> rpcResponse = new RpcResponse<>();
        rpcResponse.setStatus(true);
        // 创建并设置模拟的用户对象作为响应数据
        rpcResponse.setData(new User(1111, "II-V10-8081"
                + " ipman-" + System.currentTimeMillis()));
        return rpcResponse;
    }

    /**
     * 注册服务实例的ApplicationRunner bean.
     *
     * @param context Spring应用上下文
     * @return ApplicationRunner 实例
     */
    @Bean
    public ApplicationRunner providerRun(@Autowired ApplicationContext context) {
        return x -> {
            RegistryCenter rc = context.getBean(RegistryCenter.class);

            // 构建服务元数据
            // ServiceMeta(app=app1, namespace=public, env=dev, name=cn.ipman.rpc.demo.api.UserService, version=1.0, parameters={})
            ServiceMeta service = ServiceMeta.builder()
                    .app("app1")
                    .namespace("public")
                    .env("dev")
                    .name("cn.ipman.rpc.demo.api.UserService")
                    .version("1.0")
                    .build();

            // 构建实例元数据
            // InstanceMeta(scheme=http, host=192.168.31.232, port=9081, context=rpcman, status=false, parameters={unit=B002, gray=false, dc=shanghai-5, tc=1000})
            Map<String, String> parameters = new HashMap<>();
            parameters.put("unit", "B002");
            parameters.put("gray", "false");
            parameters.put("dc", "shanghai-5");
            parameters.put("tc", "1000");
            InstanceMeta instanceMeta = new InstanceMeta(
                    "http", "localhost", 8889, "rpcman", true, parameters);

            // 注册服务实例
            rc.register(service, instanceMeta);
        };
    }


}
