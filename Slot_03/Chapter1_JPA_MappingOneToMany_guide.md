# Giáo Trình Chi Tiết — Slot 3: JPA Mapping One-To-Many (Quan Hệ 1-N, Cascade & Giải Quyết Vấn Đề N+1 Query)

> **Môn học:** HSF302 / Java Persistence API (JPA) & Hibernate  
> **Chủ đề Slot 3:** Ánh xạ quan hệ 1-N hai chiều (Bidirectional One-to-Many), Quản lý vòng đời thực thể bằng Cascade, Helper Methods đồng bộ và tối ưu hóa hiệu năng truy vấn với JOIN FETCH.

---

## MỤC LỤC
1. [Tổng quan & Mục tiêu học tập](#1-tổng-quan--mục-tiêu-học-tập)
2. [Cơ sở lý thuyết trọng tâm](#2-cơ-sở-lý-thuyết-trọng-tâm)
   - 2.1. Phân biệt Owning Side vs Inverse Side
   - 2.2. Thuộc tính `mappedBy` và nguyên lý hoạt động
   - 2.3. Chiến lược tải dữ liệu: FetchType.LAZY vs EAGER
   - 2.4. CascadeType và orphanRemoval
   - 2.5. Vấn đề đồng bộ 2 chiều trong bộ nhớ (In-memory Inconsistency)
3. [Cấu hình dự án (Maven & persistence.xml)](#3-cấu-hình-dự-án)
4. [Hướng dẫn triển khai chi tiết từng bước (TODO 2.1 -> TODO 2.9)](#4-hướng-dẫn-triển-khai-chi-tiết)
   - [TODO 2.1] Khai báo Enum Gender
   - [TODO 2.2] Xây dựng Entity Employee (Owning Side)
   - [TODO 2.3] Xây dựng Entity Department (Inverse Side)
   - [TODO 2.4] Xây dựng Helper Methods đồng bộ 2 chiều
   - [TODO 2.5] Xây dựng DAO cho Department và Employee
   - [TODO 2.6] JPQL JOIN FETCH theo ID
   - [TODO 2.7] Demo Cascade Persist (Lưu 1 Department kèm 3 Employee)
   - [TODO 2.8] Tái hiện vấn đề N+1 Query Problem
   - [TODO 2.9] Tối ưu hóa triệt để N+1 bằng JOIN FETCH ALL
5. [Các lỗi thường gặp & Cách khắc phục (Common Pitfalls)](#5-các-lỗi-thường-gặp)

---

## 1. TỔNG QUAN & MỤC TIÊU HỌC TẬP

Trong cơ sở dữ liệu quan hệ (RDBMS), mối quan hệ 1-Nhiều (1-N) giữa bảng `departments` và bảng `employees` được thể hiện bằng khóa ngoại (`department_id`) nằm ở bảng con (`employees`).

Trong lập trình hướng đối tượng (OOP) và JPA:
- Một **Department** có danh sách nhân viên: `List<Employee> employees`.
- Mỗi **Employee** thuộc về một phòng ban: `Department department`.

### Mục tiêu cần đạt được trong Slot 03:
1. Nắm vững cách cấu hình quan hệ 2 chiều giữa 2 Entity.
2. Hiểu bản chất bên nào là **Owning Side** (nắm giữ khóa ngoại) và bên nào là **Inverse Side**.
3. Hiểu và áp dụng đúng quy tắc **FetchType.LAZY** cho `@ManyToOne`.
4. Thành thạo cơ chế **CascadeType.ALL** và **orphanRemoval**.
5. Hiểu rõ sự nguy hiểm của **N+1 Query Problem** và cách xử lý bằng **JPQL JOIN FETCH**.

---

## 2. CƠ SỞ LÝ THUYẾT TRỌNG TÂM

### 2.1. Phân biệt Owning Side vs Inverse Side
- **Owning Side (Phía sở hữu):** Là entity tương ứng với bảng chứa cột khóa ngoại (Foreign Key) trong cơ sở dữ liệu. Ở đây chính là `Employee` (chứa cột `department_id`).
  - Phải có annotation `@ManyToOne` và `@JoinColumn(name = "department_id")`.
  - Mọi thay đổi về mối quan hệ (gán phòng ban cho nhân viên) trong DB **chỉ được Hibernate lưu khi thao tác trên Owning Side**.
- **Inverse Side (Phía đảo / Phía bị sở hữu):** Là entity tương ứng với bảng phía "1" (`Department`).
  - Khai báo `@OneToMany(mappedBy = "department")`.
  - Từ khóa `mappedBy` báo hiệu cho JPA biết: *"Tôi không giữ khóa ngoại, hãy nhìn vào thuộc tính 'department' bên trong class Employee để biết khóa ngoại là gì"*.

### 2.2. Thuộc tính `mappedBy` và nguyên lý hoạt động
- Tuyệt đối **không đặt `@JoinColumn`** cùng với `mappedBy`.
- Nếu bỏ quên `mappedBy` ở `@OneToMany`, JPA/Hibernate sẽ mặc định hiểu đây là quan hệ cần một **Bảng trung gian (Join Table)** `department_employee`, làm sai lệch hoàn toàn lược đồ CSDL.

### 2.3. Chiến lược tải dữ liệu: FetchType.LAZY vs FetchType.EAGER
- **FetchType.EAGER (Tải ngay lập tức):** Khi truy vấn entity cha, JPA sẽ tự động query load luôn entity con.
  - `@ManyToOne` và `@OneToOne` mặc định là **EAGER**.
  - **Quy tắc vàng của Hibernate:** Luôn phải ghi đè `@ManyToOne(fetch = FetchType.LAZY)` để tránh việc câu lệnh SELECT Employee vô tình kéo theo SELECT Department không cần thiết, gây chậm hệ thống.
- **FetchType.LAZY (Tải trễ):** Chỉ khi nào code gọi tới `getEmployees()` thì Hibernate mới phát lệnh SQL lấy dữ liệu.
  - `@OneToMany` mặc định là **LAZY**.
  - **Lưu ý:** Nếu gọi `getEmployees()` khi `EntityManager` đã đóng (`em.close()`), bạn sẽ gặp lỗi kinh điển `LazyInitializationException`.

### 2.4. CascadeType và orphanRemoval
- **CascadeType.ALL:** Cho phép truyền trạng thái (persist, merge, remove, refresh) từ cha xuống con. Ví dụ: gọi `em.persist(dept)` thì các `employee` trong danh sách của `dept` cũng tự động được `persist` vào DB mà không cần gọi lệnh save từng người.
- **orphanRemoval = true:** Khi bạn xóa một nhân viên ra khỏi danh sách `department.getEmployees().remove(emp)`, Hibernate sẽ tự động phát sinh câu lệnh `DELETE FROM employees WHERE id = ?` để xóa nhân viên đó khỏi CSDL.

### 2.5. Vấn đề đồng bộ 2 chiều (Bidirectional In-memory Inconsistency)
Java không tự động liên kết hai đầu tham chiếu:
- Nếu bạn chỉ viết `dept.getEmployees().add(emp);` mà không viết `emp.setDepartment(dept);`, thì khi Hibernate lưu dữ liệu, cột `department_id` của nhân viên sẽ bị **NULL** (do Employee mới là bên nắm FK).
- Do đó, bắt buộc phải viết **Helper Methods** đóng gói logic này lại.

---

## 3. CẤU HÌNH DỰ ÁN

### 3.1. `pom.xml`
```xml
<dependencies>
    <!-- Hibernate Core 6.x -->
    <dependency>
        <groupId>org.hibernate.orm</groupId>
        <artifactId>hibernate-core</artifactId>
        <version>6.5.2.Final</version>
    </dependency>

    <!-- Microsoft SQL Server JDBC Driver -->
    <dependency>
        <groupId>com.microsoft.sqlserver</groupId>
        <artifactId>mssql-jdbc</artifactId>
        <version>12.6.1.jre11</version>
    </dependency>

    <!-- Jakarta Persistence API -->
    <dependency>
        <groupId>jakarta.persistence</groupId>
        <artifactId>jakarta.persistence-api</artifactId>
        <version>3.1.0</version>
    </dependency>
</dependencies>
```

### 3.2. `src/main/resources/META-INF/persistence.xml`
Bật `hibernate.show_sql` và `hibernate.format_sql` để theo dõi các câu lệnh SQL thực tế chạy dưới nền:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="https://jakarta.ee/xml/ns/persistence" version="3.0">
    <persistence-unit name="hsf302FU" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
        <class>fe.masv.pojo.Department</class>
        <class>fe.masv.pojo.Employee</class>

        <properties>
            <property name="jakarta.persistence.jdbc.driver" value="com.microsoft.sqlserver.jdbc.SQLServerDriver"/>
            <property name="jakarta.persistence.jdbc.url" value="jdbc:sqlserver://localhost:1433;databaseName=HSF302_Chapter1;encrypt=false;trustServerCertificate=true"/>
            <property name="jakarta.persistence.jdbc.user" value="sa"/>
            <property name="jakarta.persistence.jdbc.password" value="123456"/>

            <property name="hibernate.dialect" value="org.hibernate.dialect.SQLServerDialect"/>
            <property name="hibernate.hbm2ddl.auto" value="update"/>
            <!-- Rất quan trọng để quan sát số lượng truy vấn của N+1 -->
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.format_sql" value="true"/>
        </properties>
    </persistence-unit>
</persistence>
```

---

## 4. HƯỚNG DẪN TRIỂN KHAI CHI TIẾT

### [TODO 2.1] Enum Gender
Tạo enum `Gender.java` biểu diễn giới tính:
```java
package fe.masv.pojo;

public enum Gender {
    MALE, FEMALE, OTHER
}
```

---

### [TODO 2.2] Entity Employee (Owning Side)
- Đặt `@ManyToOne(fetch = FetchType.LAZY)`
- Khai báo cột khóa ngoại: `@JoinColumn(name = "department_id", nullable = false)`
- **Lưu ý đặc biệt:** Trong hàm `toString()`, **tuyệt đối không in biến `department`** để tránh vòng lặp đệ quy gây tràn bộ nhớ StackOverflowError.

```java
package fe.masv.pojo;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String fullName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private BigDecimal salary;
    private LocalDate hireDate;
    private boolean active = true;

    // TODO 2.2: Owning side - nới giữ khóa ngoại department_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    public Employee() {}

    public Employee(String email, String fullName, Gender gender, BigDecimal salary, LocalDate hireDate) {
        this.email = email;
        this.fullName = fullName;
        this.gender = gender;
        this.salary = salary;
        this.hireDate = hireDate;
        this.active = true;
    }

    // Getters & Setters ...

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    @Override
    public String toString() {
        return "Employee{id=" + id + ", email='" + email + "', name='" + fullName + "', salary=" + salary + "}";
    }
}
```

---

### [TODO 2.3 & 2.4] Entity Department (Inverse Side & Helper Methods)
- Khai báo `@OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)`
- Tạo danh sách rỗng khởi tạo sẵn `new ArrayList<>()` để tránh NullPointerException.
- Viết 2 hàm `addEmployee` và `removeEmployee` để đồng bộ 2 chiều:

```java
package fe.masv.pojo;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String location;

    // TODO 2.3: Inverse side với mappedBy, cascade ALL và orphanRemoval
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Employee> employees = new ArrayList<>();

    public Department() {}

    public Department(String name, String location) {
        this.name = name;
        this.location = location;
    }

    // TODO 2.4: Helper method đồng bộ 2 chiều
    public void addEmployee(Employee e) {
        this.employees.add(e);
        e.setDepartment(this); // CỰC KỲ QUAN TRỌNG: Gán bên phía nắm giữ FK
    }

    public void removeEmployee(Employee e) {
        this.employees.remove(e);
        e.setDepartment(null);
    }

    // Getters & Setters ...
    public List<Employee> getEmployees() { return employees; }
}
```

---

### [TODO 2.5] Xây dựng DAO Cơ Bản
`DepartmentDAO.java` và `EmployeeDAO.java` chịu trách nhiệm quản lý transaction với các thao tác `persist()`, `merge()`, `remove()`, `find()`.

Mẫu code transaction an toàn:
```java
public void save(Department d) {
    EntityManager em = JPAUtil.getEntityManager();
    try {
        em.getTransaction().begin();
        em.persist(d);
        em.getTransaction().commit();
    } catch (RuntimeException ex) {
        if (em.getTransaction().isActive()) em.getTransaction().rollback();
        throw ex;
    } finally {
        em.close();
    }
}
```

---

### [TODO 2.6] JPQL JOIN FETCH theo ID
Khi muốn lấy một Department cùng toàn bộ danh sách nhân viên của nó trong **1 câu SQL duy nhất**:
```java
public Department findByIdWithEmployees(Long id) {
    EntityManager em = JPAUtil.getEntityManager();
    try {
        return em.createQuery(
                "SELECT d FROM Department d JOIN FETCH d.employees WHERE d.id = :id", 
                Department.class)
            .setParameter("id", id)
            .getSingleResult();
    } catch (NoResultException ex) {
        return null;
    } finally {
        em.close();
    }
}
```

---

### [TODO 2.7] Demo Cascade Persist trong Main
Chỉ cần gọi lệnh lưu đối với `Department`, toàn bộ nhân viên được thêm qua `dept.addEmployee()` sẽ tự động được insert:
```java
Department dept = new Department("IT_Development", "Ha Noi");
Employee e1 = new Employee("dev1@company.com", "Nguyen Van A", Gender.MALE, new BigDecimal("20000000"), LocalDate.now());
Employee e2 = new Employee("dev2@company.com", "Tran Thi B", Gender.FEMALE, new BigDecimal("22000000"), LocalDate.now());

// Gọi hàm helper đồng bộ
dept.addEmployee(e1);
dept.addEmployee(e2);

// Chỉ persist department, Hibernate tự động cascade persist e1 và e2
departmentDAO.save(dept);
```

---

### [TODO 2.8] Tái hiện Vấn đề N+1 Query Problem
**Khái niệm N+1:**
Khi bạn muốn lấy danh sách tất cả phòng ban kèm theo nhân viên của họ:
1. Bạn chạy 1 câu truy vấn lấy tất cả phòng ban: `SELECT d FROM Department d` -> **(1 Query)**
2. Khi lặp qua $N$ phòng ban và gọi `dept.getEmployees().size()`, do `employees` là Lazy, Hibernate bắt buộc phải gửi thêm $N$ câu truy vấn con: `SELECT * FROM employees WHERE department_id = ?` -> **(N Queries)**.
👉 Tổng số truy vấn là: **1 + N queries**. Nếu có 1.000 phòng ban, hệ thống sẽ gửi 1.001 câu SQL vào DB, gây sập server (Database Bottleneck).

Code demo tái hiện:
```java
public List<Department> findAllWithEmployeesNPlusOne() {
    EntityManager em = JPAUtil.getEntityManager();
    try {
        List<Department> list = em.createQuery("SELECT d FROM Department d", Department.class)
                .getResultList(); // Query thứ 1 (Lấy danh sách phòng ban)
        
        for (Department d : list) {
            // Mỗi lần lặp sẽ bắn thêm 1 query phụ nếu đang mở EntityManager
            int count = d.getEmployees().size(); 
            System.out.println("Dept: " + d.getName() + " -> Sĩ số: " + count);
        }
        return list;
    } finally {
        em.close();
    }
}
```

---

### [TODO 2.9] Tối ưu hóa triệt để N+1 bằng JOIN FETCH
Giải pháp chuẩn mực là dùng từ khóa **`JOIN FETCH`** kết hợp **`DISTINCT`**:
- `JOIN FETCH`: Yêu cầu Hibernate tạo một câu lệnh SQL duy nhất dùng `INNER JOIN` (hoặc `LEFT JOIN`) nạp luôn cả bảng `departments` và `employees`.
- `DISTINCT`: Loại bỏ các bản ghi cha bị nhân bản do kết quả tích đề-các của phép JOIN.

```java
public List<Department> findAllWithEmployees() {
    EntityManager em = JPAUtil.getEntityManager();
    try {
        return em.createQuery(
                "SELECT DISTINCT d FROM Department d JOIN FETCH d.employees", 
                Department.class)
            .getResultList(); // CHỈ 1 CÂU QUERY DUY NHẤT CHO TẤT CẢ!
    } finally {
        em.close();
    }
}
```

---

## 5. CÁC LỖI THƯỜNG GẶP & CÁCH KHẮC PHỤC

| Hiện tượng lỗi | Nguyên nhân | Cách khắc phục |
| :--- | :--- | :--- |
| **`java.lang.StackOverflowError`** | `toString()` của Department gọi `employees.toString()`, còn `toString()` của Employee lại gọi `department.toString()`, tạo vòng lặp vô tận. | Bỏ trường `department` khỏi hàm `toString()` trong class `Employee`. |
| **`LazyInitializationException: could not initialize proxy - no Session`** | Truy cập `dept.getEmployees()` sau khi `EntityManager` đã bị `close()`. | Dùng JPQL `JOIN FETCH` để nạp sẵn dữ liệu khi EntityManager còn mở, hoặc truy cập dữ liệu trước khi `em.close()`. |
| **Dữ liệu nhân viên có `department_id = NULL`** | Quên không gọi `e.setDepartment(this)` trong hàm helper `addEmployee()`. | Luôn sử dụng helper method đồng bộ 2 chiều khi thiết lập quan hệ. |
| **Kết quả trả về danh sách Department bị lặp lại nhiều lần** | Do phép SQL JOIN bảng 1-N sinh ra nhiều dòng có cùng ID cha. | Thêm từ khóa `DISTINCT` vào câu lệnh JPQL: `SELECT DISTINCT d FROM Department d JOIN FETCH d.employees`. |

---
*Tài liệu hướng dẫn Slot 03 — Giảng dạy & Thực hành môn HSF302.*
