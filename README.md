# (dubbo-springboot) SpringBoot + Dubbo + zookeeper 搭建简单分布式服务

> springboot 简易集成 dubbo，使用 zookeeper 作为注册中心。

## 一、Dubbo 简介

## 二、搭建 zookeeper 环境
### 1、下载安装包
### 2、安装
* 2.1、 windows 环境
* 2.2、 linux 环境
	
## 三、springboot 集成 dubbo
### 1、项目目录结构
![目录结构](https://github.com/BillyYangOne/dubbo-springboot/blob/master/dubbo-interface/src/main/resources/image/structure.png)
### 2、代码编写
* 2.1、 **父项目编写**

 创建maven项目 dubbo-springboot ，添加以下依赖到 pom.xml 文件中：
```xml
   <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.1.RELEASE</version>
        <relativePath/>
    </parent>
    
    <groupId>com.billy</groupId>
    <artifactId>dubbo-springboot</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <java.version>1.8</java.version>
        <spring-boot.version>2.2.1.RELEASE</spring-boot.version>
        <dubbo.version>2.7.3</dubbo.version>
    </properties>

    <dependencies>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>


        <!--  引入 dubbo-spring-boot-starter -->
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-spring-boot-starter</artifactId>
            <version>${dubbo.version}</version>
        </dependency>

        <!-- Zookeeper dependencies -->
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-dependencies-zookeeper</artifactId>
            <version>${dubbo.version}</version>
            <type>pom</type>
            <exclusions>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-log4j12</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

    </dependencies>
```
* 2.2、 **接口项目编写**
  
为了减少冗余，编写接口项目 dubbo-interface ，为服务端和消费端提供接口。编写简单接口如下：
```java
/**
 * 接口类
 */
public interface HelloService {

    String sayHello(String name);
}
```
* 2.3、 **provider 项目编写**

服务提供者 dubbo-provider ，用于提供服务。
1. `pom.xml` 中引入接口依赖，
```xml
    <parent>
        <artifactId>dubbo-springboot</artifactId>
        <groupId>com.billy</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>dubbo-provider</artifactId>

    <dependencies>
        <!-- 引入接口模块 -->
        <dependency>
            <groupId>com.billy</groupId>
            <artifactId>dubbo-interface</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>
```
2. 编写服务层，如下：
```java
/**
 * 服务层
 */
@Service
public class HelloServiceImpl implements HelloService {

    /**
     * The default value of ${dubbo.application.name} is ${spring.application.name}
     */
    @Value("${dubbo.application.name}")
    private String serviceName;

    @Override
    public String sayHello(String name) {
        return String.format("[%s] : hell, %s", serviceName, name);
    }
}
```
注意： 此处 @Service 引自dubbo.

3. 启动类增加 `@EnableDubbo`， 用于加载 dubbo 配置
```java
@EnableDubbo
@SpringBootApplication
public class DubboProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboProviderApplication.class, args);
    }

}
``` 
4. 配置文件中增加 dubbo 配置
```properties
# Spring boot application
spring.application.name=dubbo-provider
server.port=8661

# 当前dubbo应用ID
dubbo.application.id=dubbo-provider
# 当前dubbo应用name
dubbo.application.name=dubbo-provider
# Base packages to scan Dubbo Component: @org.apache.dubbo.config.annotation.Service
dubbo.scan.base-packages=com.billy.service

# Dubbo Protocol 生产者暴露给消费者协议
dubbo.protocol.name=dubbo
# 生产者暴露给消费者端口
dubbo.protocol.port=20880

## Dubbo Registry 注册中心
dubbo.registry.address=zookeeper://xxxxx:2181
```

* 2.4、 **consumer 项目编写**
1. `pom.xml` 文件中引入接口依赖，同 2.3.1

2. 编写测试类 HelloController,如下：
```java
@RestController
public class HelloController {

    @Reference
    private HelloService helloService;

    @RequestMapping("hello")
    public String hello(String name) {

        return helloService.sayHello(name);
    }
}

```

3. 编写 `application.properties`.

 ```properties
spring.application.name=dubbo-consumer
server.port=8660

# 当前dubbo应用ID
dubbo.application.id=dubbo-consumer
# 当前dubbo应用名称
dubbo.application.name=dubbo-consumer
# 注册地址
dubbo.registry.address=zookeeper://xxxx:2181

# 生产者提供的协议ID
dubbo.protocol.id = dubbo
# 生产者提供的协议名称
dubbo.protocol.name = dubbo
# 生产者提供的协议端口号
dubbo.protocol.port = 20880
 ```
### 3、测试结果
![test result](https://github.com/BillyYangOne/dubbo-springboot/blob/master/dubbo-interface/src/main/resources/image/test_result.png)
