package cn.edu.nju.server.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by thpffcj on 2019/12/15.
 */
@Data
@Component
public class RpcServerConfiguration {

    @Value("${rpc.server.zk.root}")
    private String zkRoot;

    @Value("${rpc.server.zk.addr}")
    private String zkAddr;

    @Value("${rpc.server.zk.switch}")
    private boolean zkSwitch;

    @Value("${rpc.network.port}")
    private int networkPort;

    @Value("${server.port}")
    private int serverPort;
}
