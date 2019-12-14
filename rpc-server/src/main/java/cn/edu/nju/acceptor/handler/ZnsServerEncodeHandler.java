package cn.edu.nju.acceptor.handler;

import cn.edu.nju.common.bean.RpcResponse;
import cn.edu.nju.common.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by thpffcj on 2019/12/14.
 */
public class ZnsServerEncodeHandler extends MessageToByteEncoder<RpcResponse> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse, ByteBuf byteBuf) throws Exception {

        byte[] bytes = SerializationUtil.serialize(rpcResponse);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
