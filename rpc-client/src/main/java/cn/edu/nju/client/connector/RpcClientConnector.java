package cn.edu.nju.client.connector;

import cn.edu.nju.client.bean.ChannelHolder;
import cn.edu.nju.client.bean.ProviderService;
import cn.edu.nju.client.connector.init.RpcClientInitializer;
import cn.edu.nju.client.runner.RpcRequestManager;
import cn.edu.nju.client.util.SpringBeanFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * Created by thpffcj on 2019/12/15.
 *
 * 当Client端发起一个远程服务调用时，ZnsRequestManager将会启动一个Connector与Acceptor进行连接，同时会保存通道信
 * 息ChannelHolder到内部，直到请求完成，再进行通道信息销毁。
 */
public class RpcClientConnector implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClientConnector.class);

    private String requestId;
    private ProviderService providerService;
    private CountDownLatch latch;
    private RpcClientInitializer rpcClientInitializer;

    public RpcClientConnector(String requestId, ProviderService providerService, CountDownLatch latch) {
        this.requestId = requestId;
        this.providerService = providerService;
        this.latch = latch;
        this.rpcClientInitializer = SpringBeanFactory.getBean(RpcClientInitializer.class);
    }

    @Override
    public void run() {

        // TODO
        EventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .remoteAddress(providerService.getServerIp(), providerService.getNetworkPort())
                .handler(rpcClientInitializer);

        try {
            /**
             * 异步的概念和同步相对。当一个异步过程调用发出后，调用者不能立刻得到结果。实际处理这个调用的部件在完成后，通过状态、
             * 通知和回调来通知调用者。
             * Netty 中的 I/O 操作是异步的，包括 Bind、Write、Connect 等操作会简单的返回一个 ChannelFuture。
             * 调用者并不能立刻获得结果，而是通过 Future-Listener 机制，用户可以方便的主动获取或者通过通知机制获得 IO 操作结果。
             * 当 Future 对象刚刚创建时，处于非完成状态，调用者可以通过返回的 ChannelFuture 来获取操作执行的状态，注册监听函
             * 数来执行完成后的操作。
             *
             * 常见有如下操作：
             * 1）通过 isDone 方法来判断当前操作是否完成；
             * 2）通过 isSuccess 方法来判断已完成的当前操作是否成功；
             * 3）通过 getCause 方法来获取已完成的当前操作失败的原因；
             * 4）通过 isCancelled 方法来判断已完成的当前操作是否被取消；
             * 5）通过 addListener 方法来注册监听器，当操作已完成(isDone 方法返回完成)，将会通知指定的监听器；如果 Future
             * 对象已完成，则理解通知指定的监听器。
             */
            ChannelFuture future = bootstrap.connect().sync();

            if (future.isSuccess()) {
                ChannelHolder channelHolder = ChannelHolder.builder()
                        .channel(future.channel())
                        .eventLoopGroup(worker)
                        .build();

                // 注册ChannelHolder
                RpcRequestManager.registerChannelHolder(requestId, channelHolder);

                LOGGER.info("Construct a connector with service provider[{}:{}] successfully",
                        providerService.getServerIp(),
                        providerService.getNetworkPort()
                );

                latch.countDown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
