package cn.edu.nju.api.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by thpffcj on 2019/12/13.
 */
@Component
// 表明该注解可以应用的java元素类型，TYPE：应用于类，接口（包括注解类型），枚举
@Target(ElementType.TYPE)
// 表明该注解的生命周期，RUNTIME：由JVM加载，包含在类文件中，在运行时可以被获取到
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcClient {

    String version() default "";
}
