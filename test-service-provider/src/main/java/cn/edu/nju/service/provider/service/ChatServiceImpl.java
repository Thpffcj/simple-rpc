package cn.edu.nju.service.provider.service;

import cn.edu.nju.api.annotation.RpcService;
import cn.edu.nju.common.exception.RpcException;
import cn.edu.nju.service.api.ChatService;

/**
 * Created by thpffcj on 2019/12/16.
 */
@RpcService(cls = ChatService.class)
public class ChatServiceImpl implements ChatService {

    @Override
    public String send() {
        return "Nobody send message!";
    }

    @Override
    public String send(String userName, String message) {
        return String.format("[%s] : %s", userName, message);
    }

    @Override
    public String sendWithError(String message) {
        throw new RpcException("test error! " + message);
    }
}
