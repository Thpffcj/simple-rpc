package cn.edu.nju.acceptor.handler;

import cn.edu.nju.common.bean.RpcRequest;
import cn.edu.nju.common.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by thpffcj on 2019/12/14.
 */
public class ZnsServerDecodeHandler extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        if (byteBuf.readableBytes() <= 4) {
            return;
        }

        int length = byteBuf.readInt();
        byteBuf.markReaderIndex();
        if (byteBuf.readableBytes() < length) {
            byteBuf.resetReaderIndex();
        } else {
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);

            RpcRequest rpcRequest = SerializationUtil.deserialize(bytes, RpcRequest.class);
            list.add(rpcRequest);
        }
    }
}
