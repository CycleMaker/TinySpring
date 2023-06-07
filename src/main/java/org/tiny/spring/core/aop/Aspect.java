package org.tiny.spring.core.aop;

import java.lang.reflect.Method;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-05-06 14 :38
 * @description
 */
public interface Aspect {
    void before(Object o, Method m, Object[] args);

    void after(Object o, Method m, Object[] args, Object res);

    default Object afterException(Object o, Method m, Object[] args, Exception e) throws Exception { throw  e;}

    // in finally block
    default void afterFinish(Object o, Method m, Object[] args) {}

    default int priority() { return 100; }
}
