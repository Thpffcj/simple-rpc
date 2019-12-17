package cn.edu.nju.client.util;

import cn.edu.nju.client.bean.ProviderService;
import cn.edu.nju.client.cache.ServiceRouteCache;
import cn.edu.nju.client.config.RpcClientConfiguration;
import com.google.common.collect.Lists;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by thpffcj on 2019/12/16.
 */
@Component
public class ZKUtil {

    @Autowired
    private RpcClientConfiguration rpcClientConfiguration;

    @Autowired
    private ZkClient zkClient;

    @Autowired
    private ServiceRouteCache serviceRouteCache;

    /**
     * 注册serviceName节点事件通知
     * @param serviceName
     */
    public void subscribeZKEvent(String serviceName) {
        String path = rpcClientConfiguration.getZkRoot() + "/" + serviceName;
        zkClient.subscribeChildChanges(path, new IZkChildListener() {

            @Override
            public void handleChildChange(String parentPath, List<String> list) throws Exception {
                if (CollectionUtils.isNotEmpty(list)) {
                    List<ProviderService> providerServices = convertToProviderService(list);
                    serviceRouteCache.updateCache(serviceName, providerServices);
                }
            }
        });
    }

    /**
     * 通过服务名获得ProviderService
     * @param serviceName
     * @return
     */
    public List<ProviderService> getServiceInfos(String serviceName) {
        String path = rpcClientConfiguration.getZkRoot() + "/" + serviceName;
        List<String> children = zkClient.getChildren(path);

        List<ProviderService> providerServices = convertToProviderService(children);
        return providerServices;
    }

    /**
     * 将字符串转为ProviderService
     * @param list 172.19.142.63:8082:8888
     * @return
     */
    private List<ProviderService> convertToProviderService(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayListWithCapacity(0);
        }

        List<ProviderService> providerServices = list.stream().map(v -> {
            String[] serviceInfos = v.split(":");
            return ProviderService.builder()
                    .serverIp(serviceInfos[0])
                    .serverPort(Integer.parseInt(serviceInfos[1]))
                    .networkPort(Integer.parseInt(serviceInfos[2]))
                    .build();
        }).collect(Collectors.toList());

        return providerServices;
    }
}
