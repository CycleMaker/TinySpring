package org.tiny.spring.core;

import org.tiny.spring.TinyApplication;
import org.tiny.spring.annotation.Autowired;
import org.tiny.spring.annotation.Bean;
import org.tiny.spring.annotation.Extension;
import org.tiny.spring.annotation.Value;
import org.tiny.spring.core.service.FirstAspect;
import org.tiny.spring.core.service.MyAspect;
import org.tiny.spring.core.service.TestConfig;
import org.tiny.spring.core.service.TestService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-04-06 22 :58
 * @description
 */
@Bean
@Extension(aspect = {MyAspect.class, FirstAspect.class})
public class TinySpring implements InitializingBean {
    @Autowired
    private TestService testService;

    @Autowired
    private TestConfig testConfig2;

    @Value(key = "test.config")
    protected String test;

    public static void main(String[] args) {
        TinyApplication.run(TinySpring.class);
    }

    public void test() {
        System.out.println("h");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("testService.getTestConfig().getTest() = " + testService.getTestConfig().getTest());
        System.out.println("testConfig.getTest() = " + testConfig2.getTest());
        System.out.println("test = " + test);
        this.test();
    }

}
