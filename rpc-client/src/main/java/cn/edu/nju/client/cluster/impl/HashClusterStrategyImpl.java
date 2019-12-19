package cn.edu.nju.client.cluster.impl;

import cn.edu.nju.client.bean.ProviderService;
import cn.edu.nju.client.cluster.ClusterStrategy;
import cn.edu.nju.common.util.IpUtil;

import java.util.List;

/**
 * Created by thpffcj on 2019/12/19.
 *
 * 基于IP地址的哈希负载均衡
 */
public class HashClusterStrategyImpl implements ClusterStrategy {

    @Override
    public ProviderService select(List<ProviderService> serviceRoutes) {
        String realIP = IpUtil.getRealIp();
        int hashCode = realIP.hashCode();

        int size = serviceRoutes.size();
        return serviceRoutes.get(hashCode % size);
    }
}
