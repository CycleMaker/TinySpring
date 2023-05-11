package org.tiny.spring.config.resolver;

import org.tiny.spring.config.ResourceScanner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-04-14 11 :35
 * @description
 */
public class ApplicationPropertiesResolver extends ResourceScanner implements ResourceResolver<SimplePropertiesResource> {

    private Class source;
    public ApplicationPropertiesResolver(Class source) {
        this.source = source;
    }


    @Override
    public List<SimplePropertiesResource> scanResources() {
        List<SimplePropertiesResource> res = new ArrayList<>();
        List<String> resourcePaths = listResourcePaths(source);
        for (String resourcePath : resourcePaths) {
            if (resourcePath.endsWith(".properties")) {
                res.add(new SimplePropertiesResource(resourcePath));
            }
        }
        return res;
    }



    @Override
    public Map<String, Object> resolve(SimplePropertiesResource resourceObject) {
        if (Objects.isNull(resourceObject)) {
            return null;
        }
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(resourceObject.getPath());
            return resolve(inputStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("file cannot find",e);
        }
    }

    public Map<String, Object> resolve(InputStream inputStream) {
        try {
            byte[] content = new byte[inputStream.available()];
            while (inputStream.available() > 0) {
                inputStream.read(content);
            }
            return resolve2map(new String(content));
        } catch (Exception e) {
            throw new RuntimeException("file read fail",e);
        }
    }

    public Map<String, Object> resolve2map(String contentStr) {
        HashMap<String, Object> res = new HashMap<>();
        String[] lines = contentStr.split("\n");
        for (String line : lines) {
            if (Objects.isNull(line) || line.length() == 0 || line.startsWith("#")) {
                continue;
            }
            String[] kv = line.split("=");
            if (kv.length < 2) {
                throw new RuntimeException("unable resolve config:" + line);
            }
            Map<String, String> kvMap = getKV(line);
            for (String key : kvMap.keySet()) {
                res.put(key, kvMap.get(key));
            }
        }
        return res;
    }

    Map<String,String> getKV(String line) {
        StringBuilder keyBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();
        boolean hasEqual = false;
        for (Character ch : line.toCharArray()) {
            if (ch == ' ') { continue; }
            if (ch == '=' && !hasEqual) {
                hasEqual = true;
            } else if (hasEqual) {
                valueBuilder.append(ch);
            } else {
                keyBuilder.append(ch);
            }
        }
        HashMap<String,String> res = new HashMap();
        res.put(keyBuilder.toString(), valueBuilder.toString());
        return res;
    }


}
