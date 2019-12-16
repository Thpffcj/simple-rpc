package cn.edu.nju.test.netty.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Created by thpffcj on 2019/12/15.
 *
 * 用一句简单的话来说就是：Netty封装了JDK的NIO，让你用得更爽，你不用再写一大堆复杂的代码了。
 * 用官方正式的话来说就是：Netty是一个异步事件驱动的网络应用框架，用于快速开发可维护的高性能服务器和客户端。
 *
 * 使用netty而不是nio：
 * 使用JDK自带的NIO需要了解太多的概念，编程复杂，一不小心bug横飞
 * Netty底层IO模型随意切换，而这一切只需要做微小的改动，改改参数，Netty可以直接从NIO模型变身为IO模型
 * Netty自带的拆包解包，异常检测等机制让你从NIO的繁重细节中脱离出来，让你只需要关心业务逻辑
 * Netty解决了JDK的很多包括空轮询在内的bug
 * Netty底层对线程，selector做了很多细小的优化，精心设计的reactor线程模型做到非常高效的并发处理
 * 自带各种协议栈让你处理任何一种通用协议都几乎不用亲自动手
 * Netty社区活跃，遇到问题随时邮件列表或者issue
 * Netty已经历各大rpc框架，消息中间件，分布式通信中间件线上的广泛验证，健壮性无比强大
 */
public class NettyServer {

    /**
     * 这么一小段代码就实现了我们前面NIO编程中的所有的功能，包括服务端启动，接受新连接，打印客户端传来的数据，怎么样，是不是比JDK原生的NIO
     * 编程优雅许多？
     *
     * 初学Netty的时候，由于大部分人对NIO编程缺乏经验，因此，将Netty里面的概念与IO模型结合起来可能更好理解
     * 1.boos对应，IOServer.java中的接受新连接线程，主要负责创建新连接
     * 2.worker对应 IOClient.java中的负责读取数据的线程，主要用于读取数据以及业务逻辑处理
     *
     * 基本过程描述如下：
     * 1）初始化创建 2 个 NioEventLoopGroup：其中 boosGroup 用于 Accetpt 连接建立事件并分发请求，workerGroup 用于处理 I/O 读写
     * 事件和业务逻辑。
     * 2）基于 ServerBootstrap(服务端启动引导类)：配置 EventLoopGroup、Channel 类型，连接参数、配置入站、出站事件 handler。
     * 3）绑定端口：开始工作。
     */
    public static void main(String[] args) {

        /**
         * Bootstrap 意思是引导，一个 Netty 应用通常由一个 Bootstrap 开始，主要作用是配置整个 Netty 程序，串联各个组件，Netty 中
         * Bootstrap 类是客户端程序的启动引导类，ServerBootstrap 是服务端启动引导类。
         */
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        /**
         * 创建mainReactor
         *
         * 每个 Boss NioEventLoop 循环执行的任务包含 3 步：
         * 1）轮询 Accept 事件；
         * 2）处理 Accept I/O 事件，与 Client 建立连接，生成 NioSocketChannel，并将 NioSocketChannel 注册到某个
         * Worker NioEventLoop 的 Selector 上；
         * 3）处理任务队列中的任务，runAllTasks。任务队列中的任务包括用户调用 eventloop.execute 或 schedule 执行的任务，或者其他
         * 线程提交到该 eventloop 的任务。
         */
        NioEventLoopGroup boos = new NioEventLoopGroup();

        /**
         * 创建工作线程组
         *
         * 每个 Worker NioEventLoop 循环执行的任务包含 3 步：
         * 1）轮询 Read、Write 事件；
         * 2）处理 I/O 事件，即 Read、Write 事件，在 NioSocketChannel 可读、可写事件发生时进行处理；
         * 3）处理任务队列中的任务，runAllTasks。
         */
        NioEventLoopGroup worker = new NioEventLoopGroup();

        serverBootstrap
                // 组装NioEventLoopGroup
                .group(boos, worker)
                // 设置channel类型为NIO类型
                .channel(NioServerSocketChannel.class)
                // 配置入站、出站事件handler
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) {
                        // 配置入站、出站事件channel
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) {
                                System.out.println(msg);
                            }
                        });
                    }
                })
                .bind(8000);
    }
}
