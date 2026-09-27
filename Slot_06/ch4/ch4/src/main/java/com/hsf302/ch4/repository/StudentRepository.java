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
    java.util.List<Student> findByDepartment_CodeOrderByFullNameAsc(String code);   // JOIN departments ... WHERE d.code = ?
    long countByDepartment_Code(String code);
    java.util.List<Student> findTop3ByOrderByGpaDesc();                              // SELECT TOP 3 ... ORDER BY gpa DESC

    @org.springframework.data.jpa.repository.Query("SELECT s FROM Student s " +
           "WHERE s.department.code = :code AND s.gpa >= :minGpa " +
           "ORDER BY s.gpa DESC")
    java.util.List<Student> findGoodStudentsInDepartment(@org.springframework.data.repository.query.Param("code") String code,
                                               @org.springframework.data.repository.query.Param("minGpa") double minGpa);

    @org.springframework.data.jpa.repository.Query("SELECT s FROM Student s " +
           "WHERE LOWER(s.fullName) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "   OR LOWER(s.email)    LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "ORDER BY s.fullName")
    java.util.List<Student> searchByKeyword(@org.springframework.data.repository.query.Param("kw") String keyword);

    @org.springframework.data.jpa.repository.Query("SELECT s FROM Student s " +
           "WHERE s.gpa > (SELECT AVG(s2.gpa) FROM Student s2) " +
           "ORDER BY s.gpa DESC")
    java.util.List<Student> findAboveAverageGpa();
}
