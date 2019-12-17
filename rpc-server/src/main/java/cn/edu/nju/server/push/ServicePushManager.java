package cn.edu.nju.server.push;

import cn.edu.nju.api.annotation.RpcService;
import cn.edu.nju.common.util.IpUtil;
import cn.edu.nju.server.config.RpcServerConfiguration;
import cn.edu.nju.server.util.SpringBeanFactory;
import cn.edu.nju.server.util.ZKUtil;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by thpffcj on 2019/12/16.
 *
 * 将服务信息注册到Zookeeper中
 */
@Component
public class ServicePushManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServicePushManager.class);

    @Autowired
    private ZKUtil zkUtil;

    @Autowired
    private RpcServerConfiguration rpcServerConfiguration;

    /**
     * 当Server端启动后，自动将当前Server所提供的所有带有@ZnsService注解的Service Impl注册到Zookeeper中，在Zookeeper
     * 中存储数据结构为 ip:httpPort:acceptorPort
     */
    public void registerIntoZK() {

        Map<String, Object> beanWithAnnotations =
                SpringBeanFactory.getBeanListByAnnotationClass(RpcService.class);

        if (MapUtils.isEmpty(beanWithAnnotations)) {
            return;
        }

        zkUtil.createRootNode();

        for (Object bean : beanWithAnnotations.values()) {
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            String serviceName = rpcService.cls().getName();
            pushServiceInfoIntoZK(serviceName);
        }
        LOGGER.info("Register service into zookeeper successfully");
    }

    /**
     * 创建服务信息节点
     * @param serviceName
     */
    private void pushServiceInfoIntoZK(String serviceName) {

        zkUtil.createPersistentNode(serviceName);

        // 172.19.142.63:8082:8888
        String serviceAddress = IpUtil.getRealIp()
                + ":" + rpcServerConfiguration.getServerPort()
                + ":" + rpcServerConfiguration.getNetworkPort();
        String serviceAddressPath = serviceName + "/" + serviceAddress;

        zkUtil.createPersistentNode(serviceAddressPath);

        LOGGER.info("Register service[{}] into zookeeper successfully", serviceAddressPath);
    }

}
