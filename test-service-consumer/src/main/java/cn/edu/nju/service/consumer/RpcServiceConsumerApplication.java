package cn.edu.nju.service.consumer;

import cn.edu.nju.client.RpcClientPackage;
import cn.edu.nju.client.RpcClientRunner;
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
        basePackages = "cn.edu.nju.service.consumer",
        basePackageClasses = RpcClientPackage.class
)
@SpringBootApplication
public class RpcServiceConsumerApplication implements ApplicationRunner {

    @Autowired
    private RpcClientRunner rpcClientRunner;

    public static void main(String[] args) {
        SpringApplication.run(RpcServiceConsumerApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        rpcClientRunner.run();
    }
}
