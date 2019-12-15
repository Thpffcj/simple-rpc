package cn.edu.nju.common.exception;

import cn.edu.nju.common.constant.StatusCode;

/**
 * Created by thpffcj on 2019/12/14.
 */
public class RpcException extends RuntimeException {

    public RpcException() {

    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(StatusCode statusCode) {
        super(statusCode.getDescription());
    }
}
