package cn.edu.nju.client.util;

import java.util.UUID;

/**
 * Created by thpffcj on 2019/12/14.
 */
public class RequestIdUtil {

    public static String requestId() {
        return UUID.randomUUID().toString();
    }
}
