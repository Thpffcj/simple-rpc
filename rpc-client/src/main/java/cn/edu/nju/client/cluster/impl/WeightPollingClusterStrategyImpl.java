package cn.edu.nju.client.cluster.impl;

import cn.edu.nju.client.bean.ProviderService;
import cn.edu.nju.client.cluster.ClusterStrategy;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by thpffcj on 2019/12/19.
 *
 * 考虑权重的PollingClusterStrategy
 */
public class WeightPollingClusterStrategyImpl implements ClusterStrategy {

    private int counter = 0;
    private Lock lock = new ReentrantLock();

    @Override
    public ProviderService select(List<ProviderService> serviceRoutes) {
        ProviderService providerService = null;

        try {
            lock.tryLock(10, TimeUnit.SECONDS);
            List<ProviderService> providerServices = Lists.newArrayList();
            for (ProviderService serviceRoute : serviceRoutes) {
                int weight = serviceRoute.getWeight();
                for (int i = 0; i < weight; i++) {
                    providerServices.add(serviceRoute);
                }
            }

            if (counter >= providerServices.size()) {
                counter = 0;
            }
            providerService = providerServices.get(counter);
            counter++;
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            lock.unlock();
        }

        if (providerService == null) {
            providerService = serviceRoutes.get(0);
        }
        return providerService;
    }
}
