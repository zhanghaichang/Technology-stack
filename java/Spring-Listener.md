在这篇文章中[聊一聊 Spring 中的扩展机制（一）](https://juejin.im/post/5b7964d6f265da43412866c7)中对`Spring`中的事件机制进行了分析。那么对于 `SpringBoot` 来说，它在 `Spring` 的基础上又做了哪些拓展呢？本篇将来聊一聊 `SpringBoot` 中的事件。

在 SpringBoot 的启动过程中，会通过 SPI 机制去加载 spring.factories 下面的一些类，这里面就包括了事件相关的类。

*   SpringApplicationRunListener

```java

    # Run Listeners
    org.springframework.boot.SpringApplicationRunListener=\
    org.springframework.boot.context.event.EventPublishingRunListener
```
*   ApplicationListener
```java
    # Application Listeners
    org.springframework.context.ApplicationListener=\
    org.springframework.boot.ClearCachesApplicationListener,\
    org.springframework.boot.builder.ParentContextCloserApplicationListener,\
    org.springframework.boot.context.FileEncodingApplicationListener,\
    org.springframework.boot.context.config.AnsiOutputApplicationListener,\
    org.springframework.boot.context.config.ConfigFileApplicationListener,\
    org.springframework.boot.context.config.DelegatingApplicationListener,\
    org.springframework.boot.context.logging.ClasspathLoggingApplicationListener,\
    org.springframework.boot.context.logging.LoggingApplicationListener,\
    org.springframework.boot.liquibase.LiquibaseServiceLocatorApplicationListener
```
`SpringApplicationRunListener` 类是 `SpringBoot` 中新增的类。`SpringApplication` 类 中使用它们来间接调用 `ApplicationListener`。另外还有一个新增的类是`SpringApplicationRunListeners`，`SpringApplicationRunListeners` 中包含了多个 `SpringApplicationRunListener`。

## SpringApplicationRunListener
----------------------------

`SpringApplicationRunListener` 接口规定了 `SpringBoot` 的生命周期，在各个生命周期广播相应的事件，调用实际的 `ApplicationListener` 类。通过对 `SpringApplicationRunListener` 的分析，也可以对 `SpringBoot` 的整个启动过程的理解会有很大帮助。

先来看下`SpringApplicationRunListener` 接口的代码：
```java
    public interface SpringApplicationRunListener {
    	//当run方法首次启动时立即调用。可用于非常早期的初始化。
    	void starting();
    	//在准备好环境后，但在创建ApplicationContext之前调用。
    	void environmentPrepared(ConfigurableEnvironment environment);
    	//在创建和准备好ApplicationContext之后，但在加载源之前调用。
    	void contextPrepared(ConfigurableApplicationContext context);
    	//在加载应用程序上下文后但刷新之前调用
    	void contextLoaded(ConfigurableApplicationContext context);
    	//上下文已刷新，应用程序已启动，但尚未调用commandlinerunner和applicationrunner。
    	void started(ConfigurableApplicationContext context);
    	//在运行方法完成之前立即调用，此时应用程序上下文已刷新，
    	//并且所有commandlinerunner和applicationrunner都已调用。
    	//2.0 才有
    	void running(ConfigurableApplicationContext context);
    	//在运行应用程序时发生故障时调用。2.0 才有
    	void failed(ConfigurableApplicationContext context, Throwable exception);
    }
    
```
## SpringApplicationRunListeners
-----------------------------

上面提到，`SpringApplicationRunListeners` 是`SpringApplicationRunListener`的集合，里面包括了很多`SpringApplicationRunListener`实例；`SpringApplication` 类实际上使用的是 `SpringApplicationRunListeners` 类，与 `SpringApplicationRunListener` 生命周期相同，调用各个周期的 `SpringApplicationRunListener` 。然后广播相应的事件到 `ApplicationListener`。

> 代码详见：[SpringApplicationRunListeners](https://link.juejin.im?target=https%3A%2F%2Fgithub.com%2Fspring-projects%2Fspring-boot%2Fblob%2Fmaster%2Fspring-boot-project%2Fspring-boot%2Fsrc%2Fmain%2Fjava%2Forg%2Fspringframework%2Fboot%2FSpringApplicationRunListeners.java).

### EventPublishingRunListener

`EventPublishingRunListener` 类是 `SpringApplicationRunListener`接口的实现类 ，它具有广播事件的功能。其内部使用 `ApplicationEventMulticaster`在实际刷新上下文之前发布事件。下面来看下 `EventPublishingRunListener` 类生命周期对应的事件。

### ApplicationStartingEvent

`ApplicationStartingEvent` 是 `SpringBoot` 启动开始的时候执行的事件，在该事件中可以获取到 `SpringApplication` 对象，可做一些执行前的设置，对应的调用方法是 `starting()`。

### ApplicationEnvironmentPreparedEvent

`ApplicationEnvironmentPreparedEvent` 是`SpringBoot` 对应 `Enviroment` 已经准备完毕时执行的事件，此时上下文 `context` 还没有创建。在该监听中获取到 `ConfigurableEnvironment` 后可以对配置信息做操作，例如：修改默认的配置信息，增加额外的配置信息等。对应的生命周期方法是 `environmentPrepared(environment)`；`SpringCloud` 中，引导上下文就是在这时初始化的。

### ApplicationContextInitializedEvent

当 `SpringApplication` 启动并且准备好 `ApplicationContext`，并且在加载任何 `bean` 定义之前调用了 `ApplicationContextInitializers` 时发布的事件。对应的生命周期方法是`contextPrepared()`

### ApplicationPreparedEvent

`ApplicationPreparedEvent` 是`SpringBoot`上下文 `context` 创建完成是发布的事件；但此时 `spring` 中的 `bean` 还没有完全加载完成。这里可以将上下文传递出去做一些额外的操作。但是在该监听器中是无法获取自定义 `bean` 并进行操作的。对应的生命周期方法是 `contextLoaded()`。

### ApplicationStartedEvent

这个事件是在 2.0 版本才引入的；具体发布是在应用程序上下文刷新之后，调用任何 `ApplicationRunner` 和 `CommandLineRunner` 运行程序之前。

### ApplicationReadyEvent

这个和 `ApplicationStartedEvent` 很类似，也是在应用程序上下文刷新之后之后调用，区别在于此时`ApplicationRunner` 和 `CommandLineRunner`已经完成调用了，也意味着 `SpringBoot` 加载已经完成。

### ApplicationFailedEvent

`SpringBoot` 启动异常时执行的事件，在异常发生时，最好是添加虚拟机对应的钩子进行资源的回收与释放，能友善的处理异常信息。

### demo 及各个事件的执行顺序

下面的各个事件对应的demo及打印出来的执行顺序。

*   GlmapperApplicationStartingEventListener
```java
    public class GlmapperApplicationStartingEventListener implements ApplicationListener<ApplicationStartingEvent> {
        @Override
        public void onApplicationEvent(ApplicationStartingEvent applicationStartingEvent) {
            System.out.println("execute ApplicationStartingEvent ...");
        }
    }
```

*   GlmapperApplicationEnvironmentPreparedEvent
```java
    public class GlmapperApplicationEnvironmentPreparedEvent implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
        @Override
        public void onApplicationEvent(ApplicationEnvironmentPreparedEvent applicationEnvironmentPreparedEvent) {
            System.out.println("execute ApplicationEnvironmentPreparedEvent ...");
        }
    }
```  

*   GlmapperApplicationContextInitializedEvent
```java
    public class GlmapperApplicationContextInitializedEvent implements ApplicationListener<ApplicationContextInitializedEvent> {
        @Override
        public void onApplicationEvent(ApplicationContextInitializedEvent applicationContextInitializedEvent) {
            System.out.println("execute applicationContextInitializedEvent ...");
        }
    }
```

*   GlmapperApplicationPreparedEvent
```java
    public class GlmapperApplicationPreparedEvent implements ApplicationListener<ApplicationPreparedEvent> {
        @Override
        public void onApplicationEvent(ApplicationPreparedEvent applicationPreparedEvent) {
            System.out.println("execute ApplicationPreparedEvent ...");
        }
    }
```  

*   GlmapperApplicationStartedEvent
```java
    public class GlmapperApplicationStartedEvent implements ApplicationListener<ApplicationStartedEvent> {
        @Override
        public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
            System.out.println("execute ApplicationStartedEvent ...");
        }
    }
```  

*   GlmapperApplicationReadyEvent
```java
    public class GlmapperApplicationReadyEvent implements ApplicationListener<ApplicationReadyEvent> {
        @Override
        public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
            System.out.println("execute ApplicationReadyEvent ...");
        }
    }
```  

*   执行结果

![](https://user-gold-cdn.xitu.io/2019/1/1/1680822d81776a67?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

## SpringBoot 中的事件体系
-----------------

这里围绕 `SpringApplicationRunListener` 这个类来说。在实现类 `EventPublishingRunListener` 中，事件发布有两种模式：

*   通过 `SimpleApplicationEventMulticaster` 进行事件广播
*   所有监听器交给相应的 `Context`

所以`EventPublishingRunListener` 不仅负责发布事件，而且在合适的时机将 `SpringApplication` 所获取的监听器和应用上下文作关联。

### SimpleApplicationEventMulticaster

`SimpleApplicationEventMulticaster`是 `Spring` 默认的事件广播器。来看下它是怎么工作的：
```java
    @Override
    public void multicastEvent(final ApplicationEvent event, @Nullable ResolvableType eventType) {
        ResolvableType type = (eventType != null ? eventType : resolveDefaultEventType(event));
        for (final ApplicationListener<?> listener : getApplicationListeners(event, type)) {
            Executor executor = getTaskExecutor();
            if (executor != null) {
                executor.execute(() -> invokeListener(listener, event));
            }
            else {
                invokeListener(listener, event);
            }
        }
    }
``` 

从上面的代码段可以看出，它是通过遍历注册的每个监听器，并启动来调用每个监听器的 `onApplicationEvent` 方法。

下面再来看下 `SimpleApplicationEventMulticaster` 的类集成结构：

![](https://user-gold-cdn.xitu.io/2019/1/1/1680830a7fd2c4b5?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

这里的 `AbstractApplicationContext` 下面来聊，这个类实际上就负责了事件体系的初始化工作。

### 事件体系的初始化

事件体系的初始化对应在 `SpringBoot`启动过程的 `refreshContext`这个方法；`refreshContext`具体调用 AbstractApplicationContext.refresh()方法，最后调用 initApplicationEventMulticaster() 来完成事件体系的初始化,代码如下：

![](https://user-gold-cdn.xitu.io/2019/1/1/1680835fb264f84a?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

用户可以为容器定义一个自定义的事件广播器，只要实现 `ApplicationEventMulticaster` 就可以了，`Spring` 会通过 反射的机制将其注册成容器的事件广播器，如果没有找到配置的外部事件广播器，`Spring` 就是默认使用 `SimpleApplicationEventMulticaster` 作为事件广播器。

### 事件注册

事件注册是在事件体系初始化完成之后做的事情，也是在 `AbstractApplicationContext.refresh()` 方法中进行调用的。

![](https://user-gold-cdn.xitu.io/2019/1/1/16808437ea8623d2?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

这里干了三件事：

*   首先注册静态指定的 `listeners`；这里包括我们自定义的那些监听器。
*   调用 `DefaultListableBeanFactory` 中 `getBeanNamesForType` 得到自定义的 `ApplicationListener` `bean` 进行事件注册。
*   广播早期的事件。

### 事件广播

事件发布伴随着 `SpringBoot` 启动的整个生命周期。不同阶段对应发布不同的事件，上面我们已经对各个事件进行了分析，下面就具体看下发布事件的实现：

> org.springframework.context.support.AbstractApplicationContext#publishEvent
> 
> ![](https://user-gold-cdn.xitu.io/2019/1/1/168084a716c01856?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

> earlyApplicationEvents 中的事件是广播器未建立的时候保存通知信息，一旦容器建立完成，以后都是直接通知。

广播事件最终还是通过调用 `ApplicationEventMulticaster` 的 `multicastEvent` 来实现。而 `multicastEvent` 也就就是事件执行的方法。

### 事件执行

上面 `SimpleApplicationEventMulticaster` 小节已经初步介绍了 `multicastEvent` 这个方法。补充一点， 如果有可用的 `taskExecutor` 会使用并发的模式执行事件，但是实际上 `SimpleApplicationEventMulticaster` 并没有提供线程池实现，默认请况下是使用同步的方式执行事件（`org.springframework.core.task.SyncTaskExecutor`），所以如果需要异步配置的话，需要自己去实现线程池。

SpringBoot 启动过程中的事件阶段
---------------------

这里回到 `SpringApplication`的`run`方法，看下 `SpringBoot` 在启动过程中，各个事件阶段做了哪些事情。

### starting -> ApplicationStartingEvent

这里 `debug` 到 `starting` 方法，追踪到 `multicastEvent`，这里 `type`为 `ApplicationStartingEvent`；对应的事件如下：

![](https://user-gold-cdn.xitu.io/2019/1/1/16808657f6db440c?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

*   LoggerApplicationListener：配置日志系统。使用`logging.config`环境变量指定的配置或者缺省配置
*   BackgroundPreinitializer：尽早触发一些耗时的初始化任务，使用一个后台线程
*   DelegatingApplicationListener：监听到事件后转发给环境变量`context.listener.classes`指定的那些事件监听器
*   LiquibaseServiceLocatorApplicationListener：使用一个可以和 `SpringBoot` 可执行`jar`包配合工作的版本替换 `liquibase ServiceLocator`

### listeners.environmentPrepared->ApplicationEnvironmentPreparedEvent

![](https://user-gold-cdn.xitu.io/2019/1/1/16808703538f4265?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

*   AnsiOutputApplicationListener：根据`spring.output.ansi.enabled`参数配置`AnsiOutput`
    
*   ConfigFileApplicationListener：`EnvironmentPostProcessor`，从常见的那些约定的位置读取配置文件，比如从以下目录读取`application.properties`,`application.yml`等配置文件：
    
    *   classpath:
    *   file:.
    *   classpath:config
    *   file:./config/
    
    也可以配置成从其他指定的位置读取配置文件。
    
*   ClasspathLoggingApplicationListener：对环境就绪事件`ApplicationEnvironmentPreparedEvent`/应用失败事`件ApplicationFailedEvent`做出响应，往日志`DEBUG`级别输出`TCCL(thread context class loader)`的 `classpath`。
    
*   FileEncodingApplicationListener：如果系统文件编码和环境变量中指定的不同则终止应用启动。具体的方法是比较系统属性`file.encoding`和环境变量`spring.mandatory-file-encoding`是否相等(大小写不敏感)。
    

### listeners.contextPrepared->ApplicationContextInitializedEvent

![](https://user-gold-cdn.xitu.io/2019/1/1/1680877bed3cfbbd?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

相关监听器参考上面的描述。

### listeners.contextLoaded->ApplicationPreparedEvent

![](https://user-gold-cdn.xitu.io/2019/1/1/168087a73a7666bd?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

相关监听器参考上面的描述。

### refresh->ContextRefreshedEvent

![](https://user-gold-cdn.xitu.io/2019/1/1/168087bc9054d773?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

*   ConditionEvaluationReportLoggingListener：实际上实现的是 `ApplicationContextInitializer`接口，其目的是将 `ConditionEvaluationReport` 写入到日志，使用`DEBUG`级别输出。程序崩溃报告会触发一个消息输出，建议用户使用调试模式显示报告。它是在应用初始化时绑定一个`ConditionEvaluationReportListener`事件监听器，然后相应的事件发生时输出`ConditionEvaluationReport`报告。
*   ClearCachesApplicationListener：应用上下文加载完成后对缓存做清除工作，响应事件`ContextRefreshedEvent`。
*   SharedMetadataReaderFactoryContextInitializer： 向`context`注册了一个`BeanFactoryPostProcessor`：`CachingMetadataReaderFactoryPostProcessor`实例。
*   ResourceUrlProvider：`handling mappings`处理

### started->ApplicationStartedEvent

![](https://user-gold-cdn.xitu.io/2019/1/1/168088a3a77dae6d?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

相关监听器参考上面的描述。

### running->ApplicationReadyEvent

![](https://user-gold-cdn.xitu.io/2019/1/1/168088b04deff509?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

相关监听器参考上面的描述。

### BackgroundPreinitializer&DelegatingApplicationListener

这两个贯穿了整个过程，这里拎出来单独解释下：

*   BackgroundPreinitializer：对于一些耗时的任务使用一个后台线程尽早触发它们开始执行初始化，这是`SpringBoot`的缺省行为。这些初始化动作也可以叫做预初始化。可以通过设置系统属性`spring.backgroundpreinitializer.ignore`为`true`可以禁用该机制。该机制被禁用时，相应的初始化任务会发生在前台线程。
*   DelegatingApplicationListener：监听应用事件，并将这些应用事件广播给环境属性`context.listener.classes`指定的那些监听器。

小结
--

到此，`SpringBoot` 中的事件相关的东西就结束了。本文从`SpringApplicationRunListener`这个类说起，接着介绍 `SpringBoot` 启动过程的事件以及事件的生命周期。最后介绍了 `SpringBoot`中的内置的这些 监听器在启动过程中对应的各个阶段。

参考
--

*   [blog.csdn.net/andy_zhang2…](https://link.juejin.im?target=https%3A%2F%2Fblog.csdn.net%2Fandy_zhang2007%2Farticle%2Fdetails%2F84105284)
