package cn.edu.nju.client.bean;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by thpffcj on 2019/12/14.
 *
 * 一个EventLoopGroup包含一个或者多个EventLoop
 * 一个EventLoop在它的生命周期内只和一个Thread绑定
 * 所有由EventLoop处理的I/O事件都将在它专有的Thread上被处理
 * 一个Channel在它的生命周期内只注册于一个EventLoop
 * 一个EventLoop可能会被分配给一个或多个Channel
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelHolder {

    private Channel channel;
    private EventLoopGroup eventLoopGroup;
}
