package org.tiny.spring.core.aop;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-06-02 17 :50
 * @description
 */
public class AopContext {

    private static final Map<Class,Object> agentMap = new HashMap<>();

    public static <T> T getAgent(Class<T> origin) {
        return (T) agentMap.get(origin);
    }

    public static void putAgent(Class origin,Object bean) {
        agentMap.put(origin,bean);
    }

    private AopContext() {

    }
}
