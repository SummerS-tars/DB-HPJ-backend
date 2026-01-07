# LLM评估数据集管理系统 - 后端

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

一个基于 Spring Boot 3.x 的大语言模型（LLM）评估数据集管理系统后端服务，专注于管理和处理 LLM 评估所需的问答数据集。

## 📋 目录

- [项目简介](#项目简介)
- [核心功能](#核心功能)
- [技术栈](#技术栈)
- [系统要求](#系统要求)
- [快速开始](#快速开始)
- [项目结构](#项目结构)
- [API 文档](#api-文档)
- [配置说明](#配置说明)
- [开发指南](#开发指南)
- [部署说明](#部署说明)
- [贡献指南](#贡献指南)
- [许可证](#许可证)

## 🎯 项目简介

LLM评估数据集管理系统是一个专门用于管理、处理和评估大语言模型测试数据的后端服务。系统支持从多个来源（如 Stack Overflow）导入原始问答数据，进行标准化处理，生成评估数据集，并记录和分析 LLM 的评估结果。

### 主要应用场景

- 🔄 **数据导入与管理**：支持从 Stack Overflow 等平台批量导入原始问答数据
- 📊 **数据标准化**：将原始数据转换为标准化的评估问题和答案
- 🏷️ **标签和版本管理**：支持多标签分类和多版本数据集管理
- 🤖 **评估结果记录**：记录和管理不同 LLM 模型的评估结果
- 📈 **统计分析**：提供详细的数据统计和分析功能

## ✨ 核心功能

### 1. 原始数据管理
- ✅ 批量导入原始问题和答案（支持 CSV/JSON 格式）
- ✅ 原始数据的查询、筛选和分页
- ✅ 数据状态管理（待转换/已转换/已忽略）
- ✅ 支持 Stack Overflow 数据格式

### 2. 标准问题管理
- ✅ 创建和编辑标准化评估问题
- ✅ 关联原始问题到标准问题
- ✅ 多标签分类支持
- ✅ 问题难度和类型标记

### 3. 答案管理
- ✅ 候选答案的导入和筛选
- ✅ 标准答案的创建和评分
- ✅ 答案状态管理（待审核/已接受/已拒绝）
- ✅ 答案质量评估

### 4. 数据集版本管理
- ✅ 创建和管理多个数据集版本
- ✅ 为版本添加标准问题
- ✅ 版本统计和导出功能

### 5. 评估结果管理
- ✅ LLM 模型注册和管理
- ✅ 评估结果记录和查询
- ✅ 评估批次管理
- ✅ 结果分析和统计

### 6. 统计与分析
- ✅ 全局数据统计
- ✅ 分类和标签统计
- ✅ 评估结果分析
- ✅ 数据导出功能

## 🛠️ 技术栈

### 核心框架
- **Spring Boot 3.3.6** - 主应用框架
- **Spring Data JPA** - 数据持久化
- **Spring Web** - RESTful API
- **Spring Validation** - 参数校验
- **Spring Actuator** - 应用监控

### 数据库
- **MySQL 8.0+** - 生产环境数据库
- **H2 Database** - 开发和测试环境

### 工具库
- **Lombok** - 减少样板代码
- **ModelMapper 3.2.0** - DTO 映射
- **SpringDoc OpenAPI 2.3.0** - API 文档自动生成

### 构建工具
- **Maven 3.8+** - 项目构建和依赖管理
- **Java 17** - 开发语言

## 📦 系统要求

- **JDK**: 17 或更高版本
- **Maven**: 3.8 或更高版本
- **MySQL**: 8.0 或更高版本（生产环境）
- **操作系统**: Windows / Linux / macOS

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/SummerS-tars/DB-HPJ-backend.git
cd DB-HPJ-backend/llm-eval-backend
```

### 2. 配置数据库

创建 MySQL 数据库：

```sql
CREATE DATABASE llm_eval_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 配置应用

编辑 `src/main/resources/application-dev.properties` 文件，配置数据库连接：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/llm_eval_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 4. 构建项目

```bash
# 使用 Maven Wrapper（推荐）
./mvnw clean install

# 或使用本地 Maven
mvn clean install
```

### 5. 运行应用

```bash
# 开发模式
./mvnw spring-boot:run

# 或运行打包后的 jar
java -jar target/llm-eval-backend-0.0.1-SNAPSHOT.jar
```

### 6. 访问应用

- **应用主页**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API 文档**: http://localhost:8080/api-docs
- **健康检查**: http://localhost:8080/actuator/health

## 📁 项目结构

```
llm-eval-backend/
├── src/
│   ├── main/
│   │   ├── java/top/thesumst/llm_eval_backend/
│   │   │   ├── LlmEvalBackendApplication.java    # 应用启动类
│   │   │   ├── config/                            # 配置类
│   │   │   │   ├── DatabaseConfig.java
│   │   │   │   ├── ModelMapperConfig.java
│   │   │   │   └── WebConfig.java
│   │   │   ├── controller/                        # REST 控制器
│   │   │   │   ├── RawQuestionController.java    # 原始问题 API
│   │   │   │   ├── RawAnswerController.java      # 原始答案 API
│   │   │   │   ├── StandardQuestionController.java # 标准问题 API
│   │   │   │   ├── StandardAnswerController.java   # 标准答案 API
│   │   │   │   ├── CandidateAnswerController.java  # 候选答案 API
│   │   │   │   ├── VersionController.java          # 版本管理 API
│   │   │   │   ├── TagController.java              # 标签管理 API
│   │   │   │   ├── EvaluationTagController.java    # 评估标签 API
│   │   │   │   ├── EvaluationResultController.java # 评估结果 API
│   │   │   │   ├── EvaluationAnalysisController.java # 评估分析 API
│   │   │   │   ├── AnalysisTagController.java      # 分析标签 API
│   │   │   │   └── StatisticsController.java       # 统计 API
│   │   │   ├── dto/                               # 数据传输对象
│   │   │   │   ├── request/                       # 请求 DTO
│   │   │   │   └── response/                      # 响应 DTO
│   │   │   ├── entity/                            # 实体类
│   │   │   │   ├── RawQuestion.java              # 原始问题实体
│   │   │   │   ├── RawAnswer.java                # 原始答案实体
│   │   │   │   ├── StandardQuestion.java         # 标准问题实体
│   │   │   │   ├── StandardAnswer.java           # 标准答案实体
│   │   │   │   ├── CandidateAnswer.java          # 候选答案实体
│   │   │   │   ├── Version.java                  # 版本实体
│   │   │   │   ├── Tag.java                      # 标签实体
│   │   │   │   ├── EvaluationTag.java            # 评估标签实体
│   │   │   │   ├── EvaluationResult.java         # 评估结果实体
│   │   │   │   ├── EvaluationAnalysis.java       # 评估分析实体
│   │   │   │   ├── AnalysisTag.java              # 分析标签实体
│   │   │   │   └── enums/                        # 枚举类
│   │   │   ├── repository/                        # 数据访问层
│   │   │   │   ├── RawQuestionRepository.java
│   │   │   │   ├── RawAnswerRepository.java
│   │   │   │   └── ... (其他 Repository)
│   │   │   ├── service/                           # 服务层
│   │   │   │   ├── RawQuestionService.java
│   │   │   │   ├── RawAnswerService.java
│   │   │   │   └── ... (其他 Service)
│   │   │   └── exception/                         # 异常处理
│   │   │       ├── BusinessException.java
│   │   │       ├── ErrorCode.java
│   │   │       └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       ├── application.properties              # 主配置文件
│   │       ├── application-dev.properties          # 开发环境配置
│   │       ├── application-prod.properties         # 生产环境配置
│   │       └── application-local.properties        # 本地环境配置
│   └── test/                                       # 测试代码
├── documents/                                      # 项目文档
│   ├── api/                                        # API 文档
│   ├── development/                                # 开发文档
│   ├── examples/                                   # 使用示例
│   └── support/                                    # 支持文档
├── pom.xml                                         # Maven 配置
├── mvnw                                            # Maven Wrapper (Unix)
├── mvnw.cmd                                        # Maven Wrapper (Windows)
└── README.md                                       # 项目说明
```

## 📚 API 文档

系统提供了完整的 RESTful API，所有 API 都以 `/api/v1` 作为基础路径。

### 主要 API 端点

#### 原始数据管理
- `POST /api/v1/raw-questions/import` - 批量导入原始问题
- `GET /api/v1/raw-questions` - 查询原始问题列表
- `GET /api/v1/raw-questions/{id}` - 获取单个原始问题详情
- `PATCH /api/v1/raw-questions/{id}/status` - 更新问题状态

#### 标准问题管理
- `POST /api/v1/standard-questions` - 创建标准问题
- `GET /api/v1/standard-questions` - 查询标准问题列表
- `PUT /api/v1/standard-questions/{id}` - 更新标准问题
- `POST /api/v1/standard-questions/{id}/tags` - 添加标签

#### 答案管理
- `POST /api/v1/candidate-answers/import` - 导入候选答案
- `GET /api/v1/candidate-answers` - 查询候选答案列表
- `PATCH /api/v1/candidate-answers/{id}/status` - 更新答案状态
- `POST /api/v1/standard-answers` - 创建标准答案

#### 版本管理
- `POST /api/v1/versions` - 创建新版本
- `GET /api/v1/versions` - 查询版本列表
- `POST /api/v1/versions/{id}/questions` - 添加问题到版本

#### 评估管理
- `POST /api/v1/evaluation-tags` - 注册评估模型
- `POST /api/v1/evaluation-results/import` - 导入评估结果
- `GET /api/v1/evaluation-results` - 查询评估结果

#### 统计信息
- `GET /api/v1/statistics/overall` - 获取全局统计
- `GET /api/v1/statistics/by-tag` - 按标签统计
- `GET /api/v1/statistics/by-status` - 按状态统计

### Swagger UI

访问 http://localhost:8080/swagger-ui.html 查看完整的交互式 API 文档。

详细的 API 文档请参考：
- [API 文档主文档](documents/api/api.md)
- [API 简易参考](documents/api/api_simple_reference.md)
- [前端 API 文档](documents/api/supplement/PHASE7_FRONTEND_API_DOCUMENTATION_UPDATED.md)

## ⚙️ 配置说明

### 环境配置

系统支持多环境配置，通过 `spring.profiles.active` 属性切换：

- **dev**: 开发环境（默认）
- **prod**: 生产环境
- **local**: 本地环境

### 开发环境配置示例

```properties
# application-dev.properties
spring.datasource.url=jdbc:mysql://localhost:3306/llm_eval_db
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 生产环境配置示例

```properties
# application-prod.properties
spring.datasource.url=jdbc:mysql://${DB_HOST}:3306/${DB_NAME}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
```

### 关键配置项

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `server.port` | 服务端口 | 8080 |
| `spring.jpa.hibernate.ddl-auto` | 数据库表生成策略 | validate |
| `spring.jpa.show-sql` | 是否显示 SQL | true (dev) |
| `logging.level.top.thesumst` | 日志级别 | DEBUG (dev) |

## 👨‍💻 开发指南

### 开发环境设置

1. **安装 JDK 17**
   ```bash
   # 检查 Java 版本
   java -version
   ```

2. **安装 Maven**
   ```bash
   # 检查 Maven 版本
   mvn -version
   ```

3. **安装 MySQL**
   ```bash
   # 启动 MySQL 服务
   mysql -u root -p
   ```

4. **导入项目到 IDE**
   - IntelliJ IDEA (推荐)
   - Eclipse
   - VS Code

### 代码规范

- 遵循 Java 标准编码规范
- 使用 Lombok 减少样板代码
- 实体类使用 JPA 注解
- 控制器使用 REST 风格
- 统一的异常处理
- 完整的 API 注释

### 测试

```bash
# 运行所有测试
./mvnw test

# 运行特定测试类
./mvnw test -Dtest=RawQuestionServiceTest

# 生成测试覆盖率报告
./mvnw test jacoco:report
```

### 数据库迁移

使用 JPA 自动生成或手动创建数据库表结构。开发环境建议使用 `ddl-auto=update`，生产环境使用 `ddl-auto=validate`。

### 调试

```bash
# 启用远程调试
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

## 🚢 部署说明

### 打包应用

```bash
# 清理并打包
./mvnw clean package

# 跳过测试打包
./mvnw clean package -DskipTests
```

### 运行 JAR

```bash
# 默认配置运行
java -jar target/llm-eval-backend-0.0.1-SNAPSHOT.jar

# 指定配置文件运行
java -jar target/llm-eval-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

# 指定端口运行
java -jar target/llm-eval-backend-0.0.1-SNAPSHOT.jar --server.port=9090
```

### Docker 部署

```bash
# 构建 Docker 镜像
docker build -t llm-eval-backend:latest .

# 运行容器
docker run -d \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=mysql \
  -e DB_NAME=llm_eval_db \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  --name llm-eval-backend \
  llm-eval-backend:latest
```

### 使用 Docker Compose

```bash
# 启动所有服务
docker-compose up -d

# 停止服务
docker-compose down

# 查看日志
docker-compose logs -f
```

## 📖 相关文档

- [后端开发计划](documents/后端开发计划.md)
- [API 文档](documents/api/api.md)
- [候选答案使用示例](documents/examples/candidate_answers_usage_examples.md)
- [统计模块文档](documents/support/statistics.md)

## 🤝 贡献指南

我们欢迎任何形式的贡献！

### 贡献流程

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

### 代码审查

- 确保代码符合项目规范
- 添加必要的测试用例
- 更新相关文档
- 通过所有 CI 检查

## 📝 许可证

本项目采用 MIT 许可证。详见 [LICENSE](LICENSE) 文件。

## 👥 作者

- **SummerS-tars** - *Initial work* - [GitHub](https://github.com/SummerS-tars)

## 🙏 致谢

- Spring Boot 团队提供的优秀框架
- 所有贡献者的辛勤付出
- 开源社区的支持和帮助

## 📮 联系方式

- 项目地址: https://github.com/SummerS-tars/DB-HPJ-backend
- Issue 追踪: https://github.com/SummerS-tars/DB-HPJ-backend/issues

---

**注意**: 这是一个正在积极开发中的项目，部分功能可能还在完善中。如有问题请提交 Issue。
