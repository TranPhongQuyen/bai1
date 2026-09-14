# Hướng dẫn chi tiết — Bài 0: JPA cơ bản với 1 Entity (chưa Mapping)

> Tài liệu đi kèm `Chapter1_JPA_Basic.md`. Mục tiêu: nắm chắc annotation cơ bản + CRUD + entity lifecycle **trước khi** học sang quan hệ (OneToOne/OneToMany/ManyToMany).

---

## 0. Tạo dự án trong IntelliJ (Step-by-step)

### Bước 1 — Mở New Project

1. Mở IntelliJ → màn hình chào → **New Project** (hoặc `File → New → Project...` nếu đang mở project khác).
2. Panel bên trái chọn **Java**.
3. **Build system:** chọn **Maven**.
4. **JDK:** chọn **21** (nếu chưa có, bấm dropdown → `Add SDK → Download JDK` → Version 21 → Vendor tùy ý, ví dụ Eclipse Temurin → Download).

### Bước 2 — Advanced Settings: khai báo GroupId / ArtifactId

Đây là bước hay bị bỏ qua nhất vì mặc định bị ẩn/thu gọn:

1. Trong màn hình **New Project**, tìm dòng **Advanced Settings** (thường nằm phía dưới ô chọn JDK, dạng thanh có thể bấm mở rộng — click vào để xổ ra).
2. Sau khi mở rộng, điền đúng 3 ô:

   | Trường | Giá trị nhập | Ý nghĩa |
   |---|---|---|
   | **GroupId** | `fe.masv` | Định danh tổ chức/nhóm — tương đương gốc package Java của dự án |
   | **ArtifactId** | `Chapter1-jpa-basic` | Tên dự án — cũng là tên thư mục gốc và tên file `.jar` khi build |
   | **Version** | để mặc định `1.0-SNAPSHOT` | Không cần đổi cho bài tập |

3. Trường **Name** ở phía trên (ngoài Advanced Settings) nên đặt trùng hoặc gần giống ArtifactId, ví dụ `Chapter1-jpa-basic`.
4. Trường **Location** chọn nơi lưu project trên máy (ví dụ trong thư mục `Chapter1` bạn đang làm bài).

> **Vì sao GroupId/ArtifactId quan trọng?** Maven dùng cặp `GroupId:ArtifactId:Version` để định danh duy nhất 1 project — giống như tọa độ. GroupId theo convention thường viết dạng domain ngược (`com.company`, `fe.masv`...) và cũng thường được dùng làm **gốc package Java** trong source code (ví dụ package `fe.masv.pojo`, `fe.masv.dao` ở các bước sau) để nhất quán giữa cấu hình Maven và cấu trúc thư mục code.

### Bước 3 — Tạo project và chờ đồng bộ

1. Bấm **Create**.
2. IntelliJ sinh ra project với `pom.xml` tối thiểu; đợi vài giây để Maven tải index lần đầu (xem thanh trạng thái góc dưới).
3. Kiểm tra `pom.xml` vừa tạo đã có đúng:
   ```xml
   <groupId>fe.masv</groupId>
   <artifactId>Chapter1-jpa-basic</artifactId>
   <version>1.0-SNAPSHOT</version>
   ```

### Bước 4 — Cấu trúc dự án sau khi tạo

```
Chapter1-jpa-basic/
├── pom.xml
├── .gitignore                      ← IntelliJ tự sinh (tuỳ chọn)
└── src/
    ├── main/
    │   ├── java/                   ← chuột phải → New Package để tạo fe.masv...
    │   └── resources/              ← nơi tạo thư mục META-INF/persistence.xml
    └── test/
        └── java/                   ← chưa dùng tới trong bài này
```

Sau khi tạo package, cấu trúc đích cần đạt được cho bài này:

```
src/main/java/
├── fe/masv/pojo/
│   ├── Employee.java
│   └── Gender.java
├── fe/masv/dao/
│   └── EmployeeDAO.java
└── Main.java

src/main/resources/
└── META-INF/
    └── persistence.xml
```

**Cách tạo:** chuột phải `src/main/java` → **New → Package** → gõ liền `fe.masv.pojo` (IntelliJ tự tạo đủ 3 cấp thư mục `fe/masv/pojo`); làm tương tự cho `fe.masv.dao`. `Main.java` đặt trực tiếp ở gốc `src/main/java` (không nằm trong package nào) để chạy nhanh, hoặc bạn có thể đặt trong `fe.masv` cũng được — miễn nhất quán.

### Bước 5 — Cấu hình `pom.xml`

Mở `pom.xml`, sửa lại thành đầy đủ như sau (giữ nguyên `groupId`/`artifactId` IntelliJ đã sinh ở Bước 2, chỉ thêm `properties` và `dependencies`):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>fe.masv</groupId>
    <artifactId>Chapter1-jpa-basic</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

   <dependencies>
        <!-- Source: https://mvnrepository.com/artifact/org.hibernate.orm/hibernate-core -->
        <!-- Hibernate Core dependency for JPA implementation -->
        <dependency>
            <groupId>org.hibernate.orm</groupId>
            <artifactId>hibernate-core</artifactId>
            <version>6.5.2.Final</version>
        </dependency>
        <!-- MSSQL JDBC Driver -->
        <!-- Source: https://mvnrepository.com/artifact/com.microsoft.sqlserver/mssql-jdbc -->
        <dependency>
            <groupId>com.microsoft.sqlserver</groupId>
            <artifactId>mssql-jdbc</artifactId>
            <version>12.6.1.jre11</version>
            <scope>compile</scope>
        </dependency>
        <!-- Jakarta Persistence API -->
        <!-- Source: https://mvnrepository.com/artifact/jakarta.persistence/jakarta.persistence-api -->
        <dependency>
            <groupId>jakarta.persistence</groupId>
            <artifactId>jakarta.persistence-api</artifactId>
            <version>3.1.0</version>
        </dependency>

        <!-- Lombok dependency for reducing boilerplate code -->
        <!-- Source: https://mvnrepository.com/artifact/org.projectlombok/lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.34</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

</project>
```

Sau khi lưu file, bấm nút **reload Maven** (biểu tượng 2 mũi tên tròn ở góc tool window Maven bên phải, hoặc icon chữ "m" nổi lên góc trên bên phải editor) để tải 3 dependency về.

**Tự kiểm tra Bước 5:**
- [ ] `pom.xml` có đúng `groupId=fe.masv`, `artifactId=Chapter1-jpa-basic`.
- [ ] Sau khi reload Maven, thư mục **External Libraries** trong tool window bên trái xuất hiện `hibernate-core`, `mssql-jdbc`, `jakarta.persistence-api`.
- [ ] Không có dòng đỏ/gạch chân báo lỗi ở phần `<dependencies>`.

### Bước 6 — Tạo `persistence.xml`

1. Chuột phải `src/main/resources` → **New → Directory** → gõ `META-INF`.
2. Chuột phải `META-INF` → **New → File** → gõ `persistence.xml`.
3. Dán nội dung ở mục 2.3 bên dưới (đã cập nhật `<class>fe.masv.pojo.Employee</class>` đúng package mới).

### Bước 7 — Viết code và chạy thử

Tiếp tục theo mục 1 → 2 bên dưới: tạo `Gender.java`, `Employee.java` trong package `fe.masv.pojo`, `EmployeeDAO.java` trong `fe.masv.dao`, và `Main.java` ở gốc `src/main/java`. Chạy thử `Main.main()` — nếu console hiện `Hibernate: create table employees (...)` là project đã setup đúng.

---

## 1. Cách tiếp cận

Bài này cố tình **không có** annotation quan hệ để người học tập trung vào 2 thứ cốt lõi:

1. **Annotation ánh xạ field cơ bản** (`@Id`, `@Column`, `@Enumerated`, `@Transient`...) — nền tảng bắt buộc trước khi học quan hệ.
2. **Vòng đời entity qua từng thao tác CRUD** — vì đây là thứ quyết định method nào (`persist`/`merge`/`remove`/`find`) dùng đúng lúc nào.

Nên làm theo thứ tự: tạo entity (0.1) → đăng ký `persistence.xml` (0.2) → viết từng thao tác CRUD riêng lẻ và test độc lập (0.3 → 0.7) → ráp thành luồng demo đầy đủ (0.8) → thử case lỗi (0.9) → cuối cùng mới viết giải thích lifecycle (0.10), vì phải chạy thực tế mới hiểu rõ entity đang ở trạng thái nào.

---

## 2. Code mẫu tham khảo

### 2.1 `Gender.java`

```java
package fe.masv.pojo;

public enum Gender {
    MALE, FEMALE, OTHER
}
```

### 2.2 `Employee.java`

```java
package fe.masv.pojo;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true)
    private String email;

    private BigDecimal salary;

    // Luôn dùng STRING, KHÔNG dùng mặc định ORDINAL (số thứ tự dễ sai khi enum thay đổi)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    // JPA 2.2+ map LocalDate trực tiếp, không cần @Temporal
    private LocalDate hireDate;

    private boolean active;

    // KHÔNG có cột tương ứng trong DB — tính toán ngay khi gọi getter
    @Transient
    private int yearsOfService;

    public Employee() {
    }

    public Employee(String fullName, String email, BigDecimal salary,
                     Gender gender, LocalDate hireDate) {
        this.fullName = fullName;
        this.email = email;
        this.salary = salary;
        this.gender = gender;
        this.hireDate = hireDate;
        this.active = true;
    }

    // yearsOfService không lưu DB, tính lại mỗi lần gọi dựa trên hireDate hiện có
    public int getYearsOfService() {
        if (hireDate == null) return 0;
        return Period.between(hireDate, LocalDate.now()).getYears();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "Employee{id=" + id + ", fullName='" + fullName + "', email='" + email
                + "', salary=" + salary + ", gender=" + gender + ", hireDate=" + hireDate
                + ", active=" + active + ", yearsOfService=" + getYearsOfService() + "}";
    }
}
```

### 2.3 `persistence.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence
                                 https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd"
             version="3.0">
    <persistence-unit name="hsf302FU" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>

        <class>fu.se123456.pojo.Employee</class>

        <properties>
            <property name="jakarta.persistence.jdbc.driver"
                      value="com.microsoft.sqlserver.jdbc.SQLServerDriver"/>
            <property name="jakarta.persistence.jdbc.url"
                      value="jdbc:sqlserver://localhost:1433;databaseName=HSF302_Chapter1;encrypt=false;trustServerCertificate=true"/>
            <property name="jakarta.persistence.jdbc.user" value="sa"/>
            <property name="jakarta.persistence.jdbc.password" value="sa"/>

            <property name="hibernate.dialect" value="org.hibernate.dialect.SQLServerDialect"/>
            <property name="hibernate.hbm2ddl.auto" value="update"/>
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.format_sql" value="true"/>
        </properties>
    </persistence-unit>
</persistence>

```

### 2.4 `EmployeeDAO.java` — đầy đủ CRUD

```java
package fe.masv.dao;

import fe.masv.pojo.Employee;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

public class EmployeeDAO {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("hsf301PU");

    // ---------- CREATE (TODO 0.3) ----------
    public void save(Employee e) {
        // Truoc dong nay: e dang o trang thai NEW/TRANSIENT
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(e); // -> e chuyen sang MANAGED, se duoc INSERT khi commit
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close(); // sau dong nay, e (neu con giu tham chieu) la DETACHED
        }
    }

    // ---------- READ (TODO 0.4) ----------
    public Employee findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Employee.class, id); // tra ve null neu khong ton tai
        } finally {
            em.close();
        }
    }

    public List<Employee> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT e FROM Employee e", Employee.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // ---------- READ co dieu kien (TODO 0.5) ----------
    public Employee findByEmail(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> result = em.createQuery(
                            "SELECT e FROM Employee e WHERE e.email = :email", Employee.class)
                    .setParameter("email", email)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        } finally {
            em.close();
        }
    }

    public List<Employee> findBySalaryGreaterThanAndActive(BigDecimal minSalary) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT e FROM Employee e WHERE e.salary > :minSalary AND e.active = true",
                            Employee.class)
                    .setParameter("minSalary", minSalary)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // ---------- UPDATE (TODO 0.6) ----------
    public Employee update(Employee e) {
        // e truyen vao co the dang DETACHED (lay tu findById() o mot EntityManager khac)
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Employee merged = em.merge(e); // merge() TRA VE mot entity MANAGED khac
            em.getTransaction().commit();
            return merged; // PHAI dung object nay tiep, khong dung "e" cu
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    // ---------- DELETE (TODO 0.7) ----------
    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Employee e = em.find(Employee.class, id); // e dang MANAGED
            if (e != null) {
                em.remove(e); // -> e chuyen sang REMOVED, se bi DELETE khi commit
            }
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }
}
```

### 2.5 `Main.java` — demo luồng CRUD đầy đủ (TODO 0.8, 0.9, 0.10)

```java
import fe.masv.dao.EmployeeDAO;
import fe.masv.pojo.Employee;
import fe.masv.pojo.Gender;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        EmployeeDAO dao = new EmployeeDAO();

        // ===== CREATE =====
        // [Lifecycle] emp dang o trang thai NEW/TRANSIENT (moi "new", chua lien quan DB)
        Employee emp = new Employee("Nguyen Van A", "a@fpt.edu.vn",
                new BigDecimal("15000000"), Gender.MALE, LocalDate.of(2022, 3, 1));

        dao.save(emp);
        // [Lifecycle] sau save(): trong luc persist() emp la MANAGED; sau khi method
        // save() return (EntityManager da dong), emp tro thanh DETACHED.
        System.out.println("Da tao: " + emp);

        // ===== READ =====
        Employee found = dao.findById(emp.getId());
        // [Lifecycle] found la mot object MANAGED trong pham vi EntityManager cua findById(),
        // nhung EntityManager cung da dong ngay sau khi return -> found cung la DETACHED
        // ngay khi ra khoi method.
        System.out.println("Doc lai: " + found);

        // ===== UPDATE =====
        found.setSalary(new BigDecimal("17000000"));
        // [Lifecycle] found dang DETACHED, sua field luc nay KHONG tu dong sync xuong DB
        Employee updated = dao.update(found);
        // [Lifecycle] update() goi merge(found) -> tra ve "updated" la MANAGED (trong luc
        // transaction dang chay); sau khi method return, "updated" tro thanh DETACHED.
        System.out.println("Sau update: " + updated);

        // Doc lai de kiem chung
        Employee reChecked = dao.findById(emp.getId());
        System.out.println("Kiem tra lai sau update: " + reChecked);

        // ===== DELETE =====
        dao.delete(emp.getId());
        // [Lifecycle] ben trong delete(): entity tim duoc chuyen MANAGED -> REMOVED,
        // bi xoa that su khoi DB khi commit().
        Employee afterDelete = dao.findById(emp.getId());
        System.out.println("Sau khi xoa, tim lai: " + afterDelete); // ky vong: null

        // ===== TODO 0.9: kiem chung unique constraint tren email =====
        Employee dup1 = new Employee("User 1", "trung@fpt.edu.vn",
                new BigDecimal("10000000"), Gender.FEMALE, LocalDate.now());
        Employee dup2 = new Employee("User 2", "trung@fpt.edu.vn", // trung email
                new BigDecimal("11000000"), Gender.MALE, LocalDate.now());

        dao.save(dup1);
        try {
            dao.save(dup2); // ky vong: nem exception vi vi pham UNIQUE
            System.out.println("LOI: khong thay exception nhu ky vong!");
        } catch (RuntimeException ex) {
            System.out.println("Da bat duoc loi trung email nhu ky vong: "
                    + ex.getClass().getSimpleName());
        }
    }
}
```

---

## 3. Cách kiểm chứng từng checklist bằng thao tác thật

| Checklist | Cách kiểm chứng |
|---|---|
| Bảng `employees` đúng cột/kiểu | Xem log `Hibernate: create table employees (...)` lúc chạy lần đầu, hoặc mở SSMS xem cấu trúc bảng |
| `save()` sinh ra `id` | `System.out.println(emp.getId())` ngay sau `dao.save(emp)` — phải khác `null` |
| `update()` lưu đúng | So sánh giá trị `salary` in ra ở bước "Doc lai" và "Kiem tra lai sau update" |
| `delete()` xóa thật | Bước "Sau khi xoa, tim lai" phải in ra `null` |
| Vi phạm `unique` bị chặn | Khối `try/catch` ở cuối `Main` phải rơi vào nhánh `catch`, không phải nhánh "LOI: khong thay exception" |
| `yearsOfService` không lưu DB | Mở bảng `employees` trong SSMS — không có cột `years_of_service`/`yearsOfService` nào cả |

---

## 4. Vì sao bài này quan trọng trước khi học Mapping (Bài 1, 2, 3)

Toàn bộ annotation quan hệ (`@OneToOne`, `@OneToMany`...) ở các bài sau đều **xây trên nền** những gì luyện ở bài này:

- `@JoinColumn` chỉ là biến thể của `@Column` áp dụng cho khóa ngoại.
- Việc hiểu `merge()` trả về entity khác (không sửa object cũ) ở bài này giúp tránh nhầm lẫn tương tự khi update entity có quan hệ ở Bài 1/2.
- Hiểu rõ 4 trạng thái lifecycle (Transient/Managed/Detached/Removed) ở 1 entity đơn giản trước, thì khi sang bài có quan hệ (2 entity ảnh hưởng lẫn nhau qua cascade), sẽ dễ hình dung hơn nhiều việc "persist cha thì con cũng persist theo" nghĩa là gì về mặt lifecycle.

---

## 5. Lỗi thường gặp

1. **Dùng `double` cho `salary`** thay vì `BigDecimal` → sai số làm tròn khi tính toán tiền tệ.
2. **Để `@Enumerated` mặc định (`ORDINAL`)** → lưu số thứ tự thay vì tên enum, dễ vỡ dữ liệu khi thêm/sửa thứ tự giá trị enum sau này.
3. **Gán lại `e = em.merge(e)` bị quên** → tưởng đã update nhưng thực chất object gốc không đổi, dữ liệu vẫn cũ khi đọc lại từ object đó (dù DB đã đúng, object Java bạn đang giữ chưa chắc đồng bộ).
4. **Gọi `em.remove()` trên entity đang detached** → ném `IllegalArgumentException` — luôn phải `find()` lại trong cùng transaction trước khi `remove()`.
5. **Không bọc `try/catch`** quanh thao tác cố ý gây lỗi (TODO 0.9) → chương trình dừng đột ngột thay vì minh họa được hành vi ràng buộc `unique`.

---

