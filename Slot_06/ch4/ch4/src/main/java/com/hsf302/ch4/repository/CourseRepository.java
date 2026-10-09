package com.hsf302.ch4.repository;

import com.hsf302.ch4.pojo.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCode(String code);

    List<Course> findBySemesterOrderByCodeAsc(String semester);

    long countBySemester(String semester);

    List<Course> findByStudents_Department_CodeOrderByCodeAsc(String deptCode);

    List<Course> findDistinctByStudents_Department_CodeOrderByCodeAsc(String deptCode);

    // 1. Khóa học có credits trong khoảng min-max
    List<Course> findByCreditsBetween(int minCredits, int maxCredits);
    
    @Query("SELECT c FROM Course c WHERE c.credits BETWEEN :min AND :max")
    List<Course> findCoursesByCreditsRange(@Param("min") int min, @Param("max") int max);

    // 2. Đếm số khóa học có credits > 3
    long countByCreditsGreaterThan(int credits);

    @Query("SELECT COUNT(c) FROM Course c WHERE c.credits > :credits")
    long countCoursesWithCreditsGreaterThan(@Param("credits") int credits);

    // 3. Tìm khóa học có tên chứa từ khóa (không phân biệt hoa thường)
    List<Course> findByNameContainingIgnoreCase(String keyword);

    @Query("SELECT c FROM Course c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :kw, '%'))")
    List<Course> searchCoursesByName(@Param("kw") String keyword);

    @Query("SELECT c FROM Course c WHERE SIZE(c.students) >= c.capacity")
    List<Course> findFullCourses();

    @EntityGraph(attributePaths = "students")
    @Query("SELECT c FROM Course c WHERE c.code = :code")
    Optional<Course> findByCodeWithStudents(@Param("code") String code);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM student_courses WHERE student_id IN (SELECT id FROM students WHERE active = 0)", nativeQuery = true)
    int removeInactiveStudentsFromCourses();
}
