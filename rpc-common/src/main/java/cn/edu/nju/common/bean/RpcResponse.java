package cn.edu.nju.common.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by thpffcj on 2019/12/13.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse {

    private String requestId;
    private Object result;
    private Throwable cause;

    public boolean isError() {
        return cause != null;
    }
}
