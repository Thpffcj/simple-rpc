package cn.edu.nju.client.cluster.impl;

import cn.edu.nju.client.bean.ProviderService;
import cn.edu.nju.client.cluster.ClusterStrategy;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by thpffcj on 2019/12/19.
 *
 * 将可用服务连成一个圈，依次提供服务
 */
public class PollingClusterStrategyImpl implements ClusterStrategy {

    private int counter = 0;
    private Lock lock = new ReentrantLock();

    @Override
    public ProviderService select(List<ProviderService> serviceRoutes) {
        ProviderService providerService = null;

        try {
            lock.tryLock(10, TimeUnit.SECONDS);

            int size = serviceRoutes.size();
            if (counter >= size) {
                counter = 0;
            }

            providerService = serviceRoutes.get(counter);
            counter++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        if (providerService == null) {
            providerService = serviceRoutes.get(0);
        }
        return providerService;
    }
}
