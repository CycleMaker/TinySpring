package org.tiny.spring.core;

import org.tiny.spring.Container;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-04-20 15 :36
 * @description
 */
public interface ContextFinishListener {
    void finishContext(Container container);
}
