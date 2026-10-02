package com.hsf302.ch4.runner;
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
    private void title(String t) { System.out.println("\n===== " + t + " ====="); }
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
        enrollmentService.getEnrollmentsByDepartment("SE").forEach(v -> System.out.printf("%s - %s học %s (%s)\n", v.getStudentCode(), v.getFullName(), v.getCourseCode(), v.getSemester()));
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
