package cn.edu.nju.common.serialize.impl;

import cn.edu.nju.common.serialize.ISerializer;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by thpffcj on 2019/12/14.
 *
 * Protocol Buffer是谷歌出品的一种数据交换格式，独立于语言和平台，类似于json。Google提供了多种语言的实现：java、c++、go和python。
 * 对象序列化成Protocol Buffer之后可读性差，但是相比xml，json，它占用小，速度快。适合做数据存储或 RPC 数据交换格式。
 *
 * 但是protobuf需要编写.proto文件，再通过protobuf转换成对应的java代码，非常不好维护。
 * protostuff就是为了解决这个痛点而产生的。通过protostuff，不需要编写.proto文件，只需要编写普通的java bean就可以使用protobuf的
 * 序列化／反序列化。
 */
public class ProtocolStuffSerializer implements ISerializer {

    // 缓存Schema
    private static Map<Class<?>, Schema<?>> classSchemaMap = new ConcurrentHashMap<>();
    private static Objenesis objenesis = new ObjenesisStd(true);

    /**
     * 序列化方法，把指定对象序列化成字节数组
     * @param t
     * @param <T>
     * @return
     */
    @Override
    public <T> byte[] serialize(T t) {
        Class<T> cls = (Class<T>) t.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

        try {
            Schema<T> schema = getClassSchema(cls);
            return ProtobufIOUtil.toByteArray(t, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    /**
     * 反序列化方法，将字节数组反序列化成指定Class类型
     * @param bytes
     * @param cls
     * @param <T>
     * @return
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> cls) {
        try {
            Schema<T> schema = getClassSchema(cls);
            T message = objenesis.newInstance(cls);
            ProtobufIOUtil.mergeFrom(bytes, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private <T> Schema<T> getClassSchema(Class<T> cls) {
        Schema<T> classSchema = null;
        if (classSchemaMap.containsKey(cls)) {
            classSchema = (Schema<T>) classSchemaMap.get(cls);
        } else {
            // 这个schema通过RuntimeSchema进行懒创建并缓存
            // 所以可以一直调用RuntimeSchema.getSchema()，这个方法是线程安全的
            classSchema = RuntimeSchema.getSchema(cls);
            if (classSchema != null) {
                classSchemaMap.put(cls, classSchema);
            }
        }
        return classSchema;
    }
}
