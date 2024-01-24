# BeanFactoryPostProcessor 实例化前的处理器
目前系统自带了以下几个BeanFactoryPostProcessor处理器:
## InitContextBeanFactoryPostProcessor
主要是用于注册初始化系统需要的BeanPostProcessor，包含有:  
**AutowiredBeanPostProcessor** 用于处理@Autowire注解来解决依赖问题  
**ExtensionBeanPostProcessor** 用于处理@Aspect注解来引入AOP系统  
**FillConfigValueBeanPostProcessor** 用于解析@Value注解中的配置key  
**InitializingBeanProcessorFactory** 用户处理实例化后用户自定义初始化操作  
**DefaultInstantiationProcessor** 默认的bean实例化操作  
**RegisterBeanPostProcessor** 用于处理用户注册Bean，类似Spring的@Bean注解的功能  
## ResolveResourceBeanFactoryPostProcessor
主要是用于处理用户自定义的配置文件
## AutoConfigurationBeanFactoryPostProcessor
用于扫描"META-INF/spring.factories"下的配置文件并自动注册bean,文件内容格式:  
org.tiny.spring.core.AutoConfiguration=org.tiny.mvc.configure.WebMVCAutoConfigurerBeanFactoryPostProcessor

***
# BeanInstantiationProcessor 用于实例化bean的处理器
目前系统自带了DefaultInstantiationProcessor，只用于无参构造函数的bean。用户可以继承BeanInstantiationProcessor并实现自己的处理器,
每个BeanInstantiationProcessor都需要实现初始化操作:
```
public Object processInstantiation(Class clazz, String beanName)
```

***
# BeanPostProcessor 用于初始化的bean处理器
用户可以继承BeanPostProcessor来实现自己的初始化处理器
```
public Object postProcessAfterInitialization(Object bean, String beanName)
```

*** 
# 使用
## @Bean注解
用于注册一个Bean
```
@Bean
public class MyBean {
   
}
```
## @Autowired注解
用于引入依赖
```
@Bean
public class MyBean {
    @Autowired
    private TestConfig testConfig;
}
```

## @Value注解
用于解析配置
```
@Bean
public class AutoTest {
    @Value(key = "test")
    private String test;
}
```

## InitializingBean接口
用于用户自定义初始化操作
```
@Bean
public class AutoTest implements InitializingBean {
    @Override
    public void afterPropertiesSet() {
        System.out.println("after init");
    }
}
```
## Container类
用于管理容器和配置，重要方法:
registerBean 注册一个bean
getBeanByName 通过名字获取一个bean
getBeanByType 通过Type获取bean列表
getSingleton 获取一个单例bean，如果有多个会报错
addConfigProperty 增加一个配置
可以在任意bean中通过@Autowire注解来获取Container

## Aspect接口
用于注册一个切面定义
```
public interface Aspect {
    void before(Object o, Method m, Object[] args);

    void after(Object o, Method m, Object[] args, Object res);

    default Object afterException(Object o, Method m, Object[] args, Exception e) throws Exception { throw  e;}

    // in finally block
    default void afterFinish(Object o, Method m, Object[] args) {}

    default int priority() { return 100; }
}


```
## Extension注解
注解可以放在类中也可以放在方法中，aspect参数里填的是实现Aspect的类。
@Extension(aspect = {MyAspect.class, FirstAspect.class})

## AopContext工具类
用于在被代理的类中获取代理类，类似于Spring的AopUtil



