package cn.edu.nju.client.connector.handler;

import cn.edu.nju.client.runner.RpcRequestPool;
import cn.edu.nju.common.bean.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by thpffcj on 2019/12/14.
 */
@Component
@ChannelHandler.Sharable
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {

    @Autowired
    private RpcRequestPool rpcRequestPool;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {
        rpcRequestPool.notifyRequest(rpcResponse.getRequestId(), rpcResponse);
    }
}
