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
public class RpcRequest {

   private String requestId;
   private String className;
   private String methodName;
   private Class<?>[] parameterTypes;
   private Object[] parameters;
}
