package com.hsf302.chapter6.dto;

import jakarta.validation.constraints.*;

public class StudentForm {
    
    private Long id;

    @NotBlank(message = "Tên không được để trống")
    @Size(min = 2, max = 50, message = "Tên phải từ 2 đến 50 ký tự")
    private String name;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email tối đa 100 ký tự")
    private String email;

    @NotNull(message = "Tuổi không được để trống")
    @Min(value = 18, message = "Tuổi tối thiểu là 18")
    @Max(value = 30, message = "Tuổi tối đa là 30")
    private Integer age;

    @NotNull(message = "Chuyên ngành không được để trống")
    private Long majorId;

    @NotNull(message = "GPA không được để trống")
    @DecimalMin(value = "0.0", message = "GPA tối thiểu là 0.0")
    @DecimalMax(value = "4.0", message = "GPA tối đa là 4.0")
    private Double gpa;

    public StudentForm() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Long getMajorId() { return majorId; }
    public void setMajorId(Long majorId) { this.majorId = majorId; }

    public Double getGpa() { return gpa; }
    public void setGpa(Double gpa) { this.gpa = gpa; }
}
