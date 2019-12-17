package cn.edu.nju.service.consumer.controller;

import cn.edu.nju.client.util.SpringBeanFactory;
import cn.edu.nju.service.api.ChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by thpffcj on 2019/12/16.
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/testservice")
    public void test() {

        ChatService chatService = SpringBeanFactory.getBean(ChatService.class);

        System.out.println(chatService.send());
        System.out.println(chatService.send("thpffcj", "Happy Spring Festival!"));

        try {
            chatService.sendWithError("No message");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
