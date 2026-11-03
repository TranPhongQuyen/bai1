package com.example.ui.controller;

import com.example.data.exception.StudentNotFoundException;
import com.example.data.exception.ValidationException;
import com.example.data.pojo.Student;
import com.example.data.service.StudentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

/**
 * TẦNG ĐIỀU KHIỂN GIAO DIỆN (UI Controller - JavaFX + Spring)
 * - Đánh dấu @Component để Spring Boot quản lý như một Spring Bean.
 * - Chỉ phụ thuộc vào Interface StudentService (không gọi trực tiếp Repository hay EntityManager).
 */
@Component
public class StudentController {

    // ── Khai báo các thành phần giao diện liên kết với file student-view.fxml ──
    @FXML private TableView<Student> tableView;
    @FXML private TextField searchField, idField, nameField, emailField, ageField;
    @FXML private Button btnUpdate, btnDelete;
    @FXML private Label messageLabel, statusLabel;

    // Tiêm phụ thuộc (Dependency Injection) qua Constructor
    private final StudentService studentService;

    // TODO 5.1: Danh sách quan sát (ObservableList) duy nhất chứa dữ liệu hiển thị trên bảng
    private final ObservableList<Student> data = FXCollections.observableArrayList();

    /**
     * Constructor Injection: Spring tự động tiêm bean StudentServiceImpl vào đây
     * nhờ thiết lập loader.setControllerFactory(context::getBean) trong JavaFxApplication.
     */
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * TODO 5.2 — Hàm khởi tạo của Controller (chạy tự động sau khi giao diện FXML được load):
     * 1. Gán danh sách data vào TableView.
     * 2. Lắng nghe sự kiện click chọn dòng trên bảng để tự điền thông tin vào Form.
     * 3. Ràng buộc Data Binding: Tự động vô hiệu hoá (Disable) nút Update và Delete khi chưa chọn dòng nào.
     * 4. Gọi loadData() tải danh sách sinh viên ban đầu từ cơ sở dữ liệu.
     */
    @FXML
    public void initialize() {
        // Gán ObservableList vào TableView
        tableView.setItems(data);

        // Bắt sự kiện chọn dòng: khi người dùng click chọn 1 sinh viên, gọi fillForm để điền dữ liệu sang bên phải
        tableView.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    if (newSel != null) fillForm(newSel);
                });

        // Data Binding: Nếu chưa chọn dòng nào (isNull() = true) thì disable nút Update và Delete
        var noSelection = tableView.getSelectionModel().selectedItemProperty().isNull();
        btnUpdate.disableProperty().bind(noSelection);
        btnDelete.disableProperty().bind(noSelection);

        // Tải dữ liệu ban đầu
        loadData();
    }

    /**
     * TODO 5.3 — Tải danh sách toàn bộ sinh viên từ database:
     * - Gọi qua Service: studentService.findAll()
     * - Cập nhật dữ liệu vào bảng bằng data.setAll(...)
     * - Hiển thị số lượng ở thanh trạng thái dưới cùng (statusLabel)
     */
    private void loadData() {
        try {
            data.setAll(studentService.findAll());
            statusLabel.setText("Loaded " + data.size() + " students");
        } catch (Exception e) {
            showError("Cannot load data: " + e.getMessage());
            statusLabel.setText("Database error");
        }
    }

    /**
     * TODO 5.4 — Xử lý tìm kiếm sinh viên theo tên hoặc email:
     * - Kích hoạt khi bấm nút "Search" hoặc nhấn phím ENTER trong ô tìm kiếm.
     */
    @FXML
    private void handleSearch() {
        try {
            data.setAll(studentService.search(searchField.getText()));
            statusLabel.setText("Found " + data.size() + " students");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    /**
     * TODO 5.4 — Xử lý làm mới (Refresh):
     * - Xoá trắng ô tìm kiếm và nạp lại toàn bộ danh sách từ DB.
     */
    @FXML
    private void handleRefresh() {
        searchField.clear();
        loadData();
    }

    /**
     * TODO 6.1 — Đọc và kiểm tra dữ liệu thô từ Form giao diện:
     * - Kiểm tra trường Tuổi (Age): không được để trống và phải là số nguyên.
     * - Các quy tắc nghiệp vụ khác (tên trống, email sai định dạng, giới hạn tuổi) để ServiceImpl xử lý.
     */
    private Student readForm() {
        String ageText = ageField.getText() == null ? "" : ageField.getText().trim();
        if (ageText.isEmpty()) throw new ValidationException("Age is required");
        int age;
        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException e) {
            throw new ValidationException("Age must be a number");
        }
        return new Student(nameField.getText(), emailField.getText(), age);
    }

    /**
     * TODO 6.2 — Xử lý Thêm mới sinh viên (Add):
     * 1. Đọc dữ liệu từ form qua readForm().
     * 2. Gọi studentService.create(student).
     * 3. Thành công: báo xanh, xoá trắng form và tải lại bảng.
     * 4. Bắt lỗi nghiệp vụ (ValidationException): hiển thị chữ đỏ mà không crash app.
     */
    @FXML
    private void handleAdd() {
        try {
            Student saved = studentService.create(readForm());
            showInfo("Added: " + saved.getName());
            handleClear();
            loadData();
        } catch (ValidationException | StudentNotFoundException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("System error: " + e.getMessage());
        }
    }

    /**
     * TODO 6.3 — Xử lý Cập nhật sinh viên (Update):
     * 1. Lấy dòng sinh viên đang chọn trên bảng.
     * 2. Tạo đối tượng mới và gán ID của sinh viên được chọn.
     *    (LƯU Ý: Không sửa trực tiếp trên object đang chọn để tránh sai lệch dữ liệu nếu DB lỗi).
     * 3. Gọi studentService.update(edit).
     * 4. Thành công: báo xanh, xoá form và load lại bảng.
     */
    @FXML
    private void handleUpdate() {
        Student selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a student");
            return;
        }
        try {
            Student edit = readForm();
            edit.setId(selected.getId());
            Student updated = studentService.update(edit);
            showInfo("Updated: " + updated.getName());
            handleClear();
            loadData();
        } catch (ValidationException | StudentNotFoundException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("System error: " + e.getMessage());
        }
    }

    /**
     * TODO 6.4 — Xử lý Xoá sinh viên (Delete):
     * 1. Bật hộp thoại Alert (CONFIRMATION) hỏi: "Delete student <tên>?"
     * 2. Chỉ khi người dùng bấm OK mới gọi studentService.delete(id).
     * 3. Bấm Cancel sẽ huỷ thao tác, không xoá sinh viên.
     */
    @FXML
    private void handleDelete() {
        Student selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a student");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete student \"" + selected.getName() + "\"?",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText("Confirm delete");
        if (tableView.getScene() != null && tableView.getScene().getWindow() != null) {
            confirm.initOwner(tableView.getScene().getWindow());
        }
        confirm.showAndWait()
               .filter(rs -> rs == ButtonType.OK)
               .ifPresent(rs -> {
                   try {
                       studentService.delete(selected.getId());
                       showInfo("Deleted: " + selected.getName());
                       handleClear();
                       loadData();
                   } catch (StudentNotFoundException e) {
                       showError(e.getMessage());
                       loadData();
                   } catch (Exception e) {
                       showError("System error: " + e.getMessage());
                   }
               });
    }

    /**
     * TODO 6.5 — Xử lý Xoá trắng Form (Clear):
     * - Xoá nội dung 4 ô nhập (ID, Name, Email, Age).
     * - Bỏ chọn dòng trên TableView.
     * - Đặt con trỏ chuột (focus) về ô Name để sẵn sàng nhập mới.
     */
    @FXML
    private void handleClear() {
        idField.clear();
        nameField.clear();
        emailField.clear();
        ageField.clear();
        tableView.getSelectionModel().clearSelection();
        messageLabel.setText("");
        nameField.requestFocus();
    }

    /**
     * TODO 5.5 — Điền thông tin sinh viên được chọn vào các trường của Form bên phải
     */
    private void fillForm(Student s) {
        idField.setText(String.valueOf(s.getId()));
        nameField.setText(s.getName());
        emailField.setText(s.getEmail());
        ageField.setText(String.valueOf(s.getAge()));
        messageLabel.setText("");
    }

    /**
     * TODO 5.5 — Hiển thị thông báo thành công (màu xanh lá)
     */
    private void showInfo(String msg) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: green;");
    }

    /**
     * TODO 5.5 — Hiển thị thông báo lỗi (màu đỏ)
     */
    private void showError(String msg) {
        messageLabel.setText(msg);
        messageLabel.setStyle("-fx-text-fill: red;");
    }
}
