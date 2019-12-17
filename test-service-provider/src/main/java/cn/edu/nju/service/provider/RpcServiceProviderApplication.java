package cn.edu.nju.service.provider;

import cn.edu.nju.server.RpcServerPackage;
import cn.edu.nju.server.RpcServerRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by thpffcj on 2019/12/16.
 */
@ComponentScan(
        basePackages = "cn.edu.nju.service.provider",
        basePackageClasses = RpcServerPackage.class
)
@SpringBootApplication
public class RpcServiceProviderApplication implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServiceProviderApplication.class);

    @Autowired
    private RpcServerRunner rpcServerRunner;

    public static void main(String[] args) {
        SpringApplication.run(RpcServiceProviderApplication.class, args);
        LOGGER.info("Rpc service provider application startup successfully");
    }

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        rpcServerRunner.run();
    }
}
