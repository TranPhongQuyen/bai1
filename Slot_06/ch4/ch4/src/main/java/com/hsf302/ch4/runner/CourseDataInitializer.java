package com.hsf302.ch4.runner;

import com.hsf302.ch4.pojo.Course;
import com.hsf302.ch4.pojo.Student;
import com.hsf302.ch4.repository.CourseRepository;
import com.hsf302.ch4.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Order(2)
@RequiredArgsConstructor
public class CourseDataInitializer implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (courseRepository.count() == 0) {
            courseRepository.saveAll(List.of(
                    new Course("PRJ301", "Java Web Application Development", 3, 5, "FA26"),
                    new Course("HSF302", "Hibernate & Spring Framework", 3, 6, "FA26"),
                    new Course("SWP391", "Software Development Project", 4, 4, "FA26"),
                    new Course("AIL303", "Machine Learning", 3, 4, "FA26"),
                    new Course("IAA202", "Risk Management in Information Systems", 3, 4, "FA26"),
                    new Course("MKT101", "Marketing Principles", 3, 4, "SU26")
            ));

            Student an = getStudent("SE001");
            Student binh = getStudent("SE002");
            Student mai = getStudent("SE004");
            Student dung = getStudent("AI001");
            Student em = getStudent("AI002");
            Student hoa = getStudent("AI003");
            Student giang = getStudent("IA001");
            Student cuong = getStudent("IA002");

            Course prj = getCourse("PRJ301");
            Course hsf = getCourse("HSF302");
            Course swp = getCourse("SWP391");
            Course ail = getCourse("AIL303");
            Course iaa = getCourse("IAA202");

            an.enroll(prj); an.enroll(hsf); an.enroll(swp);
            binh.enroll(prj); binh.enroll(hsf);
            mai.enroll(hsf); mai.enroll(swp);
            dung.enroll(hsf); dung.enroll(ail);
            em.enroll(ail);
            hoa.enroll(prj); hoa.enroll(swp); hoa.enroll(ail);
            giang.enroll(iaa);
            cuong.enroll(prj); cuong.enroll(iaa);

            System.out.println(">>> Seeded 6 courses and student enrollments");
        }
    }

    private Student getStudent(String code) {
        return studentRepository.findByStudentCode(code).orElseThrow();
    }

    private Course getCourse(String code) {
        return courseRepository.findByCode(code).orElseThrow();
    }
}
