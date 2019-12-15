package cn.edu.nju.client.connector.handler;

import cn.edu.nju.common.bean.RpcResponse;
import cn.edu.nju.common.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by thpffcj on 2019/12/14.
 */
public class RpcClientDecodeHandler extends ByteToMessageDecoder {

    private static final int HEAD_LENGTH = 4;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        if (byteBuf.readableBytes() <= HEAD_LENGTH) {
            return;
        }

        // 我们标记一下当前的readIndex的位置
        byteBuf.markReaderIndex();
        int length = byteBuf.readInt();

        // 读到的消息体长度如果小于我们传送过来的消息长度，则resetReaderIndex
        if (byteBuf.readableBytes() < length) {
            byteBuf.resetReaderIndex();
        } else {
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);

            RpcResponse rpcResponse = SerializationUtil.deserialize(bytes, RpcResponse.class);
            list.add(rpcResponse);
        }
    }
}
