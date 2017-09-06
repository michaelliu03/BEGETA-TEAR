### Unable to find pom.properties in /F:/IdeaProject13/BEGETA-TEAR/drools/target/classes
需要把打包之后的jar里的META-INF中的maven信息复制到classes中

spring和drools整合:<http://liureying.blog.163.com/blog/static/6151352011111810916588/>

doc:<http://docs.jboss.org/drools/release/6.3.0.Final/drools-docs/html_single/index.html>

---

+ Drools入门系列（五）——KIE概论:<http://www.tuicool.com/articles/b2yqeq>
 
<pre>
1、引言

在上一章节我们用到了几个类和他们的对象：KieServices、KieContainer、KieSession，新入门的人肯定很困惑，这几个类都是干啥的，都有什么作用啊？然后再kmodule.xml配置文件里面配置了kbase、ksession，这些东西都是什么玩意？本章以及后面可能的几章就是要解决这些问题。

2、什么是KIE？

KIE是jBoss里面一些相关项目的统称，下图就是KIE代表的一些项目，其中我们比较熟悉的就有jBPM和Drools。

这些项目都有一定的关联关系，并且存在一些通用的API，比如说涉及到构建（building）、部署（deploying）和加载（loading）等方面的，这些API就都会以KIE作为前缀来表示这些是通用的API。前面看到的一些KieServices、KieContainer、KieSession类就都是KIE的公共API。

总的来说，就是jBoss通过KIE将jBPM和Drools等相关项目进行了一个整合，统一了他们的使用方式。像KieServices这些KIE类就是整合后的结果，在Drools中这样使用，在jBPM里面也是这样使用。

kie

3、KIE项目生命周期

一个Drools应用项目其实就是一个KIE项目，KIE的生命周期其实就是Drools和jBPM这些项目的生命周期。

KIE项目生命周期包含：编写（Author）、构建（Build）、测试（Test）、部署（Deploy）、使用（Utilize）、执行（Run）、交互（Work）、管理（Manage）。

编写：编写就是编写规则文件或者流程文件；
构建：就是构建一个可以发布部署的组件，在KIE中就是构建一个jar文件；
测试：在部署到应用程序之前需要对规则或者流程进行测试；
部署：就是将jar部署到应用程序，KIE利用Maven仓库来进行发布和部署；
使用：就是加载jar文件，并通过KieContainer对jar文件进行解析，然后创建KieSession；
执行：系统通过KieSession对象的API跟Drools引擎进行交互，执行规则或者流程；
交互：用户通过命令行或者UI跟引擎进行交互；
管理：管理KieSession或者KieContainer对象。
4、KIE & Maven

通过前面的知识我们了解到Drools工程其实就是一个Maven工程，有着Maven工程标准的结构，然后Drools在这个基础上也定义了一个自己的存储结构：

drools-simple-project

drools的标准存储结构就是在src/main/resources文件夹下面存储规则文件（包括DRL文件和Excel文件），然后在META-INF文件夹下面创建一个kmodule.xml文件用来存储规则定义声明。

Drools项目最终都是打包成jar然后进行发布部署的（KIE项目生命周期提到的），这样定义工程结构和打包发布方式的根本原因就是——Maven！

kie-maven

上图描述了KIE项目（包括Drools）的打包、发布、部署过程，就是一个KIE项目按照上面定义的工程结构进行设计开发，然后通过mvn deploy命令发布到Maven仓库，然后应用程序可以通过mvn install将发布好的jar包下载安装到本地应用程序中，最后通过KieServices等API就可以直接使用这些发布好的规则了。

为什么我们写的JUnit Test类里面驱动一个规则的代码非常简单，就是因为Drools定义了上面的一套规范，按照规范来编写、发布、部署规则之后就可以确保以最简单的方式来使用Drools等KIE项目。这也是惯例优于配置的一种体现。

所以我们说一个Drools项目工程就是一个Maven项目工程，或者说一个KIE项目工程就是一个Maven工程。

KIE也提供了一种策略，能够让应用程序在运行时，能够动态监测Maven仓库中Drools项目jar组件的版本更新情况，然后可以根据配置动态更新Drools发布包，实现热插拔功能，这个是通过KieScanner API实现的。
</pre>