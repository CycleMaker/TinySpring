package org.tiny.spring.core.processor;

import org.tiny.spring.Container;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-04-11 19 :31
 * @description
 */
public class InitContextBeanFactoryPostProcessor implements BeanFactoryPostProcessor {


    @Override
    public void process(Container container) {
        container.registerBean(new AutowiredBeanPostProcessor(container));
        container.registerBean(new ExtensionBeanPostProcessor(container));
        container.registerBean(new FillConfigValueBeanPostProcessor(container));
        container.registerBean(new InitializingBeanProcessorFactory());
        container.registerBean(new DefaultInstantiationProcessor());
        container.registerBean(new RegisterBeanPostProcessor(container));
    }

}
