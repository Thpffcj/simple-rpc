package cn.edu.nju.server.config;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by thpffcj on 2019/12/17.
 */
@Configuration
public class BeanConfig {

    @Autowired
    private RpcServerConfiguration rpcServerConfiguration;

    @Bean
    public ZkClient zkClient() {
        return new ZkClient(rpcServerConfiguration.getZkAddr(), 5000);
    }
}
