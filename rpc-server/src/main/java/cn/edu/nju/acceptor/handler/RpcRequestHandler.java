package cn.edu.nju.acceptor.handler;

import cn.edu.nju.common.bean.RpcRequest;
import cn.edu.nju.common.bean.RpcResponse;
import cn.edu.nju.util.SpringBeanFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Created by thpffcj on 2019/12/15.
 *
 * ChannelHandler 是一个接口，处理 I/O 事件或拦截 I/O 操作，并将其转发到其 ChannelPipeline(业务处理链)中的下一个处理程序。
 * 用于处理入站 I/O 事件
 */
@Component
@ChannelHandler.Sharable
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcRequestHandler.class);

    /**
     * channel读取数据
     *
     * @param ctx
     * @param rpcRequest
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {

        RpcResponse rpcResponse = new RpcResponse();
        rpcRequest.setRequestId(rpcRequest.getRequestId());

        String className = rpcRequest.getClassName();
        String methodName = rpcRequest.getMethodName();
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] parameters = rpcRequest.getParameters();

        try {
            // 反射调用本地服务
            Object targetClass = SpringBeanFactory.getBean(Class.forName(className));
            Method targetMethod = targetClass.getClass().getMethod(methodName, parameterTypes);
            Object result = targetMethod.invoke(targetClass, parameters);

            rpcResponse.setResult(result);
        } catch (Throwable cause) {
            rpcResponse.setCause(cause);
        }

        // 把响应刷到客户端
        ctx.writeAndFlush(rpcResponse);
    }
}
