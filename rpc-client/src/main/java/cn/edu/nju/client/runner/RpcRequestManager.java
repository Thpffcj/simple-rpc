package cn.edu.nju.client.runner;

import cn.edu.nju.client.bean.ChannelHolder;
import cn.edu.nju.client.bean.ProviderService;
import cn.edu.nju.client.cache.ServiceRouteCache;
import cn.edu.nju.client.cluster.ClusterStrategy;
import cn.edu.nju.client.cluster.engine.ClusterEngine;
import cn.edu.nju.client.config.RpcClientConfiguration;
import cn.edu.nju.client.connector.RpcClientConnector;
import cn.edu.nju.client.util.SpringBeanFactory;
import cn.edu.nju.common.bean.RpcRequest;
import cn.edu.nju.common.constant.StatusCode;
import cn.edu.nju.common.exception.RpcException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created by thpffcj on 2019/12/14.
 *
 * 当Client端发起一个远程服务调用时，ZnsRequestManager将会启动一个Connector与Acceptor进行连接，同时会保存通道信息
 * ChannelHolder到内部，直到请求完成，再进行通道信息销毁。
 */
public class RpcRequestManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcRequestManager.class);

    // requestId -> ChannelHolder
    private static final ConcurrentHashMap<String, ChannelHolder> channelHolderMap =
            new ConcurrentHashMap<>();

    private static final ExecutorService REQUEST_EXECUTOR = new ThreadPoolExecutor(
            30,
            100,
            0,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(30),
            new BasicThreadFactory.Builder().namingPattern("request-service-connector-%d").build()
    );

    private static RpcRequestPool RPC_REQUEST_POOL;
    private static ServiceRouteCache SERVICE_ROUTE_CACHE;
    private static String CLUSTER_STRATEGY;

    public static void startRpcRequestManager(RpcRequestPool rpcRequestPool,
                                              ServiceRouteCache serviceRouteCache) {
        RPC_REQUEST_POOL = rpcRequestPool;
        SERVICE_ROUTE_CACHE = serviceRouteCache;
        CLUSTER_STRATEGY = SpringBeanFactory.getBean(RpcClientConfiguration.class).
                getRpcClientClusterStrategy();
    }

    /**
     * 发送消息
     * @param rpcRequest
     */
    public static void sendRequest(RpcRequest rpcRequest) throws InterruptedException {
        ClusterStrategy strategy = ClusterEngine.queryClusterStrategy(CLUSTER_STRATEGY);
        List<ProviderService> providerServices =
                SERVICE_ROUTE_CACHE.getServiceRoutes(rpcRequest.getClassName());
        // 选取服务提供者
        ProviderService targetServiceProvider = strategy.select(providerServices);

        if (targetServiceProvider != null) {
            String requestId = rpcRequest.getRequestId();
            CountDownLatch latch = new CountDownLatch(1);

            REQUEST_EXECUTOR.execute(new RpcClientConnector(requestId, targetServiceProvider, latch));

            latch.await();

            // 保存通道信息ChannelHolder到内部，直到请求完成，再进行通道信息销毁
            ChannelHolder channelHolder = channelHolderMap.get(requestId);
            channelHolder.getChannel().writeAndFlush(rpcRequest);

            LOGGER.info("Send request[{}:{}] to service provider successfully",
                    requestId, rpcRequest.toString());
        } else {
            throw new RpcException(StatusCode.NO_AVAILABLE_SERVICE_PROVIDER);
        }
    }

    /**
     * 注册channelHolder
     * @param requestId
     * @param channelHolder
     */
    public static void registerChannelHolder(String requestId, ChannelHolder channelHolder) {
        if (StringUtils.isBlank(requestId) || channelHolder == null) {
            return;
        }

        channelHolderMap.put(requestId, channelHolder);
        LOGGER.info("Register ChannelHolder[{}:{}] successfully", requestId, channelHolder.toString());

        RPC_REQUEST_POOL.submitRequest(requestId, channelHolder.getChannel().eventLoop());
        LOGGER.info("Submit request into RpcRequestPool successfully");
    }

    /**
     * 关闭ChannelHolder
     * @param requestId
     */
    public static void destroyChannelHolder(String requestId) {
        if (StringUtils.isBlank(requestId)) {
            return;
        }

        ChannelHolder channelHolder = channelHolderMap.remove(requestId);

        try {
            channelHolder.getChannel().closeFuture();
            channelHolder.getEventLoopGroup().shutdownGracefully();
        } catch (Exception e) {
            LOGGER.error("Close ChannelHolder[{}] error", requestId);
        }
        LOGGER.info("Destroy ChannelHolder[{}] successfully", requestId);
    }
}
