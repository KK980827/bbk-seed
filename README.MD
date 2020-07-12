# BestBigKK-Seed
> 落地，生根，发芽，壮大！

- 做一个功能尽可能完善全面的脚手架，便于项目快速开发，避免重复性质的配置工作。
- 在使用该脚手架之前，请首先在yaml配置文件中补充数据库, Redis 等适用于自己的连接信息。
- 默认你需要执行init-doc/t_operation_log.sql文件到你的数据库中，以保证日志持久化功能可以正常使用，并按照自己的实际需要对其进行扩展。

------
#  目前已集成功能/特点：
### 1.按模块划分。
### 2. Swagger集成。
### 3. Redis集成。
### 4. Redis缓存集成。
### 5.自定义Banner
   更改/resource/banner.txt即可
### 6.Druid集成。
> 注意只能使用pom中给定的版本，其他版本无法完全仅依靠配置文件完成配置。
- 已经集成多数据源支持，在yaml配置文件中可以找到如下：
```yaml
# 数据源
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    dynamic:
      primary: master #设置默认的数据源或者数据源组,默认值即为master
      strict: false #设置严格模式,默认false不启动. 启动后在未匹配到指定数据源时候回抛出异常,不启动会使用默认数据源.
      datasource:
        master:
          driver-class-name: com.mysql.cj.jdbc.Driver
          url: URL
          username: USERNAME
          password: PASSWORD
        halo:
          driver-class-name: com.mysql.cj.jdbc.Driver
          url: URL
          username: USERNAME
          password: PASSWORD
    # 使用阿里的Druid连接池
    druid:
      # 连接池的配置信息
      # 初始化大小，最小，最大
      initial-size: 5
      min-idle: 5
      maxActive: 20
```
其中 **master** 以及 **halo** 就是代表两个数据源，如果未使用多数据源，可以只配置一个。如果配置了多个数据源，如本例中，那么可以在项目中进行动态切换，需要注意的是，可以在配置文件中
使用dynamic.primary属性指定默认的数据源名称。

切换数据源的方式有如下几种：
- 如果在项目中未明确指定数据源，那么默认使用dynamic.primary指定的数据源。
- 如果在项目中类上使用@DS("数据源名称"")，那么该类下面的所所有操作都是针对这个数据源展开的。
- 如果具体方法上也要使用不同数据源，那么@DS()注解可以修饰在方法上，此时标识该方法使用的数据源。
- 如果类上标注了@DS()，里面的方法也标注@DS，那么就近原则。

### 7.LogBack日志集成:
```xml
    <include resource="org/springframework/boot/logging/logback/base.xml"/>
    <!--彩色日志-->
    <include resource="org/springframework/boot/logging/logback/console-appender.xml"/>
    <contextName>logback</contextName>
    <logger name="com.xxx" level="debug"/>
    <property name="app.name" value="bbk-seed"/>
    <!--日志保存根目录文件夹-->
    <property name="path" value="/kk"/>
    <property name="maxHistory" value="30"/>
    <property name="maxFileSize" value="10MB"/>
```
### 8.响应数据统一格式返回:
- 在要进行响应数据包装的Controller上面，使用@RW注解。表示这个Controller下响应的数据，要进行包装。
- 该注解等同于@RestController。
- 使用@RW注解时候，需要配合使用@RequestMapping(value = "/xxx", produces = {"application/json;charset=UTF-8"})注解，必须指定给出的produces响应格式，只有该格式的
### 9.RuntimeException及业务异常包装，异常统一处理。
- 将运行中产生的异常以及常规的业务异常BusinessException进行包装。
- 包装404异常。可能会造成static目录下的静态资源无法访问。
### 10.Long型数据转为String响应:
JavaScript无法完整的表示出Java中的所有Long值，因此大的数据会出现精度丢失。所以可以直接将Long型数据转为字符串响应：
```java
@Data
class User{
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String name;
    private Integer age;
}
```
### 11.限流
在Controller上使用@AccessLimit注解，即可按照传递的值约束这个方法的可请求速率。
```java
    @GetMapping("/ac")
    @AccessLimit(perSecond = 1D, timeOut = 1, timeOutUnit = TimeUnit.SECONDS)
    public String fun() throws InterruptedException {
        return UUID.randomUUID().toString() ;
    }
```

### 12.MybatisPlus代码自动生成
> 代码生成目录没有直接指向到项目中，是为了避免生成的具有同名的文件，无意间将原来的文件覆盖。
- 生成器位于目录 test/java/com/bestbigkk/generation/。
- 进入生成器，更改相关连接信息，点击运行即可为指定的表生成代码。
- 生成的代码处于 test/java/dist/ 下。请注意：这些代码默认引入的包是约定按照各个模块进行的，因此在生成之后，你必须将这些代码按需移动到各个模块内才可以正常工作。
- 更新，现在应尽可能的使用CodeGenerator生成代码，附带了一个CURD通用操作，可以解放更多的工作量。如果实际业务不需要这些代码，也可以直接删除。
- 如果要进行自定义代码模板的话，模板位于/resources/templates/下，根据相关实际情况进行更改即可。
### 13.执行日志持久化
提供注解可以持久化某些方法发生异常时候所执行的记录信息，便于后续查看。一般来说，如果要记录方法fun()的执行记录，只需要使用@LogRecord注解修饰，并按照注解要求设置相关属性即可。在正式使用过程中，如果该方法造成了非BusinessException的异常，那么就会自动记录其执行信息到数据库。

默认待持久化的方法执行日志会存储到Redis中进行缓存，等到一定的数量之后，再批量异步插入，这个阈值可以在类LogRecordPersistence中进行调节。

```java
    @GetMapping("/e")
    @LogRecord(modelName = "测试模块", description = "123456")
    public String fun(String a, Integer b, Integer[] arr, User user) {
        throw new BusinessException("Test");
    }
```

### 14.Shiro权限验证（默认关闭）。
> 项目中已经集成Shiro并使用Jwt保持身份信息以实现无状态化。
##### 使用方式：
首先在配置配置文件中启用项目中配置的Shiro功能，如无需使用，也可以关闭：
```yaml
bbk:
  shiro:
    # 是否启用Shiro
    enable: true
    # 身份认证信息再Redis缓存中的持久时间（秒）
    cache: 3600
```
为了保证Shiro的身份认证更具通用型，项目初始阶段将核心的：用户合法性判断，身份、角色判断抽象化。在使用项目的时候，依据当前当前项目实际需求进行实现，诸如：从数据库获取用户校验 / 从其它SSO系统校验 / etc...

具体实现的时候，需要建立一个继承于 **BaseJwtRealm** 的实现类A，在这个里面实现要求的验证方法。

在实现类A完成之后，需要将其注入到容器中，后续进行验证时候将使用类A中的校验逻辑。

**BaseJwtRealm** 已经主动将Shiro的验证范围缩小到只能校验Jwt，这意味着所有的校验行为都必须依托于Jwt进行展开。

Shiro默认从请求头Authorization中获取JWT信息。

一个示例的Realm, 项目已经给出：
```java
package com.bestbigkk.web.config.shiro.authc;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import java.util.HashSet;
import java.util.Set;

/**
* @author: 开
* @date: 2020-03-26 18:05:36
* @describe: 示例Realm实现
*/
@Component
@Slf4j
public class MyJwtRealm extends BaseJwtRealm {

    private static final Set<String> TOM_ROLE_NAME_SET = new HashSet<>();
    private static final Set<String> TOM_PERMISSION_NAME_SET = new HashSet<>();
    private static final Set<String> JERRY_ROLE_NAME_SET = new HashSet<>();
    private static final Set<String> JERRY_PERMISSION_NAME_SET = new HashSet<>();

    static {
        TOM_ROLE_NAME_SET.add("admin");
        TOM_PERMISSION_NAME_SET.add("insert");
        TOM_PERMISSION_NAME_SET.add("update");
        TOM_PERMISSION_NAME_SET.add("delete");
        TOM_PERMISSION_NAME_SET.add("query");

        JERRY_ROLE_NAME_SET.add("user");
        JERRY_PERMISSION_NAME_SET.add("query");
    }

    @Override
    protected AuthorizationInfo verifyPermissionAndRolesIsValid(String jwt) {
        log.info("自定义校验权限：{}", jwt);
        SimpleAuthorizationInfo info =  new SimpleAuthorizationInfo();

        //解析Token中的信息。
        final Claims decode = jwtUtils.decode(jwt);
        final String username = (String)decode.get("username");

        if ("tom".equals(username)) {
            info.addRoles(TOM_ROLE_NAME_SET);
            info.addStringPermissions(TOM_PERMISSION_NAME_SET);
        } else if ("jerry".equals(username)) {
            info.addRoles(JERRY_ROLE_NAME_SET);
            info.addStringPermissions(JERRY_PERMISSION_NAME_SET);
        }

        return info;
    }

    @Override
    protected boolean verifyIdentityIsValid(String jwt, ServletRequest request) {
        log.info("自定义验证身份：{}", jwt);
        try {
            return !jwtUtils.isExpired(jwt);
        } catch (Exception e) {
            return false;
        }
    }

}
```
对应Controller:
```java

    /**
     * 仅具有“admin”或者“user”角色的人可以访问
     * tom jerry 可访问，未登录人员不可访问
     */
    @RequiresRoles(value = {"user", "admin"}, logical = Logical.OR)
    @GetMapping("/inner")
    public String fun2() {
        return "Hello Admin/User";
    }

    /**
     * 要求具有“insert”, "delete" 权限
     * 仅Tom可访问
     */
    @RequiresPermissions(value = {"insert", "delete"}, logical = Logical.OR)
    @GetMapping("/id")
    public String fun3() {
        return "Hello! Insert OR Delete";
    }

    /**
     * 要求具有“admin”身份和query权限
     * 仅tom可访问
     */
    @RequiresRoles(value = {"admin"})
    @RequiresPermissions(value = {"query"}, logical = Logical.OR)
    @GetMapping("/q")
    public String fun4() {
        return "Hello! Query";
    }
```

### 15.WebSocket示例集成
> 已经在项目中集成了一个WebSocket的示例，如果需求有要求，可以直接参照使用。

进行单元测试的时候，可能会出现 java.lang.IllegalStateException: javax.websocket.server.ServerContainer not available 异常。此时请在注解总引入Servlet环境。
```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BBKApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BootTest {

    @org.junit.Test
    public void test() {
       
    }

}
```

- 运行项目
- 直接通过浏览器打开resource/WebSocketDemo页面。
- 可以在页面以及控制台看到相关的连接状态信息。
- 通过页面发送信息到服务器。服务器收到信息之后会将该信息主动发送给所有连接的客户端。
- 注意使用的已经是ws协议，注意替换掉http。

### 16.Bean自定义 请求/响应 字段
- 很多时候，一个PO对象中的一些属性是不允许由前端提交值，或者响应到前端的。由此从一个完整的PO对象衍生出一些
针对不同层的DTO, BO等。他们本质上是PO的一个子集属性。并再次基础上增加了一些能够配合本层进行状态流转的对象进去。这样可以方便的在各个层之间进行流转。
- 但是如果仅针对外部传递的属性值这一点，将一个PO划分出子集对象，有时候是重复操作且会出现大量的BeanCopy操作。
- 现在集成了属性字段的自动筛选操作。无需再次进行详细的划分。
#### 16.1 如何使用：
- 首先一个实体类想要被AOP中的切面进行属性控制，那么这个实体类需要实现FiledIgnore标识接口。
- 然后在这个实体类上面使用@RequestIgnore()注解指定要从前端忽略的属性名称：
```java
@RequestIgnore(ignoreProperties = {"id"})
public class User implements FieldIgnore {
    ....
}
```
- 这种标注在类上的属性字段是全局需要进行处理的，不管Controller层哪个方法，只要有使用到这个对象，并且指定的属性在忽略列表，那么这个属性都会被AOP中的增强置空(null),
- 如果一些属性仅仅只是需要在某个方法的请求中需要被忽略，那么无需将这些请求在实体类上进行标注，只需要在对应方法的方法参数上进行标注即可，此时只有经过这个方法的请求，将按照请求参数上面标注的属性列表将其请求值忽略。
- 需要注意的是，如果一个实体类上已经标注了一些忽略属性(p1,p2,p3),那么即便在方法参数上标注了新的忽略属性(p4, p5)，那么AOP增强默认将(p1,p2,p3,p4,p5)全部进行忽略处理。
```java
@PostMapping
@ApiOperation(value = "新增一个对象")
public User add( @RequestIgnore(ignoreProperties = {"name"}) User user){
    ...
}   
```
- 在本例中，User对象中的id属性会被全局忽略，只要进入到Controller方法形参的User对象具有id值，那么都会先被置空再传递到具体方法中去。而name属性则是针对add()方法来说的，只有进入到add()方法形参列表的User对象才会被忽略id属性，name属性。
- 以此达到忽略前端属性传递的目的，而如果一些属性不想反馈给前端，则可以使用@JsonIgnore注解将具体字段忽略。以此达到属性的输入输出可控。


# 待修复
- MybatisPlus代码生成器生成的Controller中包含一些批量操作的方法，其中：批量更新接口中，原先计划将多个对象在前端解析为JSON数组传递到后端，然后后端再次解析为对象进行，并按照ID进行更新，以此达到批量更新的目的。实际上在使用过程中，如果前端传递了正确的JSON数组，后端可以解析出对应的数量，**但是无法解析出其他数据中的ID字段**，例如：
```text
传递了这样一个串：
[{"id":"1252495436177235970", "name":"QQQ","password":"QQQ"}{"id:": "125249917744", "name":"WWW","password":"WWW"}]

后端解析的结果为：
UserPO(id=1252495436177235970, name=QQQ, password=QQQ, createTime=null)
UserPO(id=null, name=WWW, password=WWW, createTime=null)

第一个数据解析正常，但是第二个就缺失了id字段的值。
```