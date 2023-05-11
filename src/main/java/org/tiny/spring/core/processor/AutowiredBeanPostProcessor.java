package org.tiny.spring.core.processor;

import org.tiny.spring.annotation.Autowired;
import org.tiny.spring.Container;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-04-11 15 :38
 * @description
 */
public class AutowiredBeanPostProcessor implements BeanPostProcessor {

    private Container container;

    private void processAutowired(Object bean, Container container) {
        Class clazz = bean.getClass();
        List<Field> annotatedFields = getAnnotatedFields(clazz);
        for (Field field : annotatedFields) {
            field.setAccessible(true);
            Class fieldType = field.getType();
            if (fieldType == Container.class) {
                setField(field ,bean, container);
                return;
            }
            Object dependency = container.getBeanByName(field.getName(), fieldType);
            if (Objects.isNull(dependency)) {
                List<?> dependencies = container.getBeanByType(fieldType);
                if (dependencies == null || dependencies.isEmpty()) {
                    throw new RuntimeException("bean not exist:" + clazz.getName());
                }
                dependency = dependencies.stream().findFirst().get();
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

    private List<Field> getAnnotatedFields(Class clazz) {
        List<Field> res = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Objects.nonNull(field.getAnnotation(Autowired.class))) {
                res.add(field);
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
