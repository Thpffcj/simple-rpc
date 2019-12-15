package cn.edu.nju.client.runner;

import cn.edu.nju.client.bean.ChannelHolder;
import cn.edu.nju.common.bean.RpcRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Created by thpffcj on 2019/12/14.
 *
 * TODO
 */
public class RpcRequestManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcRequestManager.class);

    // requestId -> ChannelHolder
    private static final ConcurrentHashMap<String, ChannelHolder> channelHolderMap = new ConcurrentHashMap<>();

    private static final ExecutorService REQUEST_EXECUTOR = new ThreadPoolExecutor(
            30,
            100,
            0,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(30),
            new BasicThreadFactory.Builder().namingPattern("request-service-connector-%d").build()
    );

    public static void sendRequest(RpcRequest rpcRequest) {

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
