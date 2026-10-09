package com.hsf302.chapter6.repository;

import com.hsf302.chapter6.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @EntityGraph(attributePaths = {"major"})
    Page<Student> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email, Pageable pageable);

    @EntityGraph(attributePaths = {"major"})
    Page<Student> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"major"})
    Optional<Student> findById(Long id);

    /** Email đã tồn tại? (dùng khi thêm mới) */
    boolean existsByEmailIgnoreCase(String email);

    /** Email đã được sinh viên KHÁC dùng? (dùng khi cập nhật) */
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
