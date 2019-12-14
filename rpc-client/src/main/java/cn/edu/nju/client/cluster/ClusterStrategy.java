package cn.edu.nju.client.cluster;

import cn.edu.nju.client.bean.ProviderService;

import java.util.List;

/**
 * Created by thpffcj on 2019/12/14.
 */
public interface ClusterStrategy {

    ProviderService select(List<ProviderService> serviceRoutes);
}
