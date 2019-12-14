package cn.edu.nju.common.util;

import cn.edu.nju.common.serialize.ISerializer;
import cn.edu.nju.common.serialize.impl.ProtocolStuffSerializer;

/**
 * Created by thpffcj on 2019/12/14.
 */
public class SerializationUtil {

    private static final ISerializer serializer = new ProtocolStuffSerializer();

    public static <T> byte[] serialize(T t) {
        return serializer.serialize(t);
    }

    public static <T> T deserialize(byte[] bytes, Class<T> cls) {
        return serializer.deserialize(bytes, cls);
    }
}
