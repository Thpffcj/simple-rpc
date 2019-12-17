package cn.edu.nju.client.proxy;

import cn.edu.nju.client.runner.RpcRequestManager;
import cn.edu.nju.client.runner.RpcRequestPool;
import cn.edu.nju.client.util.RequestIdUtil;
import cn.edu.nju.common.bean.RpcRequest;
import cn.edu.nju.common.bean.RpcResponse;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Created by thpffcj on 2019/12/14.
 *
 * 创建服务代理
 *
 * CGLIB代理主要通过对字节码的操作，为对象引入间接级别，以控制对象的访问。我们知道Java中有一个动态代理也是做这个事情的，那我们为什
 * 么不直接使用Java动态代理，而要使用CGLIB呢？答案是CGLIB相比于JDK动态代理更加强大，JDK动态代理虽然简单易用，但是其有一个致命缺
 * 陷是，只能对接口进行代理。如果要代理的类为一个普通类、没有接口，那么Java动态代理就没法使用了。
 *
 * Enhancer可能是CGLIB中最常用的一个类，和Java1.3动态代理中引入的Proxy类差不多。和Proxy不同的是，Enhancer既能够代理普通的
 * class，也能够代理接口。Enhancer创建一个被代理对象的子类并且拦截所有的方法调用（包括从Object中继承的toString和hashCode方法）。
 * Enhancer不能够拦截final方法，例如Object.getClass()方法，这是由于Java final方法语义决定的。基于同样的道理，Enhancer也不
 * 能对fianl类进行代理操作。这也是Hibernate为什么不能持久化final class的原因。
 *
 * 使用过程：
 * 1. 生成一个代理对象
 * 2. 告诉代理对象，它的实现在哪里
 * 3. 告诉代理对象，它的父类是谁
 * 4. 生成代理类的对象
 * 5. 调用代理对象的方法
 */
@Component
public class ProxyHelper {

    @Autowired
    private RpcRequestPool rpcRequestPool;

    /**
     * 返回创建的代理对象
     * @param cls
     * @param <T>
     * @return
     */
    public <T> T newProxyInstance(Class<T> cls) {
        // 通过cglib动态代理获取代理对象的过程
        Enhancer enhancer = new Enhancer();
        // 设置enhancer对象的父类
        enhancer.setSuperclass(cls);
        // 设置enhancer对应的实现对象(回调对象)
        enhancer.setCallback(new ProxyCallBackHandler());
        return (T) enhancer.create();
    }

    class ProxyCallBackHandler implements MethodInterceptor {

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy)
                throws Throwable {
            return doIntercept(method, objects);
        }

        private Object doIntercept(Method method, Object[] parameters) throws Throwable {
            String requestId = RequestIdUtil.requestId();
            String className = method.getDeclaringClass().getName();
            String methodName = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();

            // 构造rpcRequest
            RpcRequest rpcRequest = RpcRequest.builder()
                    .requestId(requestId)
                    .className(className)
                    .methodName(methodName)
                    .parameterTypes(parameterTypes)
                    .parameters(parameters)
                    .build();

            RpcRequestManager.sendRequest(rpcRequest);
            RpcResponse rpcResponse = rpcRequestPool.fetchResponse(requestId);

            if (rpcResponse == null) {
                return null;
            }

            if (rpcResponse.isError()) {
                throw rpcResponse.getCause();
            }
            return rpcResponse.getResult();
        }
    }
}
