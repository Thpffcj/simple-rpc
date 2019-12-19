package cn.edu.nju.client.cluster.impl;

import cn.edu.nju.client.bean.ProviderService;
import cn.edu.nju.client.cluster.ClusterStrategy;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;

/**
 * Created by thpffcj on 2019/12/19.
 *
 * 基于权重的随机负载均衡
 */
public class WeightRandomClusterStrategyImpl implements ClusterStrategy {

    @Override
    public ProviderService select(List<ProviderService> serviceRoutes) {
        List<ProviderService> providerServices = Lists.newArrayList();

        // 在providerServices中添加权重个providerService
        for (ProviderService providerService : serviceRoutes) {
            int weight = providerService.getWeight();
            for (int i = 0; i < weight; i++) {
                providerServices.add(providerService);
            }
        }

        int maxLength = providerServices.size();
        int index = RandomUtils.nextInt(0, maxLength - 1);
        return providerServices.get(index);
    }
}
