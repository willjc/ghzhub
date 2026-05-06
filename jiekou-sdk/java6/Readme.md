# API网关Java-SDK使用指南
## 1 SDK简介

欢迎使用API网关开发者工具套件(SDK)。API网关SDK是根据您自定义的所有API接口，自动生成的Java调用代码，让您无需复杂编程即可访问API网关服务。这里向您介绍如何使用API网关SDK。

API网关中的API是以App为单元来组织的，所有下载下来的API代码是所属某个一个app，{{appName}}代表这个App的名字。

代码文件的层级结构如下：

* SDK文件夹
    * RELEASE       `RELEASE环境下的App代码`
        * ShieldAsyncApp_{{appName}}.java `下载下来的应用调用代码，异步的调用方式来调用App中的API`
        * ShieldSyncApp_{{appName}}.java `下载下来的应用调用代码，同步的调用方式来调用App中的API`
    * lib 
        * api-gw-sdk-java-x.x.x.jar `sdk的java包`
    * Readme.md `本SDK使用指南`
    * LICENSE `版权许可`

 **重要提示：`AppKey`和`AppSecret`是网关认证用户请求的密钥，这两个配置如果保存在客户端，请妥善保管。** 

## 2 SDK使用
### 2.1 环境准备

 1. API网关Java SDK适用于`JDK 1.6`及以上版本
 2. sdk线程数修改：获取到sdk的jar后将sdk jar包使用解压软件打开，修改build.properties中线程数以及每路线程数。sdk中默认线程数为200，每路线程数20，若配置文件中不进行配置则使用默认值； 
 3. 将SDK需要的依赖加入项目的pom
 4. 在pom.xml中添加如下依赖：这些依赖是SDK需要的
 

```xml
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
        <version>3.5</version>
    </dependency>
    <dependency>
        <groupId>commons-io</groupId>
        <artifactId>commons-io</artifactId>
        <version>2.4</version>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-collections4</artifactId>
        <version>4.1</version>
    </dependency>
    <dependency>
        <groupId>org.apache.httpcomponents</groupId>
        <artifactId>httpclient</artifactId>
        <version>4.5.2</version>
    </dependency>
    <dependency>
        <groupId>org.apache.httpcomponents</groupId>
        <artifactId>httpmime</artifactId>
        <version>4.5.3</version>
    </dependency>
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.7</version>
    </dependency>
    <dependency>
        <groupId>org.java-websocket</groupId>
        <artifactId>Java-WebSocket</artifactId>
        <version>1.3.4</version>
    </dependency>
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
        <version>1.2.83</version>
</dependency>
    <!-- 云锁sdk -->
    <dependency>
    	<groupId>com.icloudlock</groupId>
    	<artifactId>icloudlock-sdk</artifactId>
    	<version>0.2.0</version>
    </dependency>
    <!-- 云锁sdk -->
    <!-- 国密加密 -->
    <!-- https://mvnrepository.com/artifact/org.bouncycastle/bcprov-jdk15on -->
    <dependency>
        <groupId>org.bouncycastle</groupId>
        <artifactId>bcprov-jdk15on</artifactId>
        <version>1.66</version>
    </dependency>
```
        

### 2.2 引入SDK的API接口调用类
1. 将SDK jar包加入到工程
2. 把sdk文件夹中同步代码`ShieldSyncApp_{{appName}}.java`或者`ShieldAsyncApp_{{appName}}.java`复制到您的项目中；
3. 修正这些类文件的package；
4. BaseApp的成员icloudlockEnabled 控制是否开启云锁

### 2.3 使用API
1. 同步方式使用API
 ```java
    // 创建 app对象
    ShieldSyncApp_Hello app = new ShieldSyncApp_Hello();

    //调用api
    ApiResponse apiResponse = app.test("hahhahaha");
    printResponse(apiResponse);

    private static void printResponse(ApiResponse response) {
        try {
            System.out.println("response code = " + response.getStatusCode());
            System.out.println("response content = " + new String(response.getBody(), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 ```
2. 异步方式使用API
```java
    // 创建 app对象
    ShieldAsyncApp_Hello app = new ShieldAsyncApp_Hello();

    //调用api
    app.test("hahhahaha", new ApiCallback() {

        @Override
        public void onResponse(ApiRequest request, ApiResponse response) {
            printResponse(response);
        }

        @Override
        public void onFailure(ApiRequest request, Exception e) {
            System.out.println(e);
        }
    });
    
    private static void printResponse(ApiResponse response) {
        try {
            System.out.println("response code = " + response.getStatusCode());
            System.out.println("response content = " + new String(response.getBody(), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

3. 调用websocket API
       //注意，该API参数请在生成的模板中直接替换参数值即可
      ShieldWebSocketApp_opal.WS_country country = ShieldWebSocketApp_opal.getInstance().new WS_country();
      country.connect();
      country.send("hello world");