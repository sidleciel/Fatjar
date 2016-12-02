# Fatjar
#### 1. 简介 

开发过程中经常需要将多个jar包整合成一个jar包对外输出。在eclipse上开发时有一个eclipse fatjar plugin可以帮助我们完成这项工作，但开发工具换成Android studio之后我没有找到类似功能的插件，所以就自己写了一个整合jar的小工具，然后在build.gradle中添加一个task来执行它，也实现了类似的功能。

#### 2. 使用方法 

* 将准备合并的jar的路径配置到fatjar_congig.xml中；
* 将 fatjar_config.xml 和 fatjar.jar 放在同一个目录下，双击fatjar.jar即可；
* 将 fatjar_config.xml 和 fatjar.jar 放在build.gradle文件所在目录下，添加gradle的task，执行task即可；

    task fatjar (type: JavaExec) {
        javaexec {
            main="-jar";
            args = ["fatjar.jar"]；
        }
    }

    
#### 3.源码修改

已输出端额fatjar可能并未满足场景需求，此时可以修改源码并编译成jar。

步骤如下：
1.到bin目录下打包jar。
jar -cvf fatjar.jar com

2.解压得到MANIFEST.MF，并修改Main-Class。
Manifest-Version: 1.0
Main-Class: com.echelon.fatjar.Launcher
Created-By: 1.8.0_60 (Oracle Corporation)

3.带MANIFEST重新打包
jar -cvfm fatjar.jar MANIFEST.MF com

4.执行jar测试。
java -jar fatjar.jar