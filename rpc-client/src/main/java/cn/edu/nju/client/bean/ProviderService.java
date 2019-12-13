package cn.edu.nju.client.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by thpffcj on 2019/12/13.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderService implements Serializable {

    private String serverIp;
    private int serverPort;
    private int networkPort;
    private long timeout;
    // 服务提供者的权重
    private int weight;
}
