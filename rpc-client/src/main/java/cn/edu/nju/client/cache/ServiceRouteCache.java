package cn.edu.nju.client.cache;

import cn.edu.nju.api.annotation.RpcClient;
import cn.edu.nju.client.bean.ProviderService;
import cn.edu.nju.client.util.SpringBeanFactory;
import cn.edu.nju.client.util.ZKUtil;
import cn.edu.nju.common.exception.RpcException;
import com.google.common.cache.LoadingCache;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by thpffcj on 2019/12/17.
 */
@Component
public class ServiceRouteCache {

    @Autowired
    private LoadingCache<String, List<ProviderService>> cache;

    @Autowired
    private ZKUtil zkUtil;

    /**
     * 添加缓存
     * @param serviceName
     * @param serviceRoutes
     */
    public void addCache(String serviceName, List<ProviderService> serviceRoutes) {
        cache.put(serviceName, serviceRoutes);
    }

    /**
     * 更新缓存
     * @param serviceName
     * @param serviceRoutes
     */
    public void updateCache(String serviceName, List<ProviderService> serviceRoutes) {
        cache.put(serviceName, serviceRoutes);
    }

    /**
     * 整体更新缓存
     * @param newServiceRoutesMap
     */
    public void updateCache(Map<String, List<ProviderService>> newServiceRoutesMap) {
        cache.invalidateAll();
        for (Map.Entry<String, List<ProviderService>> entry : newServiceRoutesMap.entrySet()) {
            cache.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 从缓存获得服务提供者信息
     * @param serviceName
     * @return
     */
    public List<ProviderService> getServiceRoutes(String serviceName) {
        if (cache.size() == 0) {
            reloadCache();

            if (cache.size() == 0) {
                throw new RpcException("Not any service which is available.");
            }
        }
        try {
            return cache.get(serviceName);
        } catch (ExecutionException e) {
            throw new RpcException(e);
        }
    }

    /**
     * 重新加载缓存
     */
    private void reloadCache() {
        Map<String, Object> beans = SpringBeanFactory.getBeanListByAnnotationClass(RpcClient.class);
        if (MapUtils.isEmpty(beans)) {
            return;
        }

        for (Object bean : beans.values()) {
            String serviceName = bean.getClass().getName();
            List<ProviderService> serviceRoutes = zkUtil.getServiceInfos(serviceName);
            addCache(serviceName, serviceRoutes);
        }
    }
}
