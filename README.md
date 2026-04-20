# 技术栈：

- Spring Boot
- MyBatis / MyBatis-Plus
- MySQL
- MinIO (文件存储)
- 模块：用户、商品、交易、论坛...

***

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
│   │       ├── application-template.yml
│   │       ├── mapper
│   │       └── sql
│   │           └── SecondHand_Marketplace.sql
│   └── test
│       └── java
│           └── com
│               └── secondhand
│                   └── marketplace
│                       └── backend
│                           └── SecondhandMarketplaceBackendApplicationTests.java

├── pom.xml
├── README.md
└── starter.bat
```

***

# 二、目录树解释

## 1. `common`

放**全局公共能力**，不属于某个业务模块。

- `api`：统一返回体
- `exception`：全局异常
- `constants`：常量
- `enums`：公共枚举
- `util`：工具类
  - `MinioUtil.java`：MinIO 文件上传工具类
- `context`：登录用户上下文

***

## 2. `config`

放 Spring 配置类。

比如：

- MyBatis-Plus 分页插件
- Jackson 时间格式
- Redis 配置
- 线程池配置
- 接口文档配置
- MinIO 配置 (MinioConfig.java)

***

## 3. `security`

放登录认证相关逻辑。

比如：

- 登录拦截器
- 管理员拦截器
- 自定义注解
- 参数解析器

***

## 4. `infrastructure`(暂时已删除)

放基础设施能力。

比如：

- Redis
- 文件上传
- 短信
- 缓存

这些偏"技术支撑"，不是用户/商品/交易/论坛某个模块独有的业务。

***

## 5. `modules`

这里是**核心业务模块区**：

- `user`
- `goods`
- `trade`
- `forum`

每个模块内部再分：

- `controller`
- `service`
- `mapper`
- `entity`
- `dto`
- `vo`
- `convert`
- `enums`

这样业务边界会非常清晰。

***

## 6. `resources/sql`

放建表 SQL、初始化数据 SQL。

适合：

- 项目初始化
- 面试展示
- 团队协作
- 重新部署环境

***

# 三、后续写代码时要遵守的目录使用规则

## 1. 不要 `controller` 直接调 `mapper`

必须：

```text
controller -> service -> mapper
```

***

## 2. 模块之间尽量通过 `service` 交互

比如交易模块需要操作商品状态：

- 不要直接调 `GoodsMapper`
- 要调 `GoodsService`

***

## 3. `entity / dto / vo` 分开

不要混用。

- `entity`：数据库对象
- `dto`：前端入参
- `vo`：返回前端

***

## 4. 工具类不要乱塞业务逻辑

`util` 里放通用工具，不放"发布商品""取消订单"这种业务逻辑。

***

# 四、目录设计概括

> 采用模块化单体架构。整体上是一个 Spring Boot 应用，但代码组织上按业务拆成用户、商品、交易、论坛等模块。每个模块内部再按照 controller、service、mapper、entity、dto、vo 分层，保证职责清晰。公共能力例如统一返回体、全局异常、JWT、上下文、常量、工具类统一放在 common 和 config 中。这样既保留了单体应用开发和部署简单的优点，又能在代码层面保证模块边界清晰，便于后续扩展和维护。

***

# 五、配置文件使用方法

## 1. 配置文件结构

- `application.yml`：公共配置，提交到Git
- `application-template.yml`：配置模板文件，复制后修改使用
- `application-{环境名}.yml`：环境差异化配置，不提交到Git

## 2. 环境配置创建步骤

1. 复制 `application-template.yml` 文件
2. 重命名为 `application-{环境名}.yml`
   - 例如：`application-dev.yml`（开发环境）
   - 例如：`application-test.yml`（测试环境）
   - 例如：`application-prod.yml`（生产环境）
3. 修改复制后的配置文件，填写实际的配置值

## 3. 启动方式

### 3.1 命令行启动

通过 `--spring.profiles.active={环境名}` 指定使用的环境配置：

```bash
# Maven 启动
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# JAR 启动
java -jar app.jar --spring.profiles.active=dev
```

### 3.2 IDEA 启动配置

1. 打开 Run/Debug Configuration
2. 在 "Active Profiles" 中填写环境名称（如 `dev`、`test`）
3. 或者在 "Program arguments" 中添加 `--spring.profiles.active=dev`

### 3.3 VSCode 启动配置

在 `.vscode/launch.json` 中添加配置：

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Spring Boot-dev",
      "request": "launch",
      "cwd": "${workspaceFolder}",
      "console": "integratedTerminal",
      "mainClass": "com.secondhand.marketplace.backend.SecondhandMarketplaceBackendApplication",
      "projectName": "secondhand-marketplace-backend",
      "args": "--spring.profiles.active=dev",
      "springBoot": {
        "configLocations": ["${workspaceFolder}/src/main/resources/"]
      }
    }
  ]
}
```

### 3.4 使用 starter.bat 启动

项目提供了 `starter.bat` 脚本方便快速启动：

```bat
# 默认使用 dev 环境
starter.bat

# 指定环境
starter.bat dev
starter.bat test
```

## 4. 注意事项

- 不要将包含敏感信息的配置文件提交到Git
- `application-local.yml` 和 `application-lw.yml` 已被移除，使用 `application-template.yml` 作为模板
- 生产环境建议使用环境变量或配置中心管理敏感配置
- 确保环境配置文件在 `.gitignore` 的忽略列表中（`application-*.yml` 已被忽略）