package org.tiny.spring;

import org.tiny.spring.config.resolver.ApplicationPropertiesResolver;
import org.tiny.spring.core.ContextFinishListener;
import org.tiny.spring.core.processor.*;
import org.tiny.spring.core.BeanScanner;

import java.util.*;
import java.util.stream.Collectors;


public class TinyApplication {

    public static void run(Class clazz) {
        Container container = Container.getContainer();

        Map<String, Class> beanClassMap = scanBeanClass(clazz);

        invokeBeanFactoryPostProcessor(clazz, beanClassMap.values(), container);

        invokeInstantiationProcessorAndRegisterBean(beanClassMap, container);

        invokeBeanPostProcessor(container);

        invokeFinishContextListener(container);
    }

    private static void invokeFinishContextListener(Container container) {
        List<ContextFinishListener> finishBeans = container.getBeanByType(ContextFinishListener.class);
        for (ContextFinishListener listener : finishBeans) {
            listener.finishContext(container);
        }
    }


    private static void invokeBeanFactoryPostProcessor(Class source, Collection<Class> clazzList, Container container) {
        container.registerBean(new ApplicationPropertiesResolver(source));
        container.registerBean(new InitContextBeanFactoryPostProcessor());
        container.registerBean(new ResolveResourceBeanFactoryPostProcessor(source));
        container.registerBean(new AutoConfigurationBeanFactoryPostProcessor());
        processBeanFactoryPostProcessorsWithoutExecuted(container, new HashSet<>());
    }

    private static void processBeanFactoryPostProcessorsWithoutExecuted(Container container, Set<BeanFactoryPostProcessor> executed) {
        List<BeanFactoryPostProcessor> beanByType = container.getBeanByType(BeanFactoryPostProcessor.class);
        if (executed.containsAll(beanByType)) {
            return;
        }
        for (BeanFactoryPostProcessor beanFactoryPostProcessor : beanByType) {
            if (executed.contains(beanFactoryPostProcessor)) {
                continue;
            }
            beanFactoryPostProcessor.process(container);
            executed.add(beanFactoryPostProcessor);
        }
        processBeanFactoryPostProcessorsWithoutExecuted(container, executed);
    }


    private static void initBeanInstantiationProcessor(Container container, Collection clazzList) {
        List<BeanInstantiationProcessor> processors = listTargetInstance(BeanInstantiationProcessor.class, clazzList);
        processors.forEach(container::registerBean);
    }

    private static Map<String, Class> scanBeanClass(Class clazz) {
        Package pkg = clazz.getPackage();
        return BeanScanner.listNeedImportToBeanClass(pkg == null ? "" : pkg.getName());
    }

    private static void invokeBeanPostProcessor(Container container) {
        List<BeanPostProcessor> beanPostProcessors = container.getBeanByType(BeanPostProcessor.class)
                .stream()
                .sorted(Comparator.comparingInt(BeanPostProcessor::priority))
                .collect(Collectors.toList());
        Map<String, Object> beanNameMap = container.getBeanNameMap();
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            Iterator<String> iterator = beanNameMap.keySet().iterator();
            while (iterator.hasNext()) {
                String beanName = iterator.next();
                Object bean = beanNameMap.get(beanName);
                Object newBean = beanPostProcessor.postProcessAfterInitialization(bean, beanName);
                if (newBean != bean) {
                    container.unregisterBean(beanName, bean);
                    container.registerBean(beanName, newBean, false);
                }
            }
            beanNameMap = container.getBeanNameMap();
        }
    }


    private static void invokeInstantiationProcessorAndRegisterBean(Map<String, Class> beanClassMap, Container container) {
        initBeanInstantiationProcessor(container, beanClassMap.values());
        List<BeanInstantiationProcessor> processors = container
                .getBeanByType(BeanInstantiationProcessor.class)
                .stream()
                .sorted(Comparator.comparingInt(BeanInstantiationProcessor::priority))
                .collect(Collectors.toList());

        for (BeanInstantiationProcessor beanInstantiationProcessor : processors) {
            for (String beanName : beanClassMap.keySet()) {
                Class clazz = beanClassMap.get(beanName);
                if (Objects.nonNull(container.getBeanByName(beanName, clazz))) {
                    continue;
                }
                Object bean = beanInstantiationProcessor.processInstantiation(clazz, beanName);
                if (Objects.nonNull(bean)) {
                    container.registerBean(beanName, bean, true);
                }
            }
        }
    }

    private static <T> List<T> listTargetInstance(Class<T> targetClass, Collection<Class> classList) {
        List<T> res = new ArrayList<>();
        for (Class clazz : classList) {
            try {
                if (Arrays.asList(clazz.getInterfaces()).contains(targetClass)) {
                    res.add((T) clazz.newInstance());
                }
            } catch (IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(targetClass +" init error", e);
            }
        }
        return res;
    }
}
