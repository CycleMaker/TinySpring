package org.tiny.spring.core;

import org.tiny.spring.annotation.Bean;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: wuzihan (wuzihan@youzan.com)
 * @create: 2023-04-07 11 :40
 * @description
 */
public class BeanScanner {

    public static Map<String, Class> listNeedImportToBeanClass(String packageName) {
        Map<String, Class> beanMap = new HashMap<>();
        List<Class> classList = listClassAnnotatedWithBean(packageName);
        for (Class clazz : classList) {
            String beanName = BeanNameGenerator.generateBeanNameByClazz(clazz);
            if (beanMap.containsKey(beanName)) {
                throw new RuntimeException("bean already exists: " + clazz);
            }
            beanMap.put(beanName, clazz);
        }
        return beanMap;
    }

    public static List<Class> listClassAnnotatedWithBean(String packageName) {
        return scanClass(packageName)
                .stream()
                .filter(BeanScanner::isAnnotated)
                .filter(BeanScanner::isNormalClass)
                .collect(Collectors.toList());
    }

    private static boolean isNormalClass(Class clazz) {
        return !clazz.isAnnotation() && !clazz.isInterface();
    }

    private static boolean isAnnotated(Class clazz) {
        Annotation[] declaredAnnotations = clazz.getDeclaredAnnotations();
        for (Annotation annotation : declaredAnnotations) {
            if (annotation.annotationType() == Bean.class) {
                return true;
            }
            Class<? extends Annotation> type = annotation.annotationType();
            if (isMetaAnno(type)) {
                continue;
            }
            if (isAnnotated(type)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isMetaAnno(Class clazz) {
        return clazz == Retention.class || clazz == Documented.class || clazz == Target.class || clazz == Repeatable.class;
    }


    public static List<Class> scanClass(String pkgPath) {
        String filePath = pkgPath.replaceAll("\\.", "/");
        Enumeration<URL> resources = null;
        try {
            resources = Thread.currentThread().getContextClassLoader().getResources(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Unknown packagePath:" + pkgPath);
        }
        List<Class> clazzList = new ArrayList<>();
        for (FilePath fileItem : scanFiles(resources, pkgPath)) {
            if (!resolvableJavaClass(fileItem.getFullName())) {
                continue;
            }
            try {
                clazzList.add(Class.forName(getJavaClassName(fileItem.getFullName())));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Unknown Class", e);
            }
        }
        return clazzList;
    }

    private static boolean resolvableJavaClass(String fullPath) {
        if (Objects.isNull(fullPath) || fullPath.length() < 1) {
            return false;
        }
        return fullPath.endsWith(".class");
    }

    private static String getJavaClassName(String fullPath) {
        return fullPath.substring(0, fullPath.length() - ".class".length());
    }

    private static List<FilePath> scanFiles(Enumeration<URL> resources, String packageName) {
        List<FilePath> filePaths = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            filePaths.addAll(scanFiles(url.getPath(), packageName, false));
        }
        return filePaths;
    }

    private static List<FilePath> scanFiles(String filePath, String packageName, boolean changePackageName) {
        List<FilePath> filePaths = new ArrayList<>();
        File file = new File(filePath);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (Objects.isNull(files)) {
                return filePaths;
            }
            String subPackageName = packageName;
            if (changePackageName) {
                subPackageName = packageName + "." + file.getName();
            }
            for (File sub : files) {
                filePaths.addAll(scanFiles(sub.getPath(), subPackageName, true));
            }
        } else {
            filePaths.add(new FilePath(packageName, file.getName()));
        }
        return filePaths;
    }

    static class FilePath {
        private String packageName;
        private String fileName;

        FilePath(String packageName, String fileName) {
            this.packageName = packageName;
            this.fileName = fileName;
        }

        String getFullName() {
            return packageName + "." + fileName;
        }
    }
}
