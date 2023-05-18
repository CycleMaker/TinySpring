package org.tiny.spring.core.processor;

import org.tiny.spring.annotation.Autowired;
import org.tiny.spring.Container;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-04-11 15 :38
 * @description
 */
public class AutowiredBeanPostProcessor implements BeanPostProcessor {

    private Container container;

    private void processAutowired(Object bean, Container container) {
        Class clazz = bean.getClass();
        Map<Field,Autowired> annotatedFields = getAnnotatedFields(clazz);
        for (Field field : annotatedFields.keySet()) {
            field.setAccessible(true);
            Class fieldType = field.getType();
            if (fieldType == Container.class) {
                setField(field ,bean, container);
                continue;
            }
            Object dependency = container.getBeanByName(field.getName(), fieldType);
            if (Objects.isNull(dependency)) {
                List<?> dependencies = container.getBeanByType(fieldType);
                boolean check = annotatedFields.get(field).require();
                if (dependencies == null || dependencies.isEmpty()) {
                    if (check) {
                        throw new RuntimeException("bean not exist:" + clazz.getName());
                    } else {
                        dependency = null;
                    }
                } else {
                    dependency = dependencies.stream().findFirst().get();
                }
            }
            setField(field ,bean, dependency);
        }
    }

    private void setField(Field field, Object bean ,Object dependency) {
        try {
            field.set(bean, dependency);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("unable access field:" + field.getName());
        }
    }

    private Map<Field,Autowired> getAnnotatedFields(Class clazz) {
        Map<Field,Autowired> res = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Autowired annotation = field.getAnnotation(Autowired.class);
            if (Objects.nonNull(annotation)) {
                res.put(field,annotation);
            }
        }
        return res;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        processAutowired(bean, container);
        return bean;
    }

    AutowiredBeanPostProcessor(Container container) {
        this.container = container;
    }

    @Override
    public int priority() {
        return Integer.MIN_VALUE;
    }
}
