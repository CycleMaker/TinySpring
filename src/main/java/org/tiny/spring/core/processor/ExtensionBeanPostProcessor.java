package org.tiny.spring.core.processor;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.tiny.spring.Container;
import org.tiny.spring.annotation.Extension;
import org.tiny.spring.core.aop.AopContext;
import org.tiny.spring.core.aop.Aspect;
import java.lang.reflect.Modifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;


public class ExtensionBeanPostProcessor implements BeanPostProcessor {

    private Container container;

    public ExtensionBeanPostProcessor(Container container) {
        this.container = container;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Class clazz = bean.getClass();
        Map<Method, List<Class<? extends Aspect>>> methodAspectMap = getExtensionMap(clazz);
        if (methodAspectMap.isEmpty()) {
            return bean;
        }
        Object agent = enhance(bean, methodAspectMap);
        AopContext.putAgent(bean.getClass(), agent);
        return agent;
    }


    public Map<Method, List<Class<? extends Aspect>>> getExtensionMap(Class clazz) {
        Map<Method, List<Class<? extends Aspect>>> methodMap = new HashMap<>();
        for (Method m : listDeclaredMethod(clazz,false)) {
            Extension anno = m.getAnnotation(Extension.class);
            if (Objects.nonNull(anno)) {
                methodMap.computeIfAbsent(m, e -> new ArrayList<>()).addAll(Arrays.stream(anno.aspect()).collect(Collectors.toList()));
            }
        }
        Annotation clzAnno = clazz.getAnnotation(Extension.class);
        if (Objects.nonNull(clzAnno)) {
            Extension clzExtension = (Extension) clzAnno;
            Class<? extends Aspect>[] clazzAspects = clzExtension.aspect();
            if (Objects.nonNull(clazzAspects)) {
                for (Method m : listDeclaredMethod(clazz, true)) {
                    if (!methodMap.containsKey(m)) {
                        methodMap.computeIfAbsent(m, e -> new ArrayList<>()).addAll(Arrays.stream(clazzAspects).collect(Collectors.toList()));
                    }
                }
            }
        }
        return methodMap;
    }


    private Object enhance(Object origin, Map<Method, List<Class<? extends Aspect>>> methodAspectMap) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(origin.getClass());
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            List<Aspect> aspects = methodAspectMap.getOrDefault(method, new ArrayList<>()).stream().map(this::getAspect).sorted(Comparator.comparingInt(Aspect::priority)).collect(Collectors.toList());
            return enhance(aspects, 0, origin, args, () -> proxy.invoke(origin, args), method);
        });
        return enhancer.create();
    }


    public Object enhance(List<Aspect> aspects, int current, Object origin, Object[] args, SupplierWithError supplier, Method m) throws Throwable {
        if (current == aspects.size()) {
            return supplier.get();
        }
        int finalCurrent = current;
        return enhance(aspects, ++current, origin, args,
                () -> {
                    Aspect aspect = aspects.get(finalCurrent);
                    try {
                        aspect.before(origin, m, args);
                        Object res = supplier.get();
                        aspect.after(origin, m, args, res);
                        return res;
                    } catch (Exception e) {
                        return aspect.afterException(origin, m, args, e);
                    } finally {
                        aspect.afterFinish(origin, m, args);
                    }
                }, m);
    }

    public Aspect getAspect(Class<? extends Aspect> clazz) {
        List<? extends Aspect> aspects = container.getBeanByType(clazz);
        if (aspects.size() > 1) {
            throw new RuntimeException("Aspect Class More Than 1:" + clazz);
        }
        if (aspects.size() == 0) {
            throw new RuntimeException("Lack Aspect Class:" + clazz);
        }
        return aspects.get(0);
    }

    private List<Method> listDeclaredMethod(Class clazz,boolean modifyPublic) {
        List<Method> res = new ArrayList<>();
        for (Method m : clazz.getDeclaredMethods()) {
            int modifiers = m.getModifiers();
            if (!Modifier.isStatic(modifiers) && m.getDeclaringClass() == clazz) {
                if (!modifyPublic || Modifier.isPublic(modifiers)) {
                    res.add(m);
                }
            }
        }
        return res;
    }


    interface SupplierWithError {
        Object get() throws Throwable;
    }
}
