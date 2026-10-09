package com.hsf302.chapter6.config;

import com.hsf302.chapter6.entity.Major;
import com.hsf302.chapter6.entity.Student;
import com.hsf302.chapter6.repository.MajorRepository;
import com.hsf302.chapter6.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final StudentRepository studentRepository;
    private final MajorRepository majorRepository;

    public DataInitializer(StudentRepository studentRepository, MajorRepository majorRepository) {
        this.studentRepository = studentRepository;
        this.majorRepository = majorRepository;
    }

    @Override
    public void run(String... args) {
        if (majorRepository.count() == 0) {
            majorRepository.saveAll(List.of(
                    new Major("CNTT"),
                    new Major("KTPM"),
                    new Major("HTTT"),
                    new Major("ATTT"),
                    new Major("MMT")
            ));
            log.info("Đã seed 5 chuyên ngành");
        }

        if (studentRepository.count() > 0) {
            log.info("Bảng students đã có dữ liệu → bỏ qua seed");
            return;
        }

        Major cntt = majorRepository.findByName("CNTT").orElseThrow();
        Major ktpm = majorRepository.findByName("KTPM").orElseThrow();
        Major attt = majorRepository.findByName("ATTT").orElseThrow();
        Major httt = majorRepository.findByName("HTTT").orElseThrow();

        studentRepository.saveAll(List.of(
                new Student("Nguyễn Văn An",  "an@fpt.edu.vn",    20, cntt, 3.5),
                new Student("Trần Thị Bình",  "binh@fpt.edu.vn",  21, ktpm, 3.2),
                new Student("Lê Minh Cường",  "cuong@fpt.edu.vn", 19, attt, 3.8),
                new Student("Phạm Thị Dung",  "dung@fpt.edu.vn",  22, httt, 2.9)
        ));
        log.info("Đã seed {} sinh viên", studentRepository.count());
    }
}
