package org.tiny.spring.core.processor;

import java.lang.reflect.Constructor;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-04-11 16 :45
 * @description
 */
public interface BeanInstantiationProcessor {
    Object processInstantiation(Class clazz, String beanName);


    default void afterInstantiation(Object o) {};


    default int priority() { return 100; }

}
