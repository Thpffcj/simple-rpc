package cn.edu.nju.client.cluster.impl;

import cn.edu.nju.client.bean.ProviderService;
import cn.edu.nju.client.cluster.ClusterStrategy;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;

/**
 * Created by thpffcj on 2019/12/14.
 *
 * 随机负载均衡算法
 */
public class RandomClusterStrategyImpl implements ClusterStrategy {

    @Override
    public ProviderService select(List<ProviderService> serviceRoutes) {
        int MAX_LEN = serviceRoutes.size();
        int index = RandomUtils.nextInt(0, MAX_LEN - 1);
        return serviceRoutes.get(index);
    }
}
