package org.tiny.spring.core.processor;

import org.tiny.spring.Container;
import org.tiny.spring.config.resolver.ApplicationPropertiesResolver;
import org.tiny.spring.core.AutoConfiguration;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-04-21 17 :07
 * @description
 */
public class AutoConfigurationBeanFactoryPostProcessor implements BeanFactoryPostProcessor{

    private static final String FACTORY_PATH = "META-INF/spring.factories";
    private ApplicationPropertiesResolver propertiesResolver;
    @Override
    public void process(Container container) {
        if (Objects.isNull(propertiesResolver)) {
            propertiesResolver = container.getSingleton(ApplicationPropertiesResolver.class, true);
        }
        registerFromFactory(container);
    }

    private static String getAutoConfigurationClass() {
        return AutoConfiguration.class.getName();
    }


    private void registerFromFactory(Container container) {
        try {
            Enumeration<URL> urls = this.getClass().getClassLoader().getResources(FACTORY_PATH);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                Map<String, Object> kvMap = propertiesResolver.resolve(url.openConnection().getInputStream());
                String classPathStr = (String) kvMap.get(getAutoConfigurationClass());
                if (classPathStr == null || classPathStr.length() == 0) {
                    continue;
                }
                if (classPathStr.contains(",")) {
                    String[] classPaths = classPathStr.split(",");
                    for (String path : classPaths) {
                        autoRegister(container, path);
                    }
                } else {
                    autoRegister(container, classPathStr);
                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private void autoRegister(Container container, String classPath) {
        try {
            Object autoBean = Class.forName(classPath).newInstance();
            container.registerBean(autoBean);
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
