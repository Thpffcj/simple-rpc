package cn.edu.nju.server.util;

import cn.edu.nju.server.config.RpcServerConfiguration;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by thpffcj on 2019/12/16.
 */
@Component
public class ZKUtil {

    @Autowired
    private ZkClient zkClient;

    @Autowired
    private RpcServerConfiguration rpcServerConfiguration;

    public void createRootNode() {
        boolean exists = zkClient.exists(rpcServerConfiguration.getZkRoot());
        if (!exists) {
            zkClient.createPersistent(rpcServerConfiguration.getZkRoot());
        }
    }

    public void createPersistentNode(String path) {
        String pathName = rpcServerConfiguration.getZkRoot() + "/" + path;
        boolean exists = zkClient.exists(pathName);
        if (!exists) {
            zkClient.createPersistent(pathName);
        }
    }

    public void createNode(String path) {
        String pathName = rpcServerConfiguration.getZkRoot() + "/" + path;
        boolean exists = zkClient.exists(pathName);
        if (!exists) {
            zkClient.createEphemeral(pathName);
        }
    }
}
