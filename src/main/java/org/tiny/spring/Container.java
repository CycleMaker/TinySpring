package org.tiny.spring;

import org.tiny.spring.common.IdentitySet;
import org.tiny.spring.core.BeanNameGenerator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-04-06 20 :26
 * @description
 */
public class Container {
    private Map<String, Object> nameContainerMap;
    private Map<Class, IdentitySet> classContainerMap;
    private Map<String, Object> configProperties;

    private static final String BEAN_NAME_SPLITER = "%";

    private static Container container = new Container();

    protected static Container getContainer() {
        return container;
    }

    private Container() {
        nameContainerMap = new HashMap<String, Object>();
        classContainerMap = new HashMap<Class, IdentitySet>();
        configProperties = new HashMap<>();
    }

    public Map<String, Object> getBeanNameMap() {
        Map<String, Object> outerBeanNameMap = new HashMap<>();
        nameContainerMap.forEach((innerName, bean) -> {
            outerBeanNameMap.put(innerName.split(BEAN_NAME_SPLITER)[1], bean);
        });
        return outerBeanNameMap;
    }

    public void addConfigProperty(String key, Object value, boolean checkExist) {
        if (configProperties.containsKey(key) && checkExist) {
            throw new RuntimeException("config key is exist:" + key);
        }
        configProperties.put(key, value);
    }

    public Object getConfigProperty(String key) {
        return configProperties.get(key);
    }

    public void registerBean(Object bean) {
        String name = BeanNameGenerator.generateDefaultBeanName(bean);
        addBeanToNameContainerMap(name, bean, true, nameContainerMap);
        addBeanToClassContainerMap(bean, this.classContainerMap);
    }

    public void registerBean(String name, Object bean, boolean checkExist) {
        addBeanToNameContainerMap(name, bean, checkExist, nameContainerMap);
        addBeanToClassContainerMap(bean, this.classContainerMap);
    }

    public void unregisterBean(String name, Object bean) {
        removeBeanFromNameContainerMap(name, bean, this.nameContainerMap);
        removeBeanFromClassContainerMap(bean, this.classContainerMap);
    }


    public Object getBeanByName(String name, Class clazz) {
        return nameContainerMap.get(getInnerBeanName(name, clazz));
    }

    public <T> List<T> getBeanByType(Class<T> clazz) {
        List<T> res = new ArrayList<>();
        Set<Object> objects = classContainerMap.get(clazz);
        if (Objects.isNull(objects)) {
            return res;
        }
        for (Object bean : objects) {
            res.add((T) bean);
        }
        return res;
    }

    public <T> T getSingleton(Class<T> clazz, boolean checkExist) {
        List<T> beanByType = getBeanByType(clazz);
        if (beanByType.size() == 0) {
            if (checkExist) {
                throw new RuntimeException("bean is not exist:" + clazz);
            }
            return null;
        }
        return beanByType.get(0);
    }

    private String getInnerBeanName(String name, Class clazz) {
        return clazz.getName() + BEAN_NAME_SPLITER + name;
    }

    private void addBeanToClassContainerMap(Object bean, Map<Class, IdentitySet> classMap) {
        for (Class clazz : listBeanClass(bean)) {
            classMap.computeIfAbsent(clazz, e -> new IdentitySet()).add(bean);
        }
    }

    private void removeBeanFromClassContainerMap(Object bean, Map<Class, IdentitySet> classMap) {
        for (Class clazz : listBeanClass(bean)) {
            if (classMap.containsKey(clazz)) {
                classMap.get(clazz).remove(bean);
            }
        }
    }

    private List<Class<?>> listBeanClass(Object bean) {
        Class<?>[] classes = bean.getClass().getInterfaces();
        List<Class<?>> classList = Arrays.stream(classes).collect(Collectors.toList());
        classList.addAll(listSuperClass(bean.getClass()));
        classList.add(bean.getClass());
        return classList;
    }

    private List<Class<?>> listSuperClass(Class<?> clazz) {
        List<Class<?>> res = new ArrayList<>();
        for (; ; ) {
            Class<?> parent = clazz.getSuperclass();
            if (Objects.nonNull(parent) && parent != Object.class) {
                res.add(parent);
                clazz = parent;
            } else {
                break;
            }
        }
        return res;
    }

    private void removeBeanFromNameContainerMap(String name, Object bean, Map<String, Object> beanNameMap) {
        beanNameMap.remove(getInnerBeanName(name, bean.getClass()));
    }

    private void addBeanToNameContainerMap(String name, Object bean, boolean checkExist, Map<String, Object> beanNameMap) {
        if (Objects.isNull(name) || name.length() == 0) {
            throw new IllegalArgumentException();
        }
        if (name.contains(BEAN_NAME_SPLITER)) {
            throw new IllegalArgumentException("the name of bean can't contains " + BEAN_NAME_SPLITER);
        }
        if (checkExist && checkBeanExist(name, bean)) {
            throw new RuntimeException("the name is duplicated");
        }
        beanNameMap.put(getInnerBeanName(name, bean.getClass()), bean);
    }

    private boolean checkBeanExist(String name, Object bean) {
        String innerBeanName = getInnerBeanName(name, bean.getClass());
        return nameContainerMap.containsKey(innerBeanName);
    }

}
