package com.hsf302.ch4.service;
import com.hsf302.ch4.dto.*;
import com.hsf302.ch4.pojo.Student;
import org.springframework.data.domain.Page;
public interface EnrollmentService {
    void printEnrollmentInfo(String studentCode, String courseCode);
    java.util.List<Student> findStudentsByCourse(String courseCode);
    long countActiveStudentsInCourse(String courseCode);
    java.util.List<Student> findStudentsWithoutCourse();
    boolean isEnrolled(String studentCode, String courseCode);
    java.util.List<Student> findGoodStudentsInCourse(String courseCode, double minGpa);
    java.util.List<CourseStatDTO> getCourseStatistics();
    java.util.List<StudentCreditDTO> findStudentsWithMinCredits(long minCredits);
    java.util.List<Student> findStudentsEnrolledInMoreThan(int n);
    Student getStudentWithCourses(String studentCode);
    java.util.List<CourseEnrollmentCount> getTopEnrollments();
    java.util.List<EnrollmentView> getEnrollmentsByDepartment(String deptCode);
    Page<Student> findPageByCourseCode(String courseCode, int pageNum, int pageSize);
    void checkAndEnroll(String studentCode, String courseCode);
    void unenroll(String studentCode, String courseCode);
    void switchCourse(String studentCode, String oldCourseCode, String newCourseCode);
    int removeInactiveStudentsFromCourses();
    java.util.List<Student> searchEnrollments(String courseCode, String semester, String deptCode, Double minGpa);
}
