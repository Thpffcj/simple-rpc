package cn.edu.nju.common.serialize;

/**
 * Created by thpffcj on 2019/12/14.
 */
public interface ISerializer {

    <T> byte[] serialize(T t);

    <T> T deserialize(byte[] bytes, Class<T> cls);
}
