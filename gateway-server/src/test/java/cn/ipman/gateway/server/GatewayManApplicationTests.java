package cn.ipman.gateway.server;

import cn.ipman.gateway.demo.GatewayDemoApplication;
import cn.ipman.gateway.demo.pojo.User;
import cn.ipman.rpc.core.api.RpcRequest;
import cn.ipman.rpc.core.api.RpcResponse;
import cn.ipman.rpc.core.test.TestZKServer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {GatewayManApplication.class},
        properties = {"rpcman.zk.zkServer=localhost:2183", "rpcman.zk.enabled=true", "registry-ipman.enabled=false"})
@AutoConfigureWebTestClient
class GatewayManApplicationTests {

    static ApplicationContext context1;

    static TestZKServer zkServer = new TestZKServer(2183);

    @Autowired
    private WebTestClient webClient;

    @BeforeAll
    @SneakyThrows
    static void init() {
        System.out.println(" ================================ ");
        System.out.println(" =========== Mock ZK 2183 ======= ");
        System.out.println(" ================================ ");
        System.out.println(" ================================ ");
        zkServer.start();

        System.out.println(" ================================ ");
        System.out.println(" ============  8889 ============= ");
        System.out.println(" ================================ ");
        System.out.println(" ================================ ");
        context1 = SpringApplication.run(GatewayDemoApplication.class,
                "--server.port=8889",
                "--rpcman.zk.zkServer=localhost:2183",
                "--rpcman.zk.enabled=true",
                "--rpcman.zk.zkRoot=rpcman",
                "--registry-ipman.enabled=false"
        );
    }


    @Test
    void contextLoads() {
        System.out.println("gateway demo running ... ");

        // 模拟请求gateway
        RpcRequest request = new RpcRequest();
        request.setService("cn.ipman.rpc.demo.api.UserService");
        request.setMethodSign("findById@1_int");
        request.setArgs(new Object[]{100});

        // 对比返回
        RpcResponse<User> expectResponse = new RpcResponse<>();
        expectResponse.setStatus(true);
        expectResponse.setData(new User(1111, "II-V10-8081"
                + " ipman-" + System.currentTimeMillis()));

        // 模拟请求gateway
        webClient.post()
                .uri("/gw/rpcman/cn.ipman.rpc.demo.api.UserService")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RpcResponse.class).value(response -> {
                    System.out.println("response: " + JSON.toJSONString(response));
                    @SuppressWarnings("unchecked")
                    User actualUser = new JSONObject((Map) response.getData()).toJavaObject(User.class);
                    assertEquals(actualUser.getId(), expectResponse.getData().getId());
                });
    }

    @AfterAll
    static void destroy() {
        System.out.println(" ===========     close spring context     ======= ");
        SpringApplication.exit(context1, () -> 1);
        System.out.println(" ===========     stop zookeeper server    ======= ");
        zkServer.stop();
        System.out.println(" ===========     destroy in after all     ======= ");
    }
}
