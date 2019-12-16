package cn.edu.nju.client.connector.handler;

import cn.edu.nju.common.bean.RpcRequest;
import cn.edu.nju.common.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by thpffcj on 2019/12/14.
 */
public class RpcClientEncodeHandler extends MessageToByteEncoder<RpcRequest> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          RpcRequest rpcRequest, ByteBuf byteBuf) throws Exception {

        byte[] bytes = SerializationUtil.serialize(rpcRequest);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
