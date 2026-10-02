package com.hsf302.ch4.service;
import com.hsf302.ch4.pojo.Course;
import com.hsf302.ch4.pojo.Student;
import com.hsf302.ch4.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    @Override public long count() { return courseRepository.count(); }
    @Override public List<Course> findAllOrderByCode() { return courseRepository.findAll(Sort.by("code").ascending()); }
    @Override public Optional<Course> findById(Long id) { return courseRepository.findById(id); }
    @Override public Optional<Course> findByCode(String code) { return courseRepository.findByCode(code); }
    @Override public List<Course> findBySemesterOrderByCodeAsc(String semester) { return courseRepository.findBySemesterOrderByCodeAsc(semester); }
    @Override public long countBySemester(String semester) { return courseRepository.countBySemester(semester); }
    @Override public List<Course> findByStudentsDepartmentCode(String deptCode) { return courseRepository.findByStudents_Department_CodeOrderByCodeAsc(deptCode); }
    @Override public List<Course> findDistinctByStudentsDepartmentCode(String deptCode) { return courseRepository.findDistinctByStudents_Department_CodeOrderByCodeAsc(deptCode); }
    @Override public List<Course> findFullCourses() { return courseRepository.findFullCourses(); }
    @Override public Course getCourseWithStudents(String code) { return courseRepository.findByCodeWithStudents(code).orElseThrow(() -> new RuntimeException("Course not found")); }
    @Override @Transactional
    public void deleteCourseSafely(String code) {
        Course course = courseRepository.findByCodeWithStudents(code).orElseThrow(() -> new RuntimeException("Course not found"));
        List<Student> students = new ArrayList<>(course.getStudents());
        for (Student s : students) { s.unenroll(course); }
        courseRepository.delete(course);
    }
}
