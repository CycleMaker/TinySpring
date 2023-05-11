package org.tiny.spring.core.processor;

import org.tiny.spring.Container;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-04-11 16 :41
 * @description
 */


public interface BeanFactoryPostProcessor {
    void process(Container container);

    default int priority() {
        return 100;
    }
}
