package org.tiny.spring.core;

import java.util.Map;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-04-12 16 :32
 * @description 在BeanPostProcessor流程中执行
 */
public interface RegisterBean {
    /**
     * 注册bean
     * @return Map<BeanName,Bean>
     */
    Map<String,Object> registerBean();
}
