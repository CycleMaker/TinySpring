package org.tiny.spring.core.processor;

import org.tiny.spring.core.InitializingBean;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-04-11 16 :19
 * @description
 */
public class InitializingBeanProcessorFactory implements BeanPostProcessor {


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof InitializingBean) {
            ((InitializingBean)bean).afterPropertiesSet();
        }
        return bean;
    }

    @Override
    public int priority() {
        return Integer.MAX_VALUE;
    }

}
