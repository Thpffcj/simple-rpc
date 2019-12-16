package cn.edu.nju.server;

import cn.edu.nju.server.acceptor.RpcServerAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

/**
 * Created by thpffcj on 2019/12/16.
 *
 * TODO 管理中心：
 * 启动提供网络服务的Netty Acceptor
 * 将拥有@RpcService注解的服务注册到zookeeper中
 * 启动HeartBeatChecker，与Zookeeper保持连接
 */
@Component
public class RpcServerRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerRunner.class);

    private static ExecutorService executor = null;

    public void run() {

        executor.execute(new RpcServerAcceptor());
    }

}
