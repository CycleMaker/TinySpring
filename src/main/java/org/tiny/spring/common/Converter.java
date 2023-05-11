package org.tiny.spring.common;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-04-25 10 :06
 * @description
 */
public class Converter {
    public static <T> Object convertStrToBaseType(Object value,Class<T> type) {
        if (value == null) { return value; }
        if (type == Integer.class) {
            return Integer.parseInt(value.toString());
        }
        if (type == Double.class) {
            return Double.valueOf(value.toString());
        }
        if (type == Float.class) {
            return Float.valueOf(value.toString());
        }
        if (type == String.class) {
            return value.toString();
        }
        if (type == Byte.class) {
            return Byte.valueOf(value.toString());
        }
        if (type == Character.class) {
            return value.toString().toCharArray()[0];
        }
        if (type == Boolean.class) {
            if ("true".equals(value.toString())) {
                return true;
            }
            return false;
        }
        return type.cast(value);
    }
}
