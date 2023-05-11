package org.tiny.spring.core.processor;

import org.tiny.spring.Container;
import org.tiny.spring.core.ContextFinishListener;
import org.tiny.spring.core.RegisterBean;
import java.util.*;


/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-04-12 16 :33
 * @description
 */
public class RegisterBeanPostProcessor implements BeanPostProcessor {

    private Container container;

    public RegisterBeanPostProcessor(Container container) {
        this.container = container;
    }



    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof RegisterBean) {
            Map<String,Object> registerBeanMap = ((RegisterBean) bean).registerBean();
            if (Objects.isNull(registerBeanMap) || registerBeanMap.isEmpty()) {
                return bean;
            }
            String registerBeanName = registerBeanMap.keySet().stream().findFirst().get();
            Object registerBean = registerBeanMap.get(registerBeanName);
            container.registerBean(registerBeanName, registerBean, true);
        }
        return bean;
    }
}
