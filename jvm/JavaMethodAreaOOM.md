## Java虚拟机OOM--方法区溢出

[首页](../REAMDE.md)

方法区用于存放 Class 的相关信息，如类名、访问修饰符、常量池、字段描述、方法描述等。  
对于这个区域的测试，基本的思路是运行时产生大量的类去填满方法区，直到溢出。
虽然直接使用 JavaSE API 也可以动态产生类（如反射时的GeneratedConstructorAccessor 和动态代理等），但在本次实验中操作起来比较麻烦。
在代码清单 2-5 中，笔者借助 CGLib①直接操作字节码运行时，生成了大量的动态类。


值得特别注意的是，我们在这个例子中模拟的场景并非纯粹是一个实验，这样的应用经常会出现在实际应用中：当前的很多主流框架，如 Spring 和 Hibernate 对类进行增强时，都会使用到 CGLib 这类字节码技术，增强的类越多，就需要越大的方法区来保证动态生成的 Class 可以加载入内存。

```java

/**
 * VM Args： -XX:PermSize=10M -XX:MaxPermSize=10M
 */
public class JavaMethodAreaOOM {
    public static void main(String[] args) {
        while (true) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(OOMObject.class);
            enhancer.setUseCache(false);
            enhancer.setCallback(new MethodInterceptor() {
                public Object intercept(Object obj, Method method,
                        Object[] args, MethodProxy proxy) throws Throwable {
                    return proxy.invokeSuper(obj, args);
                }
            });
            enhancer.create();
        }
    }
    static class OOMObject {
    }
}
```

运行结果：

```shell
Caused by: java.lang.OutOfMemoryError: PermGen space
at java.lang.ClassLoader.defineClass1(Native Method)
at java.lang.ClassLoader.defineClassCond(ClassLoader.java:632)
at java.lang.ClassLoader.defineClass(ClassLoader.java:616)
... 8 more
```

方法区溢出也是一种常见的内存溢出异常，一个类如果要被垃圾收集器回收掉，判定条件是非常苛刻的。
在经常动态生成大量 Class 的应用中，需要特别注意类的回收状况。这类场景除了上面提到的程序使用了 GCLib 字节码增强外，
常见的还有：大量 JSP 或动态产生 JSP 文件的应用（ JSP 第一次运行时需要编译为Java 类）、基于 OSGi 的应用（即使是同一个类文件，
被不同的加载器加载也会视为不同的类）等。
