# 技术栈：
* Spring Boot
* MyBatis / MyBatis-Plus
* MySQL
* MinIO (文件存储)
* 模块：用户、商品、交易、论坛...

---

# 一、完整目录树

```text
项目根目录
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── secondhand
│   │   │           └── marketplace
│   │   │               └── backend
│   │   │                   ├── common
│   │   │                   │   ├── api
│   │   │                   │   ├── constants
│   │   │                   │   ├── enums
│   │   │                   │   ├── exception
│   │   │                   │   ├── util
│   │   │                   │   │   └── MinioUtil.java
│   │   │                   │   └── context
│   │   │                   ├── config
│   │   │                   │   ├── MybatisPlusConfig.java
│   │   │                   │   └── MinioConfig.java
│   │   │                   ├── security
│   │   │                   ├── modules
│   │   │                   │   ├── user
│   │   │                   │   │   ├── controller
│   │   │                   │   │   ├── service
│   │   │                   │   │   │   └── impl
│   │   │                   │   │   ├── mapper
│   │   │                   │   │   ├── entity
│   │   │                   │   │   ├── dto
│   │   │                   │   │   ├── vo
│   │   │                   │   │   ├── convert
│   │   │                   │   │   └── enums
│   │   │                   │   ├── goods
│   │   │                   │   │   ├── controller
│   │   │                   │   │   ├── service
│   │   │                   │   │   │   └── impl
│   │   │                   │   │   ├── mapper
│   │   │                   │   │   ├── entity
│   │   │                   │   │   ├── dto
│   │   │                   │   │   ├── vo
│   │   │                   │   │   ├── convert
│   │   │                   │   │   └── enums
│   │   │                   │   ├── trade
│   │   │                   │   │   ├── controller
│   │   │                   │   │   ├── service
│   │   │                   │   │   │   └── impl
│   │   │                   │   │   ├── mapper
│   │   │                   │   │   ├── entity
│   │   │                   │   │   ├── dto
│   │   │                   │   │   ├── vo
│   │   │                   │   │   ├── convert
│   │   │                   │   │   └── enums
│   │   │                   │   └── forum
│   │   │                   │       ├── controller
│   │   │                   │       ├── service
│   │   │                   │       │   └── impl
│   │   │                   │       ├── mapper
│   │   │                   │       ├── entity
│   │   │                   │       ├── dto
│   │   │                   │       ├── vo
│   │   │                   │       ├── convert
│   │   │                   │       └── enums
│   │   │                   └── SecondhandMarketplaceBackendApplication.java
│   │   └── resources
│   │       ├── application.yml
│   │       ├── mapper
│   │       └── sql
│   │           └── lw.sql
│   └── test
│       └── java
│           └── com
│               └── secondhand
│                   └── marketplace
│                       └── backend
│                           └── SecondhandMarketplaceBackendApplicationTests.java

├── pom.xml
└── README.md
```

---

# 二、目录树解释

## 1. `common`

放**全局公共能力**，不属于某个业务模块。

* `api`：统一返回体
* `exception`：全局异常
* `constants`：常量
* `enums`：公共枚举
* `util`：工具类
  * `MinioUtil.java`：MinIO 文件上传工具类
* `context`：登录用户上下文

---

## 2. `config`

放 Spring 配置类。

比如：

* MyBatis-Plus 分页插件
* Jackson 时间格式
* Redis 配置
* 线程池配置
* 接口文档配置
* MinIO 配置 (MinioConfig.java)

---

## 3. `security`

放登录认证相关逻辑。

比如：

* 登录拦截器
* 管理员拦截器
* 自定义注解
* 参数解析器

---

## 4. `infrastructure`(暂时已删除)

放基础设施能力。

比如：

* Redis
* 文件上传
* 短信
* 缓存

这些偏“技术支撑”，不是用户/商品/交易/论坛某个模块独有的业务。

---

## 5. `modules`

这里是**核心业务模块区**：

* `user`
* `goods`
* `trade`
* `forum`

每个模块内部再分：

* `controller`
* `service`
* `mapper`
* `entity`
* `dto`
* `vo`
* `convert`
* `enums`

这样业务边界会非常清晰。

---

## 6. `resources/sql`

放建表 SQL、初始化数据 SQL。

适合：

* 项目初始化
* 面试展示
* 团队协作
* 重新部署环境

---

# 三、后续写代码时要遵守的目录使用规则

## 1. 不要 `controller` 直接调 `mapper`

必须：

```text
controller -> service -> mapper
```

---

## 2. 模块之间尽量通过 `service` 交互

比如交易模块需要操作商品状态：

* 不要直接调 `GoodsMapper`
* 要调 `GoodsService`

---

## 3. `entity / dto / vo` 分开

不要混用。

* `entity`：数据库对象
* `dto`：前端入参
* `vo`：返回前端

---

## 4. 工具类不要乱塞业务逻辑

`util` 里放通用工具，不放“发布商品”“取消订单”这种业务逻辑。

---

# 四、目录设计概括

> 采用模块化单体架构。整体上是一个 Spring Boot 应用，但代码组织上按业务拆成用户、商品、交易、论坛等模块。每个模块内部再按照 controller、service、mapper、entity、dto、vo 分层，保证职责清晰。公共能力例如统一返回体、全局异常、JWT、上下文、常量、工具类统一放在 common 和 config 中。这样既保留了单体应用开发和部署简单的优点，又能在代码层面保证模块边界清晰，便于后续扩展和维护。