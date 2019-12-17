package cn.edu.nju.client.config;

import cn.edu.nju.client.bean.ProviderService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created by thpffcj on 2019/12/17.
 */
@Configuration
public class BeanConfig {

    private static final int EXPIRE_SECONDS = 86400;

    @Autowired
    private RpcClientConfiguration rpcClientConfiguration;

    @Bean
    public ZkClient zkClient() {
        return new ZkClient(rpcClientConfiguration.getZkAddr(), 5000);
    }

    @Bean
    public LoadingCache<String, List<ProviderService>> buildCache() {
        return CacheBuilder.newBuilder()
                .build(new CacheLoader<String, List<ProviderService>>() {
                    @Override
                    public List<ProviderService> load(String key) throws Exception {
                        return null;
                    }
                });
    }
}
