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

    // Luôn dùng STRING, KHÔNG dùng mặc định ORDINAL
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
