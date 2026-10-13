# Slot 10 - Thực hành Thymeleaf trong Spring Boot

## Bài 1 (Cơ bản) — Hiển thị dữ liệu: `th:text`, `th:each`, `th:if`

### Các TODO đã hoàn thành:
- **TODO 0.1**: Tạo project Spring Boot với các dependencies cần thiết (Web, Thymeleaf, Validation).
- **TODO 0.2**: Cấu trúc thư mục & cấu hình `application.properties` (cổng 8081, tắt thymeleaf cache).
- **TODO 0.3**: Cài đặt Bootstrap và Bootstrap Icons offline trong thư mục static.
- **TODO 1.1**: Tạo Model `SinhVien` (maSV, hoTen, diemTB) với đầy đủ getter/setter.
- **TODO 1.2**: Xây dựng `SinhVienController` xử lý `GET /sinhvien`, truyền `sinhViens` và `tieuDe` vào Model.
- **TODO 1.3**: Xây dựng template Thymeleaf `templates/sinhvien/danh-sach.html` với bảng Bootstrap, định dạng điểm, nhãn xếp loại và thông báo danh sách rỗng.
- **TODO 1.4**: Kiểm thử cả 2 nhánh (có dữ liệu và rỗng), hỗ trợ tham số `?empty=true` và viết unit test tự động.
