package org.tiny.spring.core.processor;

import org.tiny.spring.Container;
import org.tiny.spring.annotation.Value;
import org.tiny.spring.common.Converter;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-04-15 17 :37
 * @description
 */
public class FillConfigValueBeanPostProcessor implements BeanPostProcessor {
    private Container container;
    public FillConfigValueBeanPostProcessor(Container container) {
        this.container = container;
    }
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Map<Field,Value> fieldAnnoMap = scanValueAnnotation(bean);
        for (Field field : fieldAnnoMap.keySet()) {
            field.setAccessible(true);
            Value anno = fieldAnnoMap.get(field);
            Object configProperty = container.getConfigProperty(anno.key());

            try {
                if (Objects.nonNull(configProperty)) {
                    field.set(bean, Converter.convertStrToBaseType(configProperty, field.getType()));
                }
            } catch (Exception e) {
                throw new RuntimeException("unable set value for field:" + field.getName());
            }
        }
        return bean;
    }


    private Map<Field,Value> scanValueAnnotation(Object bean) {
        Map<Field,Value> fieldAnnoMap = new HashMap<>();
        for (Field field : bean.getClass().getDeclaredFields()) {
            Value anno = field.getAnnotation(Value.class);
            if (Objects.nonNull(anno)) {
                fieldAnnoMap.put(field, anno);
            }
        }
        return fieldAnnoMap;
    }
}
