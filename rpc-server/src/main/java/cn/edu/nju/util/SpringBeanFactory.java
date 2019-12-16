package cn.edu.nju.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Created by thpffcj on 2019/12/15.
 */
@Component
public class SpringBeanFactory implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static <T> T getBean(Class<T> cls) {
        return context.getBean(cls);
    }

    public static Map<String, Object> getBeanListByAnnotationClass(
            Class<? extends Annotation> annotationClass) {
        return context.getBeansWithAnnotation(annotationClass);
    }
}
