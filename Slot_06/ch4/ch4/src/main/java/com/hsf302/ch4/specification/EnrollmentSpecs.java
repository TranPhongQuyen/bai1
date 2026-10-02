package com.hsf302.ch4.specification;
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
