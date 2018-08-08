## Java虚拟机OOM--运行时常量池溢出

如果要向运行时常量池中添加内容，最简单的做法就是使用 String.intern()这个 Native 方法。 
该方法的作用是：如果池中已经包含一个等于此 String 对象的字符串，则返回代表池中这个字符串的String 对象；否则，将此 String 对象包含的字符串添加到常量池中，并且返回此 String 对象的引用。  
由于常量池分配在方法区内，我们可以通过-XX:PermSize 和-XX:MaxPermSize 限制方法区的大小，从而间接限制其中常量池的容量代码运行时常量池导致的内存溢出异常

```java
/**
 * VM Args： -XX:PermSize=10M -XX:MaxPermSize=10M
 */
public class RuntimeConstantPoolOOM {
    public static void main(String[] args) {
        // 使用 List 保持着常量池引用，避免 Full GC 回收常量池行为
        List<String> list = new ArrayList<String>();
        // 10MB 的 PermSize 在 integer 范围内足够产生 OOM 了
        int i = 0;
        while (true) {
            list.add(String.valueOf(i++).intern());
        }
    }
}
```

运行结果：

```shell
Exception in thread "main" java.lang.OutOfMemoryError: PermGen space
at java.lang.String.intern(Native Method)
at org.fenixsoft.oom.RuntimeConstantPoolOOM.main(RuntimeConstantPoolOOM.java:18)
```

从运行结果中可以看到，运行时常量池溢出，在 OutOfMemoryError 后面跟随的提示信息是“PermGenspace”，说明运行时常量池属于方法区（ HotSpot 虚拟机中的永久代）的一部分。
