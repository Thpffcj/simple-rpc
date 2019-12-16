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

    @Value("${zns.client.zk.root}")
    private String zkRoot;

    @Value("${zns.client.zk.addr}")
    private String zkAddr;

    @Value("${server.port}")
    private String znsClientPort;

    @Value("${zns.client.api.package}")
    private String znsClientApiPackage;

    @Value("${zns.cluster.strategy}")
    private String znsClientClusterStrategy;
}
