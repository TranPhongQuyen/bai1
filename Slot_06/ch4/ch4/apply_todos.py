import os

base_dir = r"d:\TranPhongquyen_DE190908_HFS\Slot_06\ch4\ch4\src\main\java\com\hsf302\ch4"

files = {
    "dto/CourseStatDTO.java": """package com.hsf302.ch4.dto;
public record CourseStatDTO(String code, String name, long totalStudents, double avgGpa) {}
""",
    "dto/StudentCreditDTO.java": """package com.hsf302.ch4.dto;
public record StudentCreditDTO(String studentCode, String fullName, long totalCredits) {}
""",
    "dto/CourseEnrollmentCount.java": """package com.hsf302.ch4.dto;
public interface CourseEnrollmentCount {
    String getCourseCode();
    Integer getEnrollmentCount();
}
""",
    "dto/EnrollmentView.java": """package com.hsf302.ch4.dto;
public interface EnrollmentView {
    String getStudentCode();
    String getFullName();
    String getCourseCode();
    String getSemester();
}
""",
    "specification/EnrollmentSpecs.java": """package com.hsf302.ch4.specification;
import com.hsf302.ch4.pojo.Course;
import com.hsf302.ch4.pojo.Student;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
public class EnrollmentSpecs {
    public static Specification<Student> hasCourseCode(String courseCode) {
        return (root, query, cb) -> {
            if (courseCode == null || courseCode.isBlank()) return null;
            query.distinct(true);
            Join<Student, Course> courses = root.join("courses");
            return cb.equal(courses.get("code"), courseCode);
        };
    }
    public static Specification<Student> hasSemester(String semester) {
        return (root, query, cb) -> {
            if (semester == null || semester.isBlank()) return null;
            query.distinct(true);
            Join<Student, Course> courses = root.join("courses");
            return cb.equal(courses.get("semester"), semester);
        };
    }
    public static Specification<Student> hasDepartmentCode(String deptCode) {
        return (root, query, cb) -> {
            if (deptCode == null || deptCode.isBlank()) return null;
            return cb.equal(root.get("department").get("code"), deptCode);
        };
    }
    public static Specification<Student> hasMinGpa(Double minGpa) {
        return (root, query, cb) -> {
            if (minGpa == null) return null;
            return cb.greaterThanOrEqualTo(root.get("gpa"), minGpa);
        };
    }
}
""",
    "repository/CourseRepository.java": """package com.hsf302.ch4.repository;
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
    
    @Query("SELECT c FROM Course c WHERE SIZE(c.students) >= c.capacity")
    List<Course> findFullCourses();

    @EntityGraph(attributePaths = "students")
    @Query("SELECT c FROM Course c WHERE c.code = :code")
    Optional<Course> findByCodeWithStudents(@Param("code") String code);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM student_courses WHERE student_id IN (SELECT id FROM students WHERE active = 0)", nativeQuery = true)
    int removeInactiveStudentsFromCourses();
}
""",
    "repository/StudentRepository.java": """package com.hsf302.ch4.repository;
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
    List<Student> findByEmailEndingWithIgnoreCase(String suffix);
    List<Student> findByEmailIsNull();
    List<Student> findByGpaBetweenOrderByGpaDesc(double min, double max);
    List<Student> findByGenderAndActiveTrue(Gender gender);
    List<Student> findByDobAfter(LocalDate date);
    List<Student> findByDepartment_CodeOrderByFullName(String code);
    long countByDepartment_Code(String code);
    List<Student> findTop3ByOrderByGpaDesc();
    
    @Query("SELECT s FROM Student s WHERE s.department.code = :deptCode AND s.gpa >= :minGpa")
    List<Student> findGoodStudents(@Param("deptCode") String deptCode, @Param("minGpa") double minGpa);
    
    @Query("SELECT s FROM Student s WHERE LOWER(s.fullName) LIKE LOWER(CONCAT('%', :kw, '%')) OR LOWER(s.email) LIKE LOWER(CONCAT('%', :kw, '%'))")
    List<Student> searchByKeyword(@Param("kw") String keyword);
    
    @Query("SELECT s FROM Student s WHERE s.gpa > (SELECT AVG(s2.gpa) FROM Student s2)")
    List<Student> findAboveAverageGpa();
    
    @Query(value = "SELECT TOP (:n) * FROM students s JOIN departments d ON s.department_id = d.id WHERE d.code = :deptCode ORDER BY s.gpa DESC", nativeQuery = true)
    List<Student> findTopNInDepartment(@Param("deptCode") String deptCode, @Param("n") int n);
    
    @Query("SELECT s.studentCode AS studentCode, s.fullName AS fullName, s.gpa AS gpa, s.department.name AS departmentName FROM Student s WHERE s.active = true")
    List<StudentSummary> getActiveSummaries();
    
    Page<Student> findByDepartment_CodeAndActiveTrue(String deptCode, Pageable pageable);
    
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
""",
    "service/CourseService.java": """package com.hsf302.ch4.service;
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
""",
    "service/EnrollmentService.java": """package com.hsf302.ch4.service;
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
""",
    "service/CourseServiceImpl.java": """package com.hsf302.ch4.service;
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
""",
    "service/EnrollmentServiceImpl.java": """package com.hsf302.ch4.service;
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
""",
    "runner/Exercise2Runner.java": """package com.hsf302.ch4.runner;
import com.hsf302.ch4.service.CourseService;
import com.hsf302.ch4.service.EnrollmentService;
import com.hsf302.ch4.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.Collection;
@Component
@Order(3)
@Profile("ex2")
@RequiredArgsConstructor
public class Exercise2Runner implements CommandLineRunner {
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    @Override
    public void run(String... args) {
        partB(); partC(); partD(); bonus(); partE();
    }
    private void title(String t) { System.out.println("\\n===== " + t + " ====="); }
    private void printList(String label, Collection<?> list) {
        System.out.println("-- " + label + ":");
        list.forEach(o -> System.out.println("   " + o));
        System.out.println("   -> " + list.size() + " record(s)");
    }
    private void attempt(String label, Runnable action) {
        try { action.run(); System.out.println("   [OK]   " + label); }
        catch (RuntimeException e) { System.out.println("   [FAIL] " + label + " -> " + e.getMessage()); }
    }
    private void partB() {
        title("TODO 6: count / findById");
        System.out.println("Total courses: " + courseService.count());
        printList("All courses ordered by code", courseService.findAllOrderByCode());
        System.out.println("findById(1): " + courseService.findById(1L).orElse(null));
        title("TODO 7: Navigation");
        enrollmentService.printEnrollmentInfo("SE001", "HSF302");
    }
    private void partC() {
        title("TODO 8: Derived query");
        System.out.println("findByCode(PRJ301): " + courseService.findByCode("PRJ301").orElse(null));
        printList("FA26 courses", courseService.findBySemesterOrderByCodeAsc("FA26"));
        System.out.println("Courses in FA26: " + courseService.countBySemester("FA26"));
        title("TODO 9: Nested property");
        printList("Students in HSF302", enrollmentService.findStudentsByCourse("HSF302"));
        System.out.println("Active students in HSF302: " + enrollmentService.countActiveStudentsInCourse("HSF302"));
        title("TODO 10: Inverse side & Distinct");
        printList("Courses of SE students", courseService.findByStudentsDepartmentCode("SE"));
        printList("Distinct Courses of SE students", courseService.findDistinctByStudentsDepartmentCode("SE"));
        title("TODO 11: IsEmpty & exists");
        printList("Students without courses", enrollmentService.findStudentsWithoutCourse());
        System.out.println("Is SE001 enrolled in PRJ301? " + enrollmentService.isEnrolled("SE001", "PRJ301"));
    }
    private void partD() {
        title("TODO 12: JPQL JOIN");
        printList("Good students in HSF302 (GPA >= 3.0)", enrollmentService.findGoodStudentsInCourse("HSF302", 3.0));
        title("TODO 13: Left Join DTO");
        printList("Course Statistics", enrollmentService.getCourseStatistics());
        title("TODO 14: Group By Having");
        printList("Students with >= 6 credits", enrollmentService.findStudentsWithMinCredits(6));
        title("TODO 15: SIZE");
        printList("Full courses", courseService.findFullCourses());
        printList("Students with > 2 courses", enrollmentService.findStudentsEnrolledInMoreThan(2));
        title("TODO 16: Fetch & EntityGraph");
        System.out.println("Student SE001 has " + enrollmentService.getStudentWithCourses("SE001").getCourses().size() + " courses");
        System.out.println("Course HSF302 has " + courseService.getCourseWithStudents("HSF302").getStudents().size() + " students");
        title("TODO 17: Native SQL");
        enrollmentService.getTopEnrollments().forEach(c -> System.out.println("Course " + c.getCourseCode() + ": " + c.getEnrollmentCount() + " enrollments"));
        title("TODO 18: Interface Projection");
        enrollmentService.getEnrollmentsByDepartment("SE").forEach(v -> System.out.printf("%s - %s học %s (%s)\\n", v.getStudentCode(), v.getFullName(), v.getCourseCode(), v.getSemester()));
        title("TODO 19: Pageable");
        org.springframework.data.domain.Page<com.hsf302.ch4.pojo.Student> p = enrollmentService.findPageByCourseCode("HSF302", 0, 2);
        printList("Page 1 of HSF302 students", p.getContent());
    }
    private void bonus() {
        title("TODO 25: Specification");
        printList("Search FA26, SE dept, GPA >= 3.0", enrollmentService.searchEnrollments(null, "FA26", "SE", 3.0));
    }
    private void partE() {
        title("TODO 20: Enroll");
        attempt("Enroll SE001 to IAA202", () -> enrollmentService.checkAndEnroll("SE001", "IAA202"));
        attempt("Enroll SE001 to IAA202 again", () -> enrollmentService.checkAndEnroll("SE001", "IAA202"));
        attempt("Enroll SE004 to IAA202", () -> enrollmentService.checkAndEnroll("SE004", "IAA202"));
        title("TODO 21: Unenroll");
        attempt("Unenroll SE001 from IAA202", () -> enrollmentService.unenroll("SE001", "IAA202"));
        title("TODO 22: Switch Course");
        attempt("Switch SE001 from PRJ301 to MKT101", () -> enrollmentService.switchCourse("SE001", "PRJ301", "MKT101"));
        title("TODO 23: Safe Delete");
        attempt("Delete PRJ301 safely", () -> courseService.deleteCourseSafely("PRJ301"));
        title("TODO 24: Bulk Delete");
        System.out.println("Deactivated 2.5 GPA: " + studentService.deactivateLowGpa(2.5));
        System.out.println("Rows deleted from student_courses: " + enrollmentService.removeInactiveStudentsFromCourses());
    }
}
"""
}

for path, content in files.items():
    full_path = os.path.join(base_dir, path)
    os.makedirs(os.path.dirname(full_path), exist_ok=True)
    with open(full_path, "w", encoding="utf-8") as f:
        f.write(content)
