package cn.edu.nju.client.connector.init;

import cn.edu.nju.client.connector.handler.RpcClientDecodeHandler;
import cn.edu.nju.client.connector.handler.RpcClientEncodeHandler;
import cn.edu.nju.client.connector.handler.RpcResponseHandler;
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
public class RpcClientInitializer extends ChannelInitializer<Channel> {

    @Autowired
    private RpcResponseHandler rpcResponseHandler;

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                .addLast(new RpcClientEncodeHandler())
                .addLast(new RpcClientDecodeHandler())
                .addLast(rpcResponseHandler);
    }
}
