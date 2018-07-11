# Java虚拟机

## JVM总体梳理
JVM体系总体分为四大块：
* 类的加载机制
* jvm内存结构
* GC算法 垃圾回收
* GC分析 命令调优

思维导图
<div align="center"><img src="../../resources/images/java/jvm/jvm_structure.jpg" width="600"></div></br> 

## 类的加载机制
主要关注点：
* 什么是类的加载
* 类的生命周期
* 类加载器
* 双亲委派模型

### 什么是类的加载
类的加载指的是将类的.class文件中的二进制数据读入到内存中，将其放在运行时数据区的方法区内，然后在堆区创建一个java.lang.Class对象，用来封装类在方法区内的数据结构。类的加载的最终产品是位于堆区中的Class对象，Class对象封装了类在方法区内的数据结构，并且向Java程序员提供了访问方法区内的数据结构的接口。

### 类的生命周期
类的生命周期包括这几个部分，加载、连接、初始化、使用和卸载，其中前三部是类的加载的过程,如下图:
<div align="center"><img src="../../resources/images/java/jvm/class.png" width="600"></div></br> 

* 加载（Loading）  
查找并加载类的二进制数据，在Java堆中也创建一个java.lang.Class类的对象
* 连接    
    * 验证（Verification）  
    文件格式、元数据、字节码、符号引用验证
    * 准备（Preparation）  
    为类的静态变量分配内存，并将其初始化为默认值  
    * 解析（Resolution）  
    把类中的符号引用转换为直接引用
* 初始化（Initialization）
为类的静态变量赋予正确的初始值
* 使用（Using）  
new出对象程序中使用
* 卸载（Unloading）
执行垃圾回收    
#### 几个小问题
* JVM初始化步骤
    1. 假如这个类还没有被加载和连接，则程序先加载并连接该类
    2. 假如该类的直接父类还没有被初始化，则先初始化其直接父类
    3. 假如类中有初始化语句，则系统依次执行这些初始化语句
* 类初始化时机
    * 创建类的实例，也就是new的方式
    * 访问某个类或接口的静态变量，或者对该静态变量赋值
    * 调用类的静态方法
    * 反射（如Class.forName(“com.shengsiyuan.Test”)）
    * 初始化某个类的子类，则其父类也会被初始化
    * Java虚拟机启动时被标明为启动类的类（Java Test），直接使用java.exe命令来运行某个主类
 
* 哪几种情况下，Java虚拟机将结束生命周期？
    * 执行了System.exit()方法
    * 程序正常执行结束
    * 程序在执行过程中遇到了异常或错误而异常终止
    * 由于操作系统出现错误而导致Java虚拟机进程终止

以上问题答案参考这篇文章[jvm系列(一):java类的加载机制](http://www.cnblogs.com/ityouknow/p/5603287.html)

### 类加载器
<div align="center"><img src="../../resources/images/java/jvm/classloader.png"> </div></br> 

* __启动类加载器__(Bootstrap ClassLoader)
负责加载存放在JDK\jre\lib(JDK代表JDK的安装目录，下同)下，或被-Xbootclasspath参数指定的路径中的，并且能被虚拟机识别的类库
* __扩展类加载器__(Extension ClassLoader)
该加载器由sun.misc.Launcher$ExtClassLoader实现，它负责加载DK\jre\lib\ext目录中，或者由java.ext.dirs系统变量指定的路径中的所有类库（如javax.*开头的类），开发者可以直接使用扩展类加载器。
* __应用程序类加载器__(Application ClassLoader)
该类加载器由sun.misc.Launcher$AppClassLoader来实现，它负责加载用户类路径（ClassPath）所指定的类，开发者可以直接使用该类加载器

### 类加载机制
* 全盘负责，当一个类加载器负责加载某个Class时，该Class所依赖的和引用的其他Class也将由该类加载器负责载入，除非显示使用另外一个类加载器来载入
* 父类委托，先让父类加载器试图加载该类，只有在父类加载器无法加载该类时才尝试从自己的类路径中加载该类
* 缓存机制，缓存机制将会保证所有加载过的Class都会被缓存，当程序中需要使用某个Class时，类加载器先从缓存区寻找该Class，只有缓存区不存在，系统才会读取该类对应的二进制数据，并将其转换成Class对象，存入缓存区。这就是为什么修改了Class后，必须重启JVM，程序的修改才会生效

## JVM内存结构
主要关注点：
* jvm内存结构都是什么
* 对象分配规则

### jvm内存结构
<div align="center"><img src="../../resources/images/java/jvm/structure.png"></div></br> 

> 方法区和堆是所有线程共享的内存区域；而java栈、本地方法栈和程序计数器是运行是线程私有的内存区域。

* Java堆（Heap）,是Java虚拟机所管理的内存中最大的一块。Java堆是被所有线程共享的一块内存区域，在虚拟机启动时创建。此内存区域的唯一目的就是存放对象实例，几乎所有的对象实例都在这里分配内存。
* 方法区（Method Area）,方法区（Method Area）与Java堆一样，是各个线程共享的内存区域，它用于存储已被虚拟机加载的类信息、常量、静态变量、即时编译器编译后的代码等数据。
* 程序计数器（Program Counter Register）,程序计数器（Program Counter Register）是一块较小的内存空间，它的作用可以看做是当前线程所执行的字节码的行号指示器。
* JVM栈（JVM Stacks）,与程序计数器一样，Java虚拟机栈（Java Virtual Machine Stacks）也是线程私有的，它的生命周期与线程相同。虚拟机栈描述的是Java方法执行的内存模型：每个方法被执行的时候都会同时创建一个栈帧（Stack Frame）用于存储局部变量表、操作栈、动态链接、方法出口等信息。每一个方法被调用直至执行完成的过程，就对应着一个栈帧在虚拟机栈中从入栈到出栈的过程。
* 本地方法栈（Native Method Stacks）,本地方法栈（Native Method Stacks）与虚拟机栈所发挥的作用是非常相似的，其区别不过是虚拟机栈为虚拟机执行Java方法（也就是字节码）服务，而本地方法栈则是为虚拟机使用到的Native方法服务。



# 参考
* 《深入理解Java虚拟机》
* [jvm系列(八):jvm知识点总览](http://www.ityouknow.com/java/2017/03/01/jvm-overview.html)
* [java类的加载机制](http://www.cnblogs.com/ityouknow/p/5603287.html)