package cn.edu.nju.client;

import cn.edu.nju.client.pull.ServicePullManager;
import cn.edu.nju.client.runner.RpcRequestManager;
import cn.edu.nju.client.runner.RpcRequestPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by thpffcj on 2019/12/16.
 *
 * 当Client端发起一个远程服务调用时，ZnsRequestManager将会启动一个Connector与Acceptor进行连接，同时会保存通道信息
 * ChannelHolder到内部，直到请求完成，再进行通道信息销毁。
 * TODO
 */
@Component
public class RpcClientRunner {

    @Autowired
    private ServicePullManager servicePullManager;

    @Autowired
    private RpcRequestPool rpcRequestPool;

    public void run() {

        // 启动请求管理器
        RpcRequestManager.startRpcRequestManager(rpcRequestPool);

        // 从Zookeeper拉取服务提供者信息
        servicePullManager.pullServiceFromZK();


    }
}
