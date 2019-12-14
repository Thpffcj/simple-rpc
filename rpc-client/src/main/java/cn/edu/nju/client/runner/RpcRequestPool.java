package cn.edu.nju.client.runner;

import cn.edu.nju.common.bean.RpcResponse;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by thpffcj on 2019/12/14.
 */
@Component
public class RpcRequestPool {

    /**
     * Netty中所有的IO操作都是异步的，所有的IO调用会立即返回，但是并不保证调用结束时该IO请求已经完成。而调用后立即返回
     * 的对象在Netty中就是一个ChannelFuture。
     *
     * Netty中除了重新创建了一个Future接口外，还创建了一个Promise接口，而Promise接口是继承自Future的。Promise增强了
     * Future的功能，通过Promise可以为Future设置结果，包括成功时的结果或者失败时的原因。简单的说Netty中Future是
     * read-only的，而Promise则是writable的。
     */
    private final ConcurrentHashMap<String, Promise<RpcResponse>> requestPool = new ConcurrentHashMap<>();

    public void submitRequest(String requestId, EventExecutor executor) {
        requestPool.put(requestId, new DefaultPromise<>(executor));
    }

    public RpcResponse fetchResponse(String requestId) throws Exception {
        Promise<RpcResponse> promise = requestPool.get(requestId);
        if (promise == null) {
            return null;
        }

        RpcResponse rpcResponse = promise.get(10, TimeUnit.SECONDS);
        requestPool.remove(requestId);

        return rpcResponse;
    }
}
