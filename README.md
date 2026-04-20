# Secondhand Marketplace Backend

二手交易平台后端项目，基于 Spring Boot 构建，提供用户、商品、论坛、交易订单、钱包、售后等模块的后端接口能力，可作为前端或移动端的 API 服务。

## 技术栈

- Java 17
- Spring Boot 3.3.5
- Maven
- MyBatis-Plus
- MySQL
- Redis (暂无使用)
- MinIO
- JWT
- Swagger / OpenAPI

## 项目结构

```text
src/main/java/com/secondhand/marketplace/backend
├── common       通用返回、异常、工具类
├── config       项目配置
├── modules      业务模块
│   ├── user       用户模块
│   ├── product    商品模块
│   ├── forum      论坛模块
│   ├── trade      交易模块
│   ├── wallet     钱包模块
│   └── aftersale  售后模块
└── security     登录拦截相关
```

## 本地运行

### 1. 克隆项目

```bash
git clone <你的远端仓库地址>
cd Secondhand_Marketplace_backend
```

### 2. 准备环境

请先安装：

- JDK 17
- Maven 3.8+
- MySQL
- Redis (暂无使用)

### 3. 初始化数据库

创建数据库：

```sql
CREATE DATABASE secondhand_marketplace DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

然后执行 SQL 文件：

```text
src/main/resources/sql/SecondHand_Marketplace.sql
```

### 4. 修改配置

根据本地环境修改：

```text
src/main/resources/application.yml
```

重点检查：

- MySQL 地址、用户名、密码
- Redis 地址、端口、密码
- MinIO 地址和账号信息
- 服务端口，默认是 `8080`

### 5. 启动项目

```bash
mvn spring-boot:run
```

或先打包再运行：

```bash
mvn clean package
java -jar target/secondhand-marketplace-backend-0.0.1-SNAPSHOT.jar
```

## API 调用

项目启动后，默认 API 地址为：

```text
http://localhost:8080
```

Swagger 接口文档地址：

```text
http://localhost:8080/swagger-ui/index.html
```

可以使用浏览器打开 Swagger 页面查看和调试接口，也可以使用 Postman、Apifox 等工具调用接口。

示例：

```bash
curl http://localhost:8080
```

部分接口需要登录后携带 Token，通常在请求头中添加：

```text
Authorization: Bearer <token>
```

## 常用命令

```bash
# 编译
mvn compile

# 运行测试
mvn test

# 打包
mvn clean package

# 启动
mvn spring-boot:run
```

## 提交说明

项目已配置 `.gitignore`，会忽略 `target/`、IDE 配置、日志和临时文件。提交代码前建议检查：

```bash
git status
```
