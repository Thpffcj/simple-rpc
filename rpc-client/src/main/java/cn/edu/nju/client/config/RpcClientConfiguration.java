package cn.edu.nju.client.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by thpffcj on 2019/12/16.
 */
@Data
@Component
public class RpcClientConfiguration {

    @Value("${rpc.client.zk.root}")
    private String zkRoot;

    @Value("${rpc.client.zk.addr}")
    private String zkAddr;

    @Value("${server.port}")
    private String rpcClientPort;

    @Value("${rpc.client.api.package}")
    private String rpcClientApiPackage;

    @Value("${rpc.cluster.strategy}")
    private String rpcClientClusterStrategy;
}
