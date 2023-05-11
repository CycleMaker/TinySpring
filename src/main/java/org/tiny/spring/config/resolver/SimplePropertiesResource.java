package org.tiny.spring.config.resolver;

import lombok.Data;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-04-15 17 :24
 * @description
 */
@Data
public class SimplePropertiesResource {
    private String path;
    public SimplePropertiesResource(String path) {
        this.path = path;
    }
}
