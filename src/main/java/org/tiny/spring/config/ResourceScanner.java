package org.tiny.spring.config;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-04-14 17 :35
 * @description
 */
public class ResourceScanner {

    public List<String> listResourcePaths(Class location) {
        URL resourceUrl = location.getClassLoader().getResource("");
        if (Objects.isNull(resourceUrl)) {
            return Collections.emptyList();
        }
        return listResourcePaths(resourceUrl.getPath());
    }

    public List<String> listResourcePaths(String resouceDir) {
        List<String> res = new ArrayList<>();
        File file = new File(resouceDir);
        if (!file.isDirectory()) { return res; }
        File[] resources = file.listFiles();
        if (Objects.isNull(resources)) { return res; }
        for (File resource : resources) {
            if (!resource.isDirectory()){
                res.add(resource.getPath());
            }
        }
        return res;
    }
}
