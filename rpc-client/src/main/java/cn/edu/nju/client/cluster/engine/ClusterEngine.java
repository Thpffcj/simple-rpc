package cn.edu.nju.client.cluster.engine;

import cn.edu.nju.client.cluster.ClusterStrategy;
import cn.edu.nju.client.cluster.ClusterStrategyEnum;
import cn.edu.nju.client.cluster.impl.RandomClusterStrategyImpl;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by thpffcj on 2019/12/17.
 */
public class ClusterEngine {

    private static final Map<ClusterStrategyEnum, ClusterStrategy> clusterStrategyMap = Maps.newConcurrentMap();

    static {
        clusterStrategyMap.put(ClusterStrategyEnum.RANDOM, new RandomClusterStrategyImpl());
//        clusterStrategyMap.put(ClusterStrategyEnum.WEIGHT_RANDOM, new WeightRandomClusterStrategyImpl());
//        clusterStrategyMap.put(ClusterStrategyEnum.POLLING, new PollingClusterStrategyImpl());
//        clusterStrategyMap.put(ClusterStrategyEnum.WEIGHT_POLLING, new WeightPollingClusterStrategyImpl());
//        clusterStrategyMap.put(ClusterStrategyEnum.HASH, new HashClusterStrategyImpl());
    }

    /**
     * 根据策略名获得策略实例
     * @param clusterStrategy
     * @return
     */
    public static ClusterStrategy queryClusterStrategy(String clusterStrategy) {
        ClusterStrategyEnum clusterStrategyEnum = ClusterStrategyEnum.queryByCode(clusterStrategy);
        if (clusterStrategyEnum == null) {
            return new RandomClusterStrategyImpl();
        }
        return clusterStrategyMap.get(clusterStrategyEnum);
    }
}
