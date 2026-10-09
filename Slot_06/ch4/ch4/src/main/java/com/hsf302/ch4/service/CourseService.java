package com.hsf302.ch4.service;

import com.hsf302.ch4.pojo.Course;
import java.util.List;
import java.util.Optional;

public interface CourseService {
    long count();

    List<Course> findAllOrderByCode();

    Optional<Course> findById(Long id);

    Optional<Course> findByCode(String code);

    List<Course> findBySemesterOrderByCodeAsc(String semester);

    long countBySemester(String semester);

    List<Course> findByStudentsDepartmentCode(String deptCode);

    List<Course> findDistinctByStudentsDepartmentCode(String deptCode);

    List<Course> findFullCourses();

    Course getCourseWithStudents(String code);

    void deleteCourseSafely(String code);
}
