package org.tiny.spring.core.service;

import org.tiny.spring.annotation.Bean;
import org.tiny.spring.core.Aspect;

import java.lang.reflect.Method;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-05-08 11 :55
 * @description
 */
@Bean
public class FirstAspect implements Aspect {
    @Override
    public void before(Object o, Method m, Object[] args) {
        System.out.println("shabi");
    }



    @Override
    public void after(Object o, Method m, Object[] args, Object res) {
        System.out.println("huhu");
    }
}
