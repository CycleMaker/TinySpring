package org.tiny.spring.core.processor;

import java.lang.reflect.Constructor;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-04-12 10 :05
 * @description
 */
public class DefaultInstantiationProcessor implements BeanInstantiationProcessor{
    @Override
    public Object processInstantiation(Class clazz, String beanName){
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Instantiation Failed:" + clazz);
        }
    }

    @Override
    public int priority() {
        return Integer.MAX_VALUE;
    }
}
