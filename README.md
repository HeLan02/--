<<<<<<< HEAD
## 教室空闲状态查询系统

按 `教室空闲状态查询系统.txt` 方案实现：教学楼/教室管理、课程表导入（Excel）、空闲教室查询、自习室推荐、教室预约与审批。

### 技术栈

- **后端**：Spring Boot（Java 21）+ Spring Web/JPA/Security + Flyway + Swagger
- **前端**：Vue 3 + Element Plus（Vite）
- **数据库**：MySQL 8（可选），默认可用 H2 内存库直接跑通

### 运行后端（推荐先用 H2）

在 `backend/` 目录执行：

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

- Swagger：`http://localhost:8080/swagger`
- H2 Console：`http://localhost:8080/h2`（JDBC URL：`jdbc:h2:mem:cfs`）

> 说明：当前演示数据已调整为 **A栋/B栋/C栋**，每栋 4 层，每层 8 间（101-108 ... 401-408）。开发演示用迁移 `V3__reseed_buildings_classrooms.sql` 会重置并重新灌入基础数据。

### 运行后端（MySQL 8）

在仓库根目录启动 MySQL：

```bash
docker compose up -d
```

MySQL root 密码：`123456`（见 `docker-compose.yml` 与后端 `application.yml` 的 mysql profile）

然后在 `backend/` 目录运行：

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

### 管理员账号

后端内置 Basic Auth 管理员：

- **username**：`admin`
- **password**：`123456`

> 需要管理员权限的接口：教学楼/教室的新增修改删除、课程导入、审批相关接口等。

### 普通用户标识

预约相关接口建议通过**请求体**传入用户信息（避免中文姓名在 Header 中被网关/容器拒绝）：

- `userId`: 学/工号
- `userName`: 姓名

也兼容仅传 `X-User-Id` / `userId`（用于“我的预约/取消”等只需要学号的接口）。

（可选）请求头方式：

- `X-User-Id`: 学/工号
- `X-User-Name`: 姓名（不推荐，可能受字符集限制）

### 关键接口（节选）

- **空闲查询**：`GET /api/classrooms/free?date=2026-05-01&startPeriod=1&endPeriod=2&buildingId=1&minCapacity=40`
- **推荐**：`GET /api/classrooms/recommend?...`
- **提交预约**：`POST /api/reservations`（需要 `X-User-Id` / `X-User-Name`）
- **我的预约**：`GET /api/reservations/my?status=0`
- **待审批**：`GET /api/reservations/pending`（管理员）
- **通过**：`PUT /api/reservations/{id}/approve`（管理员）
- **驳回**：`PUT /api/reservations/{id}/reject`（管理员）

### 课程 Excel 导入模板

`/api/courses/import` 上传 `.xlsx`，第 1 行表头，之后每行一条课程记录，列顺序固定：

1. `classroomId`
2. `courseName`
3. `dayOfWeek`（1-7）
4. `startPeriod`
5. `endPeriod`
6. `weekStart`
7. `weekEnd`
8. `teacher`

=======
# -
>>>>>>> 27ef1246e0d6c2a411fe955717ba4acf6eacd6c8
