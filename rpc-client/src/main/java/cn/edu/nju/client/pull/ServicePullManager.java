package cn.edu.nju.client.pull;

import cn.edu.nju.api.annotation.RpcClient;
import cn.edu.nju.client.bean.ProviderService;
import cn.edu.nju.client.config.RpcClientConfiguration;
import cn.edu.nju.client.util.ZKUtil;
import org.apache.commons.collections.CollectionUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Created by thpffcj on 2019/12/16.
 *
 * 当Client端启动后，根据扫描到的带有@ZnsClient注解的Service Interface从Zookeeper中拉去Service提供者信息并缓存到本地，
 * 同时在Zookeeper上添加这些服务的监听事件，一旦有节点发生变动（上线/下线），就会立即更新本地缓存。
 */
@Component
public class ServicePullManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServicePullManager.class);

    @Autowired
    private ZKUtil zkUtil;

    @Autowired
    private RpcClientConfiguration rpcClientConfiguration;

    /**
     * 从ZK上拉取服务信息
     */
    public void pullServiceFromZK() {

        Reflections reflections = new Reflections(rpcClientConfiguration.getZnsClientApiPackage());
        // 获得接口
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(RpcClient.class);
        if (CollectionUtils.isEmpty(typesAnnotatedWith)) {
            return;
        }

        for (Class<?> cls : typesAnnotatedWith) {
            // 获得接口名称：ChatService
            String serviceName = cls.getName();

            // 将服务提供列表缓存到本地
//            List<ProviderService> providerServices = zkUtil.getServiceInfos(serviceName);
//            serviceRouteCache.addCache(serviceName, providerServices);

            // 监听服务节点
            zkUtil.subscribeZKEvent(serviceName);
        }

        LOGGER.info("Pull service address list from zookeeper successfully");
    }
}
