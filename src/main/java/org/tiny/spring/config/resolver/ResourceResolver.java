package org.tiny.spring.config.resolver;

import org.tiny.spring.config.resolver.ApplicationPropertiesResolver;

import java.util.List;
import java.util.Map;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-04-14 10 :56
 * @description
 */
public interface ResourceResolver<T> {

    List<T> scanResources();

    Map<String,Object> resolve(T resource);

}
