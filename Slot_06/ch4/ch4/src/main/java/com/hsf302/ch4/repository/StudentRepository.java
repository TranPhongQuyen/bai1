package com.hsf302.ch4.repository;
import com.hsf302.ch4.dto.CourseEnrollmentCount;
import com.hsf302.ch4.dto.CourseStatDTO;
import com.hsf302.ch4.dto.EnrollmentView;
import com.hsf302.ch4.dto.StudentCreditDTO;
import com.hsf302.ch4.dto.StudentSummary;
import com.hsf302.ch4.pojo.Gender;
import com.hsf302.ch4.pojo.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {
    List<Student> findByOrderByGpaDesc();
    Optional<Student> findByStudentCode(String studentCode);
    boolean existsByEmail(String email);
    long countByActiveTrue();
    List<Student> findByFullNameContainingIgnoreCase(String keyword);
    List<Student> findByEmailEndingWith(String suffix);
    List<Student> findByEmailIsNull();
    List<Student> findByGpaBetweenOrderByGpaDesc(double min, double max);
    List<Student> findByGenderAndActiveTrue(Gender gender);
    List<Student> findByDobAfter(LocalDate date);
    List<Student> findByDepartment_CodeOrderByFullNameAsc(String code);
    long countByDepartment_Code(String code);
    List<Student> findTop3ByOrderByGpaDesc();
    
    @Query("SELECT s FROM Student s WHERE s.department.code = :deptCode AND s.gpa >= :minGpa")
    List<Student> findGoodStudentsInDepartment(@Param("deptCode") String deptCode, @Param("minGpa") double minGpa);
    
    @Query("SELECT s FROM Student s WHERE LOWER(s.fullName) LIKE LOWER(CONCAT('%', :kw, '%')) OR LOWER(s.email) LIKE LOWER(CONCAT('%', :kw, '%'))")
    List<Student> searchByKeyword(@Param("kw") String keyword);
    
    @Query("SELECT s FROM Student s WHERE s.gpa > (SELECT AVG(s2.gpa) FROM Student s2)")
    List<Student> findAboveAverageGpa();
    
    @Query(value = "SELECT TOP (:n) * FROM students s JOIN departments d ON s.department_id = d.id WHERE d.code = :deptCode ORDER BY s.gpa DESC", nativeQuery = true)
    List<Student> findTopNByDepartmentNative(@Param("deptCode") String deptCode, @Param("n") int n);
    
    @Query("SELECT s.studentCode AS studentCode, s.fullName AS fullName, s.gpa AS gpa, s.department.name AS departmentName FROM Student s WHERE s.active = true")
    List<StudentSummary> findActiveSummaries();
    
    Page<Student> findActiveByDepartment(String deptCode, Pageable pageable);
    
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Student s SET s.active = false WHERE s.gpa < :minGpa")
    int deactivateLowGpa(@Param("minGpa") double minGpa);
    
    @Modifying
    @Query("UPDATE Student s SET s.department = (SELECT d FROM Department d WHERE d.code = :newDeptCode) WHERE s.department.code = :oldDeptCode")
    int transferStudents(@Param("oldDeptCode") String oldDeptCode, @Param("newDeptCode") String newDeptCode);
    
    long deleteByActiveFalse();

    // Exercise 2 additions
    List<Student> findByCourses_CodeOrderByFullNameAsc(String courseCode);
    long countByCourses_CodeAndActiveTrue(String courseCode);
    List<Student> findByCoursesIsEmpty();
    boolean existsByStudentCodeAndCourses_Code(String studentCode, String courseCode);

    @Query("SELECT s FROM Student s JOIN s.courses c WHERE c.code = :courseCode AND s.gpa >= :minGpa")
    List<Student> findByCourseAndMinGpa(@Param("courseCode") String courseCode, @Param("minGpa") double minGpa);

    @Query("SELECT new com.hsf302.ch4.dto.CourseStatDTO(c.code, c.name, COUNT(s), COALESCE(AVG(s.gpa), 0.0)) FROM Course c LEFT JOIN c.students s GROUP BY c.code, c.name")
    List<CourseStatDTO> getCourseStatistics();

    @Query("SELECT new com.hsf302.ch4.dto.StudentCreditDTO(s.studentCode, s.fullName, SUM(c.credits)) FROM Student s JOIN s.courses c GROUP BY s.id, s.studentCode, s.fullName HAVING SUM(c.credits) >= :minCredits")
    List<StudentCreditDTO> findStudentsWithMinCredits(@Param("minCredits") long minCredits);

    @Query("SELECT s FROM Student s WHERE SIZE(s.courses) > :n")
    List<Student> findStudentsEnrolledInMoreThan(@Param("n") int n);

    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.courses WHERE s.studentCode = :studentCode")
    Optional<Student> findByStudentCodeWithCourses(@Param("studentCode") String studentCode);

    @Query(value = "SELECT c.code AS courseCode, COUNT(sc.student_id) AS enrollmentCount FROM courses c JOIN student_courses sc ON c.id = sc.course_id GROUP BY c.code ORDER BY enrollmentCount DESC", nativeQuery = true)
    List<CourseEnrollmentCount> getTopEnrollments();

    @Query("SELECT s.studentCode AS studentCode, s.fullName AS fullName, c.code AS courseCode, c.semester AS semester FROM Student s JOIN s.courses c JOIN s.department d WHERE d.code = :deptCode")
    List<EnrollmentView> getEnrollmentsByDepartment(@Param("deptCode") String deptCode);

    @Query(value = "SELECT s FROM Student s JOIN s.courses c WHERE c.code = :courseCode",
           countQuery = "SELECT COUNT(s) FROM Student s JOIN s.courses c WHERE c.code = :courseCode")
    Page<Student> findPageByCourseCode(@Param("courseCode") String courseCode, Pageable pageable);
}
