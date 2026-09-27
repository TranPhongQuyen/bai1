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
}
