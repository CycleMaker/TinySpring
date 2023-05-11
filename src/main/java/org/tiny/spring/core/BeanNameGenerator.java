package org.tiny.spring.core;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-04-12 15 :52
 * @description
 */
public class BeanNameGenerator {
    public static String generateBeanNameByClazz(Class clazz) {
        return toLowerCaseFirstOne(clazz.getSimpleName());
    }

    public static String generateDefaultBeanName(Object bean) {
        return toLowerCaseFirstOne(bean.getClass().getSimpleName());
    }

    public static String toLowerCaseFirstOne(String s) {
        return Character.isLowerCase(s.charAt(0)) ? s : Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }
}
