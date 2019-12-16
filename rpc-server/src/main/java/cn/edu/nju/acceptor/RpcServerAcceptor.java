package cn.edu.nju.acceptor;

import cn.edu.nju.acceptor.init.RpcServerInitializer;
import cn.edu.nju.config.RpcServerConfiguration;
import cn.edu.nju.util.SpringBeanFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by thpffcj on 2019/12/15.
 */
public class RpcServerAcceptor implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerAcceptor.class);

    // 创建mainReactor
    private EventLoopGroup boss = new NioEventLoopGroup();
    // 创建工作线程组
    private EventLoopGroup worker = new NioEventLoopGroup();

    private RpcServerConfiguration rpcServerConfiguration;
    private RpcServerInitializer rpcServerInitializer;

    public RpcServerAcceptor() {
        this.rpcServerConfiguration = SpringBeanFactory.getBean(RpcServerConfiguration.class);
        this.rpcServerInitializer = SpringBeanFactory.getBean(RpcServerInitializer.class);
    }

    @Override
    public void run() {
        // Bootstrap 意思是引导，一个 Netty 应用通常由一个 Bootstrap 开始，主要作用是配置整个 Netty 程序，串联各个组件，
        // Netty 中 Bootstrap 类是客户端程序的启动引导类，ServerBootstrap 是服务端启动引导类。
        ServerBootstrap bootstrap = new ServerBootstrap();

        // 组装NioEventLoopGroup
        bootstrap.group(boss, worker)
                // 设置channel类型为NIO类型
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                // 设置连接配置参数
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                // 配置入站、出站事件handler
                .childHandler(rpcServerInitializer);

        try {
            LOGGER.info("RpcServer acceptor startup at port[{}] successfully", rpcServerConfiguration.getNetworkPort());

            // 绑定端口
            ChannelFuture future = bootstrap.bind(rpcServerConfiguration.getNetworkPort()).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error("RpcServer acceptor startup failure!", e);
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully().syncUninterruptibly();
            worker.shutdownGracefully().syncUninterruptibly();
        }
    }
}
