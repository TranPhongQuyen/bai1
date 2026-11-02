# Student Management System (JavaFX + Spring Data JPA)

## Yêu cầu môi trường
- JDK 21, Maven 3.9+
- SQL Server (TCP/IP port 1433 bật, tài khoản `sa`)
- IntelliJ IDEA, Gluon Scene Builder (tuỳ chọn)

## Cài đặt
1. Tạo database:
   ```sql
   IF DB_ID('StudentDB') IS NULL CREATE DATABASE StudentDB;
   ```
2. Sửa `ui-module/src/main/resources/application.properties`
   (`spring.datasource.username`, `spring.datasource.password`).

## Build & Test
```bash
mvn clean install
```
Test dùng H2 in-memory, không cần SQL Server.

## Chạy ứng dụng
- IntelliJ: Run `com.example.ui.Launcher`
- Terminal:
  ```bash
  mvn -q clean install -DskipTests
  mvn -f ui-module/pom.xml spring-boot:run
  ```

## Kiến trúc
StudentController → StudentService (interface) → StudentServiceImpl → StudentRepository (JpaRepository) → SQL Server

| Module | Nội dung |
|---|---|
| data-module | Entity, Repository (Spring Data JPA), Service + ServiceImpl |
| ui-module | JavaFX (FXML, CSS), Controller, cấu hình Spring Boot |

## Ảnh chụp
![List](docs/screenshots/01-list.png)
![Add](docs/screenshots/02-add.png)
![Validation](docs/screenshots/03-validation.png)
![Delete](docs/screenshots/04-delete-confirm.png)
