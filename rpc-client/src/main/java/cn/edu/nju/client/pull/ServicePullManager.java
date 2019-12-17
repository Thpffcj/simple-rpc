package cn.edu.nju.client.pull;

import cn.edu.nju.client.util.ZKUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
}
