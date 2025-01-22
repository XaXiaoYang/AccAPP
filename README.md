# 简易记账系统

一个基于 Spring Boot 的记账系统，支持用户管理、交易记录、预算管理、数据导出等功能。

## 技术栈

### 后端
- **Spring Boot** 2.7.x：基础框架
- **Spring Security**：认证和授权
- **MyBatis-Plus**：ORM框架
- **JWT**：用户认证
- **Apache POI**：Excel导出
- **MySQL**：数据库
- **Swagger/OpenAPI**：API文档

### 工具和依赖
- **Lombok**：简化代码
- **Slf4j**：日志框架
- **Maven**：项目管理

## 功能模块

### 1. 用户管理
- 用户注册
  ```http
  POST /api/user/register
  Content-Type: application/json
  
  {
    "username": "string",
    "password": "string",
    "email": "string"
  }
  ```
- 用户登录
  ```http
  POST /api/user/login
  Content-Type: application/json
  
  {
    "username": "string",
    "password": "string"
  }
  ```

### 2. 交易管理
- 添加交易记录
  ```http
  POST /api/transactions
  Content-Type: application/json
  Authorization: Bearer {token}
  
  {
    "type": "income/expense",
    "categoryId": "long",
    "amount": "decimal",
    "description": "string",
    "transactionDate": "date"
  }
  ```
- 获取交易记录
  ```http
  GET /api/transactions
  Authorization: Bearer {token}
  ```
- 获取交易统计
  ```http
  GET /api/transactions/statistics
  Authorization: Bearer {token}
  ```

### 3. 分类管理
- 获取分类列表
  ```http
  GET /api/categories
  Authorization: Bearer {token}
  ```
- 添加自定义分类
  ```http
  POST /api/categories
  Content-Type: application/json
  Authorization: Bearer {token}
  
  {
    "name": "string",
    "type": "income/expense"
  }
  ```
- 修改分类
  ```http
  PUT /api/categories/{id}
  Authorization: Bearer {token}
  ```
- 删除分类
  ```http
  DELETE /api/categories/{id}
  Authorization: Bearer {token}
  ```

### 4. 预算管理
- 设置预算
  ```http
  POST /api/budgets
  Content-Type: application/json
  Authorization: Bearer {token}
  
  {
    "categoryId": "long",
    "amount": "decimal",
    "year": "integer",
    "month": "integer"
  }
  ```
- 获取预算列表
  ```http
  GET /api/budgets
  Authorization: Bearer {token}
  ```
- 获取预算执行情况
  ```http
  GET /api/budgets/execution
  Authorization: Bearer {token}
  ```

### 5. 数据导出
- 导出交易记录
  ```http
  GET /api/export/transactions?year={year}&month={month}
  Authorization: Bearer {token}
  ```
- 导出预算执行情况
  ```http
  GET /api/export/budget-execution?year={year}&month={month}
  Authorization: Bearer {token}
  ```
- 导出年度报表
  ```http
  GET /api/export/annual-report?year={year}
  Authorization: Bearer {token}
  ```

##  添加以下功能来完善记账系统：

    数据分析和可视化：
        收支趋势图表（按月/季度/年）
        支出分类饼图
        收入来源分析
        同比/环比分析
    预算提醒功能：
        预算使用率预警（如达到80%时提醒）
        定期预算执行情况推送
        超预算预警通知
    账单管理增强：
        批量导入交易记录（支持Excel导入）
        定期账单功能（如每月固定支出）
        账单标签功能（便于分类和查询）
        账单图片附件（如发票照片）
    个人设置功能：
        货币单位设置

## 快速开始

1. **环境要求**
    - JDK 1.8+
    - Maven 3.6+
    - MySQL 8.0+

2. **配置数据库**
    - 创建数据库：`expense_tracker`
    - 执行 SQL 脚本：`src/main/resources/db/schema.sql`

3. **配置应用**
   修改 `application.yml`：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/expense_tracker
       username: your_username
       password: your_password
   
   jwt:
     secret: your_jwt_secret_key
     expiration: 86400000
   ```

4. **运行应用**
   ```bash
   mvn spring-boot:run
   ```

5. **访问接口文档**
   ```
   http://localhost:8080/swagger-ui.html
   ```

## 注意事项

1. 所有需要认证的接口都需要在请求头中携带 JWT token：
   ```
   Authorization: Bearer your_token_here
   ```

2. 导出功能支持 Excel 格式，文件名会自动包含年月信息

3. 预算管理按月设置，不支持跨月操作

4. 分类支持系统预设和用户自定义两种类型


## GIT使用方法
项目结构
expense-tracker/
├── .git/
├── .gitignore
├── pom.xml
├── README.md
└── src/
├── main/
│   ├── java/
│   └── resources/
└── test/
# 进入项目根目录（确保是正确的项目目录）
cd expense-tracker

# 初始化 Git 仓库
git init

# 添加远程仓库
git remote add origin https://github.com/your-username/expense-tracker.git

# 创建并切换到主分支
git checkout -b dev

# 添加文件
git add .

# 检查状态
git status

# 提交更改
git commit -m "Initial commit"

# 推送到远程仓库
git push -u origin dev
 