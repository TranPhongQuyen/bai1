package com.hsf302.ch4.service;
import com.hsf302.ch4.dto.*;
import com.hsf302.ch4.pojo.Course;
import com.hsf302.ch4.pojo.Student;
import com.hsf302.ch4.repository.CourseRepository;
import com.hsf302.ch4.repository.StudentRepository;
import com.hsf302.ch4.specification.EnrollmentSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    @Override @Transactional(readOnly = true)
    public void printEnrollmentInfo(String studentCode, String courseCode) {
        Student s = studentRepository.findByStudentCode(studentCode).orElseThrow();
        System.out.println("Courses of " + studentCode + ":");
        s.getCourses().forEach(c -> System.out.println("  - " + c));
        Course c = courseRepository.findByCode(courseCode).orElseThrow();
        System.out.println("Students in " + courseCode + ":");
        c.getStudents().forEach(st -> System.out.println("  - " + st));
    }
    @Override public List<Student> findStudentsByCourse(String courseCode) { return studentRepository.findByCourses_CodeOrderByFullNameAsc(courseCode); }
    @Override public long countActiveStudentsInCourse(String courseCode) { return studentRepository.countByCourses_CodeAndActiveTrue(courseCode); }
    @Override public List<Student> findStudentsWithoutCourse() { return studentRepository.findByCoursesIsEmpty(); }
    @Override public boolean isEnrolled(String studentCode, String courseCode) { return studentRepository.existsByStudentCodeAndCourses_Code(studentCode, courseCode); }
    @Override public List<Student> findGoodStudentsInCourse(String courseCode, double minGpa) { return studentRepository.findByCourseAndMinGpa(courseCode, minGpa); }
    @Override public List<CourseStatDTO> getCourseStatistics() { return studentRepository.getCourseStatistics(); }
    @Override public List<StudentCreditDTO> findStudentsWithMinCredits(long minCredits) { return studentRepository.findStudentsWithMinCredits(minCredits); }
    @Override public List<Student> findStudentsEnrolledInMoreThan(int n) { return studentRepository.findStudentsEnrolledInMoreThan(n); }
    @Override public Student getStudentWithCourses(String studentCode) { return studentRepository.findByStudentCodeWithCourses(studentCode).orElseThrow(); }
    @Override public List<CourseEnrollmentCount> getTopEnrollments() { return studentRepository.getTopEnrollments(); }
    @Override public List<EnrollmentView> getEnrollmentsByDepartment(String deptCode) { return studentRepository.getEnrollmentsByDepartment(deptCode); }
    @Override public Page<Student> findPageByCourseCode(String courseCode, int pageNum, int pageSize) { return studentRepository.findPageByCourseCode(courseCode, PageRequest.of(pageNum, pageSize)); }
    @Override @Transactional
    public void checkAndEnroll(String studentCode, String courseCode) {
        Student s = studentRepository.findByStudentCode(studentCode).orElseThrow(() -> new RuntimeException("Student not found"));
        Course c = courseRepository.findByCode(courseCode).orElseThrow(() -> new RuntimeException("Course not found"));
        if (!s.isActive()) throw new RuntimeException("Student is not active");
        if (s.getCourses().contains(c)) throw new RuntimeException("Student already enrolled");
        if (c.getStudents().size() >= c.getCapacity()) throw new RuntimeException("Course is full");
        s.enroll(c);
    }
    @Override @Transactional
    public void unenroll(String studentCode, String courseCode) {
        Student s = studentRepository.findByStudentCode(studentCode).orElseThrow(() -> new RuntimeException("Student not found"));
        Course c = courseRepository.findByCode(courseCode).orElseThrow(() -> new RuntimeException("Course not found"));
        if (!s.getCourses().contains(c)) throw new RuntimeException("Student not enrolled in this course");
        s.unenroll(c);
    }
    @Override @Transactional
    public void switchCourse(String studentCode, String oldCourseCode, String newCourseCode) {
        unenroll(studentCode, oldCourseCode);
        checkAndEnroll(studentCode, newCourseCode);
    }
    @Override @Transactional
    public int removeInactiveStudentsFromCourses() { return courseRepository.removeInactiveStudentsFromCourses(); }
    @Override
    public List<Student> searchEnrollments(String courseCode, String semester, String deptCode, Double minGpa) {
        Specification<Student> spec = Specification.where(EnrollmentSpecs.hasCourseCode(courseCode))
                .and(EnrollmentSpecs.hasSemester(semester))
                .and(EnrollmentSpecs.hasDepartmentCode(deptCode))
                .and(EnrollmentSpecs.hasMinGpa(minGpa));
        return studentRepository.findAll(spec);
    }
}
