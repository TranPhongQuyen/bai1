package com.hsf302.ch4.service;

import com.hsf302.ch4.repository.DepartmentRepository;
import com.hsf302.ch4.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final StudentRepository studentRepository;

    @Override
    public long count() {
        return departmentRepository.count();
    }

    @Override
    public boolean existsById(Long id) {
        return departmentRepository.existsById(id);
    }

    @Override
    public java.util.List<com.hsf302.ch4.pojo.Department> findDepartmentsWithoutStudents() {
        return departmentRepository.findByStudentsIsEmpty();
    }

    @Override
    public java.util.List<com.hsf302.ch4.dto.DepartmentStatDTO> getStatistics() {
        return departmentRepository.getDepartmentStats();
    }

    @Override
    public java.util.Optional<com.hsf302.ch4.pojo.Department> findByCode(String code) {
        return departmentRepository.findByCode(code);
    }

    @Override
    public com.hsf302.ch4.pojo.Department getWithStudents(String code) {
        return departmentRepository.findByCodeWithStudents(code)
                .orElseThrow(() -> new IllegalArgumentException("Department not found: " + code));
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public int transferStudentsAndDelete(String fromCode, String toCode) {
        if (fromCode.equals(toCode)) {
            throw new IllegalArgumentException("Khoa nguồn và khoa đích phải khác nhau");
        }
        com.hsf302.ch4.pojo.Department from = departmentRepository.findByCode(fromCode)
                .orElseThrow(() -> new IllegalArgumentException("Department not found: " + fromCode));
        com.hsf302.ch4.pojo.Department to = departmentRepository.findByCode(toCode)
                .orElseThrow(() -> new IllegalArgumentException("Department not found: " + toCode));

        int moved = studentRepository.transferStudents(fromCode, toCode);   // 1. chuyển FK sang khoa mới
        departmentRepository.deleteById(from.getId());              // 2. khoa cũ đã rỗng → xoá được
        return moved;
    }

    @Override
    public java.util.List<com.hsf302.ch4.pojo.Department> findAll() {
        return departmentRepository.findAll(org.springframework.data.domain.Sort.by("id"));
    }
}
