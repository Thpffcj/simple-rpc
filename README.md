# 一个简单的RPC实现

- 博客地址：**[实现一个简单的RPC框架](http://www.thpffcj.com/2019/12/19/Implementing-A-Simple-RPC-Framework/)**
- 写这个项目主要是为了更好的了解一些框架底层的通信机制，以前对这部分并不了解，但是关于自己实现RPC有很多参考资料，本项目主要仿写 **[设计一个分布式RPC框架](http://www.buildupchao.cn/arch/2019/02/01/design-a-distributed-RPC-structure.html)**

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

- RPC服务通讯中间件的需求，主要设定了功能和性能两方面的内容。总的来说，实现一个高性能、低延迟的RPC服务中间件， 将客户端的业务处理请求，相对均衡地分配到后端服务执行
- RPC服务中间件的通讯模块，主要包括客户端代理和服务端I/O通讯模块两部分组成，实现分布式服务的透明调用。
- 客户端代理中间件模块的主要功能包括：
  - 接收业务请求并组成RPC请求消息，并发送到远端服务
  - 监听Socket连接获取服务端回复消息，从回复的RPC消息提取业务对象，返回给业务模块
  - 服务的发现。客户端可以动态的获取到指定服务实例的地址
  - 服务Socket连接的动态维护。如运行过程中关闭异常连接、新增连接等


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

- [《跟闪电侠学Netty》开篇：Netty是什么？](https://www.jianshu.com/p/a4e03835921a)
- [新手入门：目前为止最透彻的的Netty高性能原理和框架架构解析](https://www.cnblogs.com/imstudy/p/9908791.html)

### 3. 技术选型

**技术原理**

- 定义RPC请求消息、应答消息结构，里面要包括RPC的接口定义模块、包括远程调用的类名、方法名称、参数结构、参数值等信息
- 服务端初始化的时候通过容器加载RPC接口定义和RPC接口实现类对象的映射关系，然后等待客户端发起调用请求
- 客户端发起的RPC消息里面包含，远程调用的类名、方法名称、参数结构、参数值等信息，通过网络，以字节流的方式送给RPC服务端，RPC服务端接收到字节流的请求之后，去对应的容器里面，查找客户端接口映射的具体实现对象
- RPC服务端找到实现对象的参数信息，通过反射机制创建该对象的实例，并返回调用处理结果，最后封装成RPC应答消息通知到客户端
- 客户端通过网络，收到字节流形式的RPC应答消息，进行拆包、解析之后，显示远程调用结果。

**序列化/反序列化**

- 基于Java原生对象序列化机制的编码、解码器（ObjectEncoder、ObjectDecoder）进行实现的。当然出于性能考虑，这个可能不是最优的方案。更优的方案是把消息的编码、解码器，搞成可以配置实现的。我们使用protobuf进行解码和编码，以提高网络消息的传输效率

**配置管理**

- RPC服务端的服务接口对象和服务接口实现对象要能轻易的配置，轻松进行加载、卸载。在这里，我们通过Spring容器进行统一的对象管理

**通信技术**

- 采用业界主流的NIO框架进行服务器后端开发。主流的NIO框架主要有Netty、Mina。它们主要都是基于TCP通信，非阻塞的IO、灵活的IO线程池而设计的，应对高并发请求也是绰绰有余。我们使用Netty

**服务注册与发现**

- Zookeeper，最近对Zookeeper很感兴趣，Zookeeper在很多地方都用作服务注册与发现的工具
  - 当Server启动后，自动注册服务信息（包括host，port，还有nettyPort）到ZK中
  - 当Client启动后，自动订阅获取需要远程调用的服务信息列表到本地缓存中


## 3. 使用

### 1. 服务提供者

- 添加rpc-server依赖包

```
<dependency>
    <groupId>cn.edu.nju</groupId>
    <artifactId>test-service-api</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>

<dependency>
    <groupId>cn.edu.nju</groupId>
    <artifactId>rpc-server</artifactId>
    <version>1.0-SNAPSHOT</version>
    <exclusions>
        <exclusion>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

- 添加扫描包路径RpcServerPackage.class以及启动调用方法rpcServerRunner.run()

```
@ComponentScan(
        basePackages = "cn.edu.nju.service.provider",
        basePackageClasses = RpcServerPackage.class
)
@SpringBootApplication
public class RpcServiceProviderApplication implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServiceProviderApplication.class);

    @Autowired
    private RpcServerRunner rpcServerRunner;

    public static void main(String[] args) {
        SpringApplication.run(RpcServiceProviderApplication.class, args);
        LOGGER.info("Rpc service provider application startup successfully");
    }

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        rpcServerRunner.run();
    }
}
```

- 在application.yml中配置属性

```
logging:
  config: classpath:logback-spring.xml
server:
  port: 8082
spring:
  application:
    name: rpc-service-provider

rpc:
  server:
      zk:
        root: /rpc
        addr: localhost:2181
        switch: true
  network:
      port: 8888
```

### 2. 服务调用者

- 添加rpc-client依赖包

```
<dependency>
    <groupId>cn.edu.nju</groupId>
    <artifactId>test-service-api</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>

<dependency>
    <groupId>cn.edu.nju</groupId>
    <artifactId>rpc-client</artifactId>
    <version>1.0-SNAPSHOT</version>
    <exclusions>
        <exclusion>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

- 添加扫描包路径RpcClientPackage.class以及启动调用方法rpcClientRunnerr.run()

```
@ComponentScan(
        basePackages = "cn.edu.nju.service.consumer",
        basePackageClasses = RpcClientPackage.class
)
@SpringBootApplication
public class RpcServiceConsumerApplication implements ApplicationRunner {

    @Autowired
    private RpcClientRunner rpcClientRunner;

    public static void main(String[] args) {
        SpringApplication.run(RpcServiceConsumerApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        rpcClientRunner.run();
    }
}
```

- 在application.yml中配置属性

```
logging:
  config: classpath:logback-spring.xml
server:
  port: 8081
spring:
  application:
    name: rpc-service-consumer

rpc:
  client:
      zk:
        root: /rpc
        addr: 172.19.240.128:2181
        switch: true
      api:
        package: cn.edu.nju.service.api
  cluster:
        strategy: Random
```

### 3. 公共API test-service-api需要引入rpc-api包依赖

```
<dependency>
    <groupId>cn.edu.nju</groupId>
    <artifactId>rpc-api</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```


## 4. 参考

- [设计一个分布式RPC框架](http://www.buildupchao.cn/arch/2019/02/01/design-a-distributed-RPC-structure.html)
- [结合Zookeeper实现简易RPC框架](https://www.jianshu.com/p/f684ef537ede)
- [谈谈如何使用Netty开发实现高性能的RPC服务器](https://www.cnblogs.com/jietang/p/5615681.html)
- [一个轻量级分布式RPC框架--NettyRpc](https://www.cnblogs.com/luxiaoxun/p/5272384.html)




