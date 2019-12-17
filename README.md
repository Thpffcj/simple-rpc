# 一个简单的RPC实现

## 1. 什么是 RPC 框架？

[谁能用通俗的语言解释一下什么是 RPC 框架？](https://www.zhihu.com/question/25536695)

- 为什么要RPC，RPC是指远程过程调用，也就是说两台服务器A，B，一个应用部署在A服务器上，想要调用B服务器上应用提供的函数/方法，由于不在一个内存空间，不能直接调用，需要通过网络来表达调用的语义和传达调用的数据

![](https://raw.githubusercontent.com/Thpffcj/simple-rpc/master/pic/RPC%E6%A1%86%E6%9E%B6.jpg)

1. Client以本地调用的方式调用服务
2. Client Stub接收到调用后，把服务调用相关信息组装成需要网络传输的消息体，并找到服务地址（host:port），对消息进行编码后交给Connector进行发送
3. Connector通过网络通道发送消息给Acceptor
4. Acceptor接收到消息后交给Server Stub
5. Server Stub对消息进行解码，并根据解码的结果通过反射调用本地服务
6. Server执行本地服务并返回结果给Server Stub
7. Server Stub对返回结果组装打包并编码后交给Acceptor进行发送
8. Acceptor通过网络通道发送消息给Connector
9. Connector接收到消息后交给Client Stub，Client Stub接收到消息并进行解码后转交给Client
10. Client获取到服务调用的最终结果

## 2. 技术架构

### 1. 远程过程调用RPC的实现原理：动态代理

**RPC的实现中一个重要的功能就是动态代理。**

- 首先，对于用户方而言：
  - 只提供接口不提供实现类，所有的接口文件都导出后，调用方放在一个专门目录下接口千奇百怪，但是都没有具体实现
  - 代理要代理掉专门目录下的所有接口，任何对这里接口的调用都被序列化后转到远端
- 具体实现RPC可如下:
  - 我们只需要接口，不需要知道任何实现类的信息就可以创建一个接口的代理实现
  - 因为所有的接口都在指定目录下，我们可以扫描该目录下的所有接口，批量生成所有接口的实例，并把生成的bean都放入spring中管理。这样，用户就可以用autowair注入所有实现。而实际上我们的代理proxy就成了所有接口的实现
  - 用户调用任何接口时，都调用了我们生成的bean实现。其实都进入了我们相同的handler实现，实现中我们可以知道用户想要调用的完整方法名称(从它的目录路径可以分析出目标应用名)，参数。然后序列化后去远端调用并返回结果即可


### 2. Netty

[新手入门：目前为止最透彻的的Netty高性能原理和框架架构解析](https://www.cnblogs.com/imstudy/p/9908791.html)

## 3. 参考

[结合Zookeeper实现简易RPC框架](https://www.jianshu.com/p/f684ef537ede)


