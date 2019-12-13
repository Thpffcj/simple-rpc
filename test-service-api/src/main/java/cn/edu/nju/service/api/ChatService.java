package cn.edu.nju.service.api;

import com.buildupchao.zns.api.annotation.ZnsClient;

/**
 * Created by thpffcj on 2019/12/13.
 */
@ZnsClient
public interface ChatService {

    String send();

    String send(String userName, String message);

    String sendWithError(String message);
}
