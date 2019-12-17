package cn.edu.nju.server;

import cn.edu.nju.server.acceptor.RpcServerAcceptor;
import cn.edu.nju.server.push.ServicePushManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by thpffcj on 2019/12/16.
 *
 * 管理中心：
 * 启动提供网络服务的Netty Acceptor
 * 将拥有@RpcService注解的服务注册到zookeeper中
 * 启动HeartBeatChecker，与Zookeeper保持连接
 */
@Component
public class RpcServerRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerRunner.class);

    private static ExecutorService executor = null;

    @Autowired
    private ServicePushManager servicePushManager;

    public void run() {
        executor = Executors.newFixedThreadPool(3);

        // 启动Acceptor，等待服务调用者触发请求调用
        executor.execute(new RpcServerAcceptor());

        // 将服务提供者注册到Zookeeper中
        servicePushManager.registerIntoZK();
    }

    @PreDestroy
    public void destroy() {
        if (executor != null) {
            executor.shutdown();
        }
    }
}
