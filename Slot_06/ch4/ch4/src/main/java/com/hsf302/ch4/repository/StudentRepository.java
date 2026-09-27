package com.hsf302.ch4.repository;

import com.hsf302.ch4.pojo.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StudentRepository extends JpaRepository<Student, Long>,
                                           JpaSpecificationExecutor<Student> {
    java.util.Optional<Student> findByStudentCode(String studentCode);   // WHERE student_code = ?
    boolean existsByEmail(String email);                        // kiểm tra tồn tại
    long countByActiveTrue();                                   // WHERE active = 1 (không cần tham số)
    java.util.List<Student> findByFullNameContainingIgnoreCase(String keyword);   // UPPER(full_name) LIKE UPPER('%kw%')
    java.util.List<Student> findByEmailEndingWith(String suffix);                 // email LIKE '%suffix'
    java.util.List<Student> findByEmailIsNull();                                  // email IS NULL
    java.util.List<Student> findByGpaBetweenOrderByGpaDesc(double min, double max);   // gpa BETWEEN ? AND ? ORDER BY gpa DESC
    java.util.List<Student> findByGenderAndActiveTrue(com.hsf302.ch4.pojo.Gender gender);                  // gender = ? AND active = 1
    java.util.List<Student> findByDobAfter(java.time.LocalDate date);                            // dob > ?
}
