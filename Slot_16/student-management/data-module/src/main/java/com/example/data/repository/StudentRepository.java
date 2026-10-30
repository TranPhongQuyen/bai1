package com.example.data.repository;

import com.example.data.pojo.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // TODO 2.2 — derived query: tất cả, sắp xếp theo id tăng dần
    List<Student> findAllByOrderByIdAsc();

    // TODO 2.3 — derived query: tìm theo email, không phân biệt hoa thường
    Optional<Student> findByEmailIgnoreCase(String email);

    // TODO 2.4 — derived query: kiểm tra email tồn tại (dùng khi create)
    boolean existsByEmailIgnoreCase(String email);

    // TODO 2.5 — derived query: kiểm tra email trùng student khác (dùng khi update)
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    // TODO 2.6 — @Query: tìm theo tên hoặc email, không phân biệt hoa thường, ORDER BY id
    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.name)  LIKE LOWER(CONCAT('%', :kw, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "ORDER BY s.id")
    List<Student> search(@Param("kw") String keyword);
}
