package org.tiny.spring.core.processor;

import org.tiny.spring.Container;
import org.tiny.spring.config.resolver.ApplicationPropertiesResolver;
import org.tiny.spring.config.resolver.ResourceResolver;

import java.util.List;
import java.util.Map;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-04-14 15 :31
 * @description
 */
public class ResolveResourceBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    private Class source;

    @Override
    public void process(Container container) {
        for (ResourceResolver resourceResolver : container.getBeanByType(ResourceResolver.class)) {
            List<Object> resources = resourceResolver.scanResources();
            for (Object resource : resources) {
                Map<String, Object> kv = resourceResolver.resolve(resource);
                for (String key : kv.keySet()) {
                    container.addConfigProperty(key, kv.get(key), false);
                }
            }
        }
    }

    public ResolveResourceBeanFactoryPostProcessor(Class source) {
        this.source = source;
    }


}
