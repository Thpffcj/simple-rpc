package cn.edu.nju.test.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by thpffcj on 2019/12/15.
 *
 * 1. 线程资源受限：
 * NIO编程模型中，新来一个连接不再创建一个新的线程，而是可以把这条连接直接绑定到某个固定的线程，然后这条连接所有的读写都由这个线程来负责。
 *
 * IO模型中，一个连接来了，会创建一个线程，对应一个while死循环，死循环的目的就是不断监测这条连接上是否有数据可以读，大多数情况下，1w个连
 * 接里面同一时刻只有少量的连接有数据可读，因此，很多个while死循环都白白浪费掉了，因为读不出啥数据。
 * 而在NIO模型中，他把这么多while死循环变成一个死循环，这个死循环由一个线程控制，那么他又是如何做到一个线程，一个while死循环就能监测1w个
 * 连接是否有数据可读的呢？
 *
 * 这就是NIO模型中selector的作用，一条连接来了之后，现在不创建一个while死循环去监听是否有数据可读了，而是直接把这条连接注册到selector上，
 * 然后，通过检查这个selector，就可以批量监测出有数据可读的连接，进而读取数据。
 *
 * 2. 线程切换效率低下：
 * 由于NIO模型中线程数量大大降低，线程切换效率因此也大幅度提高
 *
 * 3. IO读写以字节为单位：
 * NIO解决这个问题的方式是数据读写不再以字节为单位，而是以字节块为单位。IO模型中，每次都是从操作系统底层一个字节一个字节地读取数据，
 * 而NIO维护一个缓冲区，每次可以从这个缓冲区里面读取一块的数据。
 *
 *
 */
public class NIOServer {

    /**
     * 使用JDK原生NIO的API实现一个简单的服务端通信程序是如此复杂
     *
     * NIO模型中通常会有两个线程，每个线程绑定一个轮询器selector
     * 在我们这个例子中serverSelector负责轮询是否有新的连接，clientSelector负责轮询连接是否有数据可读
     *
     * 服务端监测到新的连接之后，不再创建一个新的线程，而是直接将新连接绑定到clientSelector上，这样就不用IO模型中1w个while循环在死等，
     * 参见(1)
     *
     * clientSelector被一个while死循环包裹着，如果在某一时刻有多条连接有数据可读，那么通过 clientSelector.select(1)方法可以轮询出
     * 来，进而批量处理，参见(2)
     *
     * 数据的读写以内存块为单位，参见(3)
     *
     * 缺点：
     * 1、JDK的NIO编程需要了解很多的概念，编程复杂，对NIO入门非常不友好，编程模型不友好，ByteBuffer的api简直反人类
     * 2、对NIO编程来说，一个比较合适的线程模型能充分发挥它的优势，而JDK没有给你实现，你需要自己实现，就连简单的自定义协议拆包都要你自己实现
     * 3、JDK的NIO底层由epoll实现，该实现饱受诟病的空轮训bug会导致cpu飙升100%
     * 4、项目庞大之后，自行实现的NIO很容易出现各类bug，维护成本较高，上面这一坨代码我都不能保证没有bug
     */
    public static void main(String[] args) throws IOException {

        // 轮询是否有新的连接
        Selector serverSelector = Selector.open();
        // 轮询连接是否有数据可读
        Selector clientSelector = Selector.open();

        new Thread(() -> {
            try {
                // 对应IO编程中服务端启动
                ServerSocketChannel listenerChannel = ServerSocketChannel.open();
                listenerChannel.socket().bind(new InetSocketAddress(8000));
                listenerChannel.configureBlocking(false);
                listenerChannel.register(serverSelector, SelectionKey.OP_ACCEPT);

                while (true) {
                    // 监测是否有新的连接，这里的1指的是阻塞的时间为1ms
                    if (serverSelector.select(1) > 0) {
                        Set<SelectionKey> set = serverSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = set.iterator();

                        while (keyIterator.hasNext()) {
                            SelectionKey key = keyIterator.next();

                            if (key.isAcceptable()) {
                                try {
                                    // (1) 每来一个新连接，不需要创建一个线程，而是直接注册到clientSelector
                                    SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
                                    clientChannel.configureBlocking(false);
                                    clientChannel.register(clientSelector, SelectionKey.OP_READ);
                                } finally {
                                    keyIterator.remove();
                                }
                            }

                        }
                    }
                }
            } catch (IOException ignored) {
            }
        }).start();


        new Thread(() -> {
            try {
                while (true) {
                    // (2) 批量轮询是否有哪些连接有数据可读，这里的1指的是阻塞的时间为1ms
                    if (clientSelector.select(1) > 0) {
                        Set<SelectionKey> set = clientSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = set.iterator();

                        while (keyIterator.hasNext()) {
                            SelectionKey key = keyIterator.next();

                            if (key.isReadable()) {
                                try {
                                    SocketChannel clientChannel = (SocketChannel) key.channel();
                                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                                    // (3) 读取数据以块为单位批量读取
                                    clientChannel.read(byteBuffer);
                                    byteBuffer.flip();
                                    System.out.println(Charset.defaultCharset().newDecoder().decode(byteBuffer)
                                            .toString());
                                } finally {
                                    keyIterator.remove();
                                    key.interestOps(SelectionKey.OP_READ);
                                }
                            }
                        }
                    }
                }
            } catch (IOException ignored) {
            }
        }).start();
    }
}
