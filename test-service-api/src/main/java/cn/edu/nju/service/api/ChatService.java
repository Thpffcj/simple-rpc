package cn.edu.nju.service.api;

import cn.edu.nju.api.annotation.RpcClient;

/**
 * Created by thpffcj on 2019/12/13.
 *
 * 我们只需要接口，不需要知道任何实现类的信息就可以创建一个接口的代理实现
 *
 * 因为所有的接口都在指定目录下，我们可以扫描该目录下的所有接口，批量生成所有接口的实例，并把生成的bean都放入spring中管理。
 * 这样，用户就可以用autowair注入所有实现。而实际上我们的代理proxy就成了所有接口的实现
 *
 * 用户调用任何接口时，都调用了我们生成的bean实现。其实都进入了我们相同的handler实现，实现中我们可以知道用户想要调用的完整方法
 * 名称(从它的目录路径可以分析出目标应用名)、参数。然后序列化后去远端调用并返回结果即可
 */
@RpcClient
public interface ChatService {

    String send();

    String send(String userName, String message);

    String sendWithError(String message);
}
