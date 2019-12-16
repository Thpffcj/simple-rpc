package cn.edu.nju.acceptor.init;

import cn.edu.nju.acceptor.handler.RpcRequestHandler;
import cn.edu.nju.acceptor.handler.RpcServerDecodeHandler;
import cn.edu.nju.acceptor.handler.RpcServerEncodeHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by thpffcj on 2019/12/15.
 */
@Component
@ChannelHandler.Sharable
public class RpcServerInitializer extends ChannelInitializer<Channel> {

    @Autowired
    private RpcRequestHandler rpcRequestHandler;

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                // 配置入站、出站事件channel
                .addLast(new RpcServerDecodeHandler())
                .addLast(new RpcServerEncodeHandler())
                .addLast(rpcRequestHandler);
    }
}
