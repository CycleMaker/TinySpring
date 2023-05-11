package org.tiny.spring.core.processor;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-04-11 19 :46
 * @description
 */
public interface BeanPostProcessor {
    Object postProcessAfterInitialization(Object bean, String beanName);

    default int priority() { return 100; }
}
