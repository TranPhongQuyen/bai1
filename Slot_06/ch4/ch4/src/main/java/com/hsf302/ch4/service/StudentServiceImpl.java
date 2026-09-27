package com.hsf302.ch4.service;

import com.hsf302.ch4.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Override
    public long count() {
        return studentRepository.count();
    }

    @Override
    public java.util.Optional<com.hsf302.ch4.pojo.Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    public java.util.List<com.hsf302.ch4.pojo.Student> findAllOrderByGpaDesc() {
        return studentRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "gpa"));
    }

    @Override
    public org.springframework.data.domain.Page<com.hsf302.ch4.pojo.Student> findPage(int pageIndex, int size, String sortField) {
        if (pageIndex < 0 || size <= 0) {
            throw new IllegalArgumentException("pageIndex phải >= 0 và size phải > 0");
        }
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(pageIndex, size, org.springframework.data.domain.Sort.by(sortField).ascending());
        return studentRepository.findAll(pageable);
    }

    @Override
    public java.util.Optional<com.hsf302.ch4.pojo.Student> findByStudentCode(String studentCode) {
        return studentRepository.findByStudentCode(studentCode);
    }

    @Override
    public boolean isEmailExisted(String email) {
        return studentRepository.existsByEmail(email);
    }

    @Override
    public long countActive() {
        return studentRepository.countByActiveTrue();
    }

    @Override
    public java.util.List<com.hsf302.ch4.pojo.Student> searchByName(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return java.util.List.of();                              // từ khoá rỗng → không tìm
        }
        return studentRepository.findByFullNameContainingIgnoreCase(keyword.trim());
    }

    @Override
    public java.util.List<com.hsf302.ch4.pojo.Student> findByEmailDomain(String domain) {
        String suffix = domain.startsWith("@") ? domain : "@" + domain;   // "gmail.com" → "@gmail.com"
        return studentRepository.findByEmailEndingWith(suffix);
    }

    @Override
    public java.util.List<com.hsf302.ch4.pojo.Student> findWithoutEmail() {
        return studentRepository.findByEmailIsNull();
    }

    @Override
    public java.util.List<com.hsf302.ch4.pojo.Student> findByGpaRange(double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException("min GPA phải <= max GPA");
        }
        return studentRepository.findByGpaBetweenOrderByGpaDesc(min, max);
    }

    @Override
    public java.util.List<com.hsf302.ch4.pojo.Student> findActiveByGender(com.hsf302.ch4.pojo.Gender gender) {
        return studentRepository.findByGenderAndActiveTrue(gender);
    }

    @Override
    public java.util.List<com.hsf302.ch4.pojo.Student> findBornAfter(java.time.LocalDate date) {
        return studentRepository.findByDobAfter(date);
    }

    @Override
    public java.util.List<com.hsf302.ch4.pojo.Student> findByDepartment(String deptCode) {
        return studentRepository.findByDepartment_CodeOrderByFullNameAsc(deptCode);
    }

    @Override
    public long countByDepartment(String deptCode) {
        return studentRepository.countByDepartment_Code(deptCode);
    }

    @Override
    public java.util.List<com.hsf302.ch4.pojo.Student> findTop3ByGpa() {
        return studentRepository.findTop3ByOrderByGpaDesc();
    }

    @Override
    public java.util.List<com.hsf302.ch4.pojo.Student> findGoodStudents(String deptCode, double minGpa) {
        return studentRepository.findGoodStudentsInDepartment(deptCode, minGpa);
    }

    @Override
    public java.util.List<com.hsf302.ch4.pojo.Student> searchByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return java.util.List.of();
        }
        return studentRepository.searchByKeyword(keyword.trim());
    }
}
