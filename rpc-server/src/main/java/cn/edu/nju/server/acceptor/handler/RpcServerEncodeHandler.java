package cn.edu.nju.server.acceptor.handler;

import cn.edu.nju.common.bean.RpcResponse;
import cn.edu.nju.common.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by thpffcj on 2019/12/14.
 *
 * 何为编解码，通俗的来说，我们需要将一串文本信息从A发送到B并且将这段文本进行加工处理，如：A将信息文本信息编码为2进制信息进行传输。
 * B接受到的消息是一串2进制信息，需要将其解码为文本信息才能正常进行处理。
 */
public class RpcServerEncodeHandler extends MessageToByteEncoder<RpcResponse> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse, ByteBuf byteBuf) throws Exception {

        byte[] bytes = SerializationUtil.serialize(rpcResponse);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
