# Tranphongquyen_DE190908_HFS

Hệ thống bài học và thực hành Java Persistence API (JPA), Hibernate ORM và Phát triển Ứng dụng Doanh nghiệp Java.

---

## 📌 Bảng Phân bổ Lộ trình 20 Slots (Curriculum Roadmap)

| Slot | Thư mục | Chủ đề bài học | Nội dung chính | Trạng thái |
| :---: | :--- | :--- | :--- | :---: |
| **Slot 1** | `Code/Slot_01` | Tổng quan Enterprise Java & Environment Setup | Giới thiệu JDBC vs ORM, Cấu hình Maven, IntelliJ IDEA, SQL Server | 🟢 Completed |
| **Slot 2** | `Code/Slot_02` | **JPA Cơ bản - Ánh xạ 1 Entity & CRUD** | **Annotation cơ bản (`@Entity`, `@Table`, `@Id`, `@Column`, `@Enumerated`, `@Transient`), `persistence.xml`, Entity Lifecycle & CRUD `EntityManager`** | ⭐️ **ĐANG THỰC HÀNH** |
| **Slot 3** | `Code/Slot_03` | JPA Entity Mapping: Quan hệ 1 - 1 | Khai báo `@OneToOne` Uni-directional & Bi-directional, `@JoinColumn`, Cascading | ⚪️ Upcoming |
| **Slot 4** | `Code/Slot_04` | JPA Entity Mapping: Quan hệ 1 - N | Khai báo `@OneToMany` & `@ManyToOne`, `mappedBy`, CascadeTypes, Orphan Removal | ⚪️ Upcoming |
| **Slot 5** | `Code/Slot_05` | JPA Entity Mapping: Quan hệ N - N | Khai báo `@ManyToMany`, `@JoinTable`, Xử lý thuộc tính bổ sung trên Join Table | ⚪️ Upcoming |
| **Slot 6** | `Code/Slot_06` | Truy vấn JPQL (Java Persistence Query Language) | SELECT, WHERE, ORDER BY, Aggregation, JOIN, Parameters Binding | ⚪️ Upcoming |
| **Slot 7** | `Code/Slot_07` | JPQL Advanced, Native Queries & Named Queries | `@NamedQuery`, Native SQL Queries trong JPA, DTO Projection mapping | ⚪️ Upcoming |
| **Slot 8** | `Code/Slot_08` | JPA Criteria API & Dynamic Queries | Xây dựng câu truy vấn động với `CriteriaBuilder`, `CriteriaQuery`, `Root` | ⚪️ Upcoming |
| **Slot 9** | `Code/Slot_09` | JPA Inheritance Mapping Strategies | Single Table, Joined Table, Table Per Class (`@Inheritance`) | ⚪️ Upcoming |
| **Slot 10** | `Code/Slot_10` | Composite Keys & Embedded Objects | Khóa phức hợp với `@IdClass`, `@EmbeddedId` và lớp `@Embeddable` | ⚪️ Upcoming |
| **Slot 11** | `Code/Slot_11` | JPA Lifecycle Callbacks & Listeners | `@PrePersist`, `@PostPersist`, `@PreUpdate`, `@PostUpdate`, Entity Audit | ⚪️ Upcoming |
| **Slot 12** | `Code/Slot_12` | Quản lý Giao dịch & Concurrency Control | Transaction Isolation, Pessimistic Locking, Optimistic Locking (`@Version`) | ⚪️ Upcoming |
| **Slot 13** | `Code/Slot_13` | Tối ưu hóa Hiệu năng & Caching | First-level Cache, Second-level Cache, Giải quyết bài toán N+1 Query (Fetch Join) | ⚪️ Upcoming |
| **Slot 14** | `Code/Slot_14` | DAO Pattern & Giới thiệu Spring Data JPA | Chuyển đổi từ DAO truyền thống sang Spring Data JPA Repository | ⚪️ Upcoming |
| **Slot 15** | `Code/Slot_15` | Tích hợp Spring Boot với JPA / Hibernate | Cấu hình `application.properties`/`yml`, Auto-configuration, Flyway Migration | ⚪️ Upcoming |
| **Slot 16** | `Code/Slot_16` | Phát triển RESTful API với Spring Data JPA | DTO Mapping, Controller Layer, Service Layer, Response Entity | ⚪️ Upcoming |
| **Slot 17** | `Code/Slot_17` | Validation & Global Exception Handling | Jakarta Bean Validation (`@NotNull`, `@Size`, `@Email`), ControllerAdvice | ⚪️ Upcoming |
| **Slot 18** | `Code/Slot_18` | Bảo mật & Phân quyền trong JPA Application | Mã hóa mật khẩu, Authentication, Role-based Access Control (RBAC) với JWT | ⚪️ Upcoming |
| **Slot 19** | `Code/Slot_19` | Testing JPA Repositories & Integration Test | Unit Test với `@DataJpaTest`, H2 Database In-Memory, Mockito | ⚪️ Upcoming |
| **Slot 20** | `Code/Slot_20` | Tổng kết Khóa học & Đồ án Thực tế (Final Project) | Xây dựng Ứng dụng Quản lý Doanh nghiệp Hoàn chỉnh & Deployment | ⚪️ Upcoming |

---

## 🎯 Chi tiết Slot 2: JPA Cơ bản với 1 Entity (Dự án Hiện tại)

Mục tiêu của **Slot 2** là nắm vững các Annotation ánh xạ field cơ bản, cấu hình Unit Persistence và quản lý **Vòng đời Entity (Entity Lifecycle)** thông qua thao tác CRUD cơ bản trước khi học sang ánh xạ quan hệ (Slot 3 - Slot 5).

### 1. Cấu trúc Thư mục Code (`Code/Slot_02`)

```
d:/bai 1/
├── Code/
│   ├── Slot_01/
│   ├── Slot_02/                          # Thư mục thực hành Slot 2
│   │   ├── pom.xml                       # File cấu hình Maven
│   │   ├── Chapter1_JPA_Basic_guide.md   # Hướng dẫn chi tiết Slot 2
│   │   ├── README.md
│   │   └── src/
│   │       ├── main/
│   │       │   ├── java/
│   │       │   │   ├── Main.java                    # File chạy Demo luồng CRUD
│   │       │   │   └── fe/masv/
│   │       │   │       ├── dao/
│   │       │   │       │   └── EmployeeDAO.java     # Thao tác CRUD sử dụng EntityManager
│   │       │   │       └── pojo/
│   │       │   │           ├── Employee.java        # JPA Entity class
│   │       │   │           └── Gender.java          # Enum Giới tính
│   │       │   └── resources/
│   │       │       └── META-INF/
│   │       │           └── persistence.xml          # Cấu hình kết nối SQL Server & Hibernate
│   ├── Slot_03/
│   │   ...
│   └── Slot_20/
└── README.md
```

### 2. Sơ đồ CSDL & Bảng `employees` (Database Schema)

* **Database Name:** `HSF302_Chapter1`
* **Table Name:** `employees`

| Cột (DB) | Kiểu dữ liệu (SQL Server) | Ràng buộc | Ánh xạ từ Java Field | Ghi chú |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY`, `IDENTITY` | `Long id` | `@Id`, `@GeneratedValue(IDENTITY)` |
| `fullName` | `NVARCHAR(255)` | `NOT NULL` | `String fullName` | `@Column(nullable = false)` |
| `email` | `VARCHAR(255)` | `UNIQUE` | `String email` | `@Column(unique = true)` |
| `salary` | `DECIMAL(19,2)` | None | `BigDecimal salary` | Giữ độ chính xác tiền tệ |
| `gender` | `VARCHAR(255)` | None | `Gender gender` | `@Enumerated(EnumType.STRING)` |
| `hireDate` | `DATE` | None | `LocalDate hireDate` | Ánh xạ trực tiếp từ `LocalDate` |
| `active` | `BIT` | None | `boolean active` | Trạng thái làm việc |

> 💡 **Lưu ý:** Cột `yearsOfService` không tồn tại trong DB vì được đánh dấu `@Transient`.

### 3. Vòng đời của Entity (Entity Lifecycle) trong Slot 2

Trong [`EmployeeDAO.java`](file:///d:/bai%201/Code/Slot_02/src/main/java/fe/masv/dao/EmployeeDAO.java), entity trải qua 4 trạng thái:
1. **Transient (New):** `new Employee(...)` — chưa liên kết với EntityManager hay DB.
2. **Managed:** Được quản lý bởi `EntityManager` sau khi gọi `persist()`, `find()`, hoặc `merge()`.
3. **Detached:** `EntityManager` bị đóng (`em.close()`), entity không còn được tự động đồng bộ xuống DB.
4. **Removed:** Đánh dấu xóa bằng `em.remove()`, sẽ bị `DELETE` thật sự khỏi DB khi `commit()`.

---

## 📘 Tài liệu Hướng dẫn Chi tiết
Xem hướng dẫn từng bước cài đặt và giải thích sâu hơn tại: [`Chapter1_JPA_Basic_guide.md`](file:///d:/bai%201/Code/Slot_02/Chapter1_JPA_Basic_guide.md)
