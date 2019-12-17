package cn.edu.nju.service.api;

import cn.edu.nju.api.annotation.RpcClient;

/**
 * Created by thpffcj on 2019/12/13.
 */
@RpcClient
public interface ChatService {

    String send();

    String send(String userName, String message);

    String sendWithError(String message);
}
