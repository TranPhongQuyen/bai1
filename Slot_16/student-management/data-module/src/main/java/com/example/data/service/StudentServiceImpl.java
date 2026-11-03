package com.example.data.service;

import com.example.data.exception.StudentNotFoundException;
import com.example.data.exception.ValidationException;
import com.example.data.pojo.Student;
import com.example.data.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

/**
 * TẦNG NGHIỆP VỤ (Service Layer Implementation)
 * - @Service: Đăng ký class này là một Spring Service Bean.
 * - @Transactional(readOnly = true): Toàn bộ các phương thức đọc mặc định chạy trong transaction chỉ đọc,
 *   giúp tối ưu hiệu năng cơ sở dữ liệu.
 */
@Service
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {

    // Biểu thức Regex chuẩn kiểm tra định dạng email
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$");

    // TODO 3.2: Tiêm phụ thuộc qua Constructor (Constructor Injection), không dùng @Autowired trên field
    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * TODO 3.3 — Thêm mới sinh viên:
     * - @Transactional: Mở giao dịch ghi dữ liệu xuống cơ sở dữ liệu.
     * - Quy trình: Chuẩn hoá -> Xác thực dữ liệu -> Kiểm tra trùng email -> Lưu xuống DB.
     */
    @Override
    @Transactional
    public Student create(Student s) {
        normalize(s);
        validate(s);

        // Kiểm tra trùng email (không phân biệt hoa/thường)
        if (studentRepository.existsByEmailIgnoreCase(s.getEmail())) {
            throw new ValidationException("Email already exists");
        }
        return studentRepository.save(s);
    }

    /**
     * TODO 3.4 — Tìm sinh viên theo ID:
     * - Nếu không tìm thấy trong DB, ném ngoại lệ StudentNotFoundException.
     */
    @Override
    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    /**
     * TODO 3.5 — Lấy toàn bộ danh sách sinh viên:
     * - Sắp xếp theo thứ tự ID tăng dần.
     */
    @Override
    public List<Student> findAll() {
        return studentRepository.findAllByOrderByIdAsc();
    }

    /**
     * TODO 3.6 — Cập nhật thông tin sinh viên:
     * 1. Tìm entity đang được Hibernate quản lý trong DB qua findById().
     * 2. Chuẩn hoá và xác thực dữ liệu mới.
     * 3. Kiểm tra xem email mới có bị trùng với sinh viên KHÁC hay không (existsByEmailIgnoreCaseAndIdNot).
     * 4. Sao chép thông tin mới vào entity và gọi save().
     */
    @Override
    @Transactional
    public Student update(Student s) {
        // Lấy entity đang được JPA/Hibernate quản lý (managed entity)
        Student managed = studentRepository.findById(s.getId())
                .orElseThrow(() -> new StudentNotFoundException(s.getId()));

        normalize(s);
        validate(s);

        // Kiểm tra trùng email với sinh viên khác (bỏ qua ID của chính mình)
        if (studentRepository.existsByEmailIgnoreCaseAndIdNot(s.getEmail(), s.getId())) {
            throw new ValidationException("Email already exists");
        }

        // Cập nhật các trường
        managed.setName(s.getName());
        managed.setEmail(s.getEmail());
        managed.setAge(s.getAge());

        return studentRepository.save(managed);
    }

    /**
     * TODO 3.7 — Xoá sinh viên theo ID:
     * - Kiểm tra ID có tồn tại trước khi xoá; nếu không có ném StudentNotFoundException.
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }
        studentRepository.deleteById(id);
    }

    /**
     * TODO 3.8 — Tìm kiếm sinh viên theo từ khoá:
     * - Nếu từ khoá null hoặc rỗng -> Trả về tất cả sinh viên (findAll).
     * - Nếu có từ khoá -> Cắt khoảng trắng và gọi Repository tìm kiếm.
     */
    @Override
    public List<Student> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        return studentRepository.search(keyword.trim());
    }

    // ── CÁC HÀM BỔ TRỢ (HELPERS) ─────────────────────────────────────────────

    /**
     * TODO 3.9 — Chuẩn hoá dữ liệu đầu vào:
     * - Cắt bỏ khoảng trắng thừa đầu và cuối chuỗi (trim).
     * - Chuyển toàn bộ email về chữ thường (lowercase) để đồng nhất.
     */
    private void normalize(Student s) {
        if (s.getName()  != null) s.setName(s.getName().trim());
        if (s.getEmail() != null) s.setEmail(s.getEmail().trim().toLowerCase());
    }

    /**
     * TODO 3.10 — Kiểm tra hợp lệ dữ liệu theo thứ tự bắt buộc:
     * 1. Kiểm tra Tên: không được rỗng, độ dài tối đa 100 ký tự.
     * 2. Kiểm tra Email: không được rỗng, phải khớp định dạng Regex.
     * 3. Kiểm tra Tuổi: không được rỗng, nằm trong khoảng [18, 60].
     */
    private void validate(Student s) {
        // 1. Kiểm tra Name
        if (s.getName() == null || s.getName().isBlank()) {
            throw new ValidationException("Name is required");
        }
        if (s.getName().length() > 100) {
            throw new ValidationException("Name must be at most 100 characters");
        }

        // 2. Kiểm tra Email
        if (s.getEmail() == null || !EMAIL.matcher(s.getEmail()).matches()) {
            throw new ValidationException("Invalid email");
        }

        // 3. Kiểm tra Age
        if (s.getAge() == null || s.getAge() < 18 || s.getAge() > 60) {
            throw new ValidationException("Age must be between 18 and 60");
        }
    }
}
