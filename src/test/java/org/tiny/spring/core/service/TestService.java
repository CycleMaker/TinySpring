package org.tiny.spring.core.service;

import lombok.Data;
import org.tiny.spring.annotation.Autowired;
import org.tiny.spring.annotation.Bean;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-04-07 11 :39
 * @description
 */
@Bean
@Data
public class TestService {
    @Autowired
    private TestConfig testConfig;
}
