## Java虚拟机--OutOfMemoryError 异常详解及解决方法
![](../images/20150414175327108)

上图是一张Java运行时的内存分布图，可知虚拟机内存都有发生OutOfMemoryError（下文称 OOM）异常的可能，作为一个合格的Java开发人员，我们应该做到的是：


（1）第一，通过代码验证 Java 虚拟机规范中描述的各个运行时区域储存的内容；

（2）第二，遇到内存溢出的时候，应该可以找打具体的位置，并进行合理的解决；

下边就聊一下 OOM：

### 一、Java 堆溢出

我们知道Java 堆用于储存对象实例，我们只要不断地创建对象，并且保证 GC Roots 到对象之间有可达路径来避免垃圾回收机制清除这些对象，就会在对象数量到达最大堆的容量限制后产生内存溢出异常（OOM）。

```java
/**
 * VM Args： -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
 * 需要在jre1.5下编译
 * @author xuliugen
 */
public class HeapOOM {
    static class OOMObject{}
    public static void main(String[] args){
        List<OOMObject> list = new ArrayList<OOMObject>();
        while(true){
            list.add(new OOMObject());
        }
    }
}
```
为了能够看到内存溢出时候的状态信息，在项目右键–run as –run configuration –Arguments



然后可以看到爆出一下错误：

```java
java.lang.OutOfMemoryError: Java heap space
Dumping heap to java_pid6424.hprof ...
Heap dump file created [28129384 bytes in 0.118 secs]
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
    at java.util.Arrays.copyOf(Unknown Source)
    at java.util.Arrays.copyOf(Unknown Source)
    at java.util.ArrayList.grow(Unknown Source)
    at java.util.ArrayList.ensureExplicitCapacity(Unknown Source)
    at java.util.ArrayList.ensureCapacityInternal(Unknown Source)
    at java.util.ArrayList.add(Unknown Source)
    at com.lc.oom.HeapOOM.main(HeapOOM.java:21)
```

Java 堆内存的 OOM 异常是实际应用中最常见的内存溢出异常情况。出现 Java 堆内存溢出时，异常堆栈信息“java.lang.OutOfMemoryError”会跟着进一步提示“Java heap space”。要解决这个区域的异常，一般的方法是首先通过内存映像分析工具（如 Eclipse Memory Analyzer）对 dump 出来的堆转储快照进行分析，重点是确认内存中的对象是否是必要的，也就是要先分清楚到底是出现了内存泄漏（ Memory Leak）还是内存溢出（ Memory Overflow）。可以使用 EclipseMemory Analyzer 打开的堆转储快照文件。（ Eclipse Memory Analyzer是一个eclipse插件，网上百度一下，很多）

如果是内存泄漏，可进一步通过工具查看泄漏对象到 GC Roots 的引用链。于是就能找到泄漏对象是通过怎样的路径与 GC Roots 相关联并导致垃圾收集器无法自动回收它们的。握了泄漏对象的类型信息，以及 GC Roots 引用链的信息，就可以比较准确地定位出泄漏代码的位置。

如果不存在泄漏，换句话说就是内存中的对象确实都还必须存活着，那就应当检查虚拟机的堆参数（ -Xmx 与-Xms），与机器物理内存对比看是否还可以调大，从代码上检查是否存在某些对象生命周期过长、持有状态时间过长的情况，尝试减少程序运行期的内存消耗。

以上是处理 Java 堆内存问题的简略思路。
