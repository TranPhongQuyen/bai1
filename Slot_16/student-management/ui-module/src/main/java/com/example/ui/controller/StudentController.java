package com.example.ui.controller;

import com.example.data.pojo.Student;
import com.example.data.service.StudentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

@Component
public class StudentController {

    @FXML private TableView<Student> tableView;
    @FXML private TextField searchField, idField, nameField, emailField, ageField;
    @FXML private Button btnUpdate, btnDelete;
    @FXML private Label messageLabel, statusLabel;

    private final StudentService studentService;

    // TODO 5.1: ObservableList duy nhất
    private final ObservableList<Student> data = FXCollections.observableArrayList();

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // TODO 5.2: initialize
    @FXML
    public void initialize() {
        tableView.setItems(data);

        // Listener: chọn dòng → điền form; bỏ chọn → xoá form
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> fillForm(newVal));

        // Disable Update/Delete khi chưa chọn dòng nào
        btnUpdate.disableProperty().bind(
                tableView.getSelectionModel().selectedItemProperty().isNull());
        btnDelete.disableProperty().bind(
                tableView.getSelectionModel().selectedItemProperty().isNull());

        loadData();
    }

    // TODO 5.3: load toàn bộ data
    private void loadData() {
        try {
            data.setAll(studentService.findAll());
            statusLabel.setText("Loaded " + data.size() + " students");
        } catch (Exception e) {
            showError("Cannot load data: " + e.getMessage());
            statusLabel.setText("Database error");
        }
    }

    // TODO 5.4: handleSearch / handleRefresh
    @FXML
    private void handleSearch() {
        try {
            String kw = searchField.getText();
            data.setAll(studentService.search(kw));
            statusLabel.setText("Found " + data.size() + " students");
        } catch (Exception e) {
            showError("Search error: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        searchField.clear();
        loadData();
        messageLabel.setText("");
    }

    // TODO 5.5: fillForm, showInfo, showError
    private void fillForm(Student s) {
        if (s == null) {
            handleClear();
            return;
        }
        idField.setText(s.getId() == null ? "" : s.getId().toString());
        nameField.setText(s.getName());
        emailField.setText(s.getEmail());
        ageField.setText(s.getAge() == null ? "" : s.getAge().toString());
        messageLabel.setText("");
    }

    private void showInfo(String msg) {
        messageLabel.setStyle("-fx-text-fill: #27ae60;");
        messageLabel.setText(msg);
    }

    private void showError(String msg) {
        messageLabel.setStyle("-fx-text-fill: #e74c3c;");
        messageLabel.setText(msg);
    }

    // Các action của TODO 6
    @FXML private void handleAdd()     { }
    @FXML private void handleUpdate()  { }
    @FXML private void handleDelete()  { }
    @FXML private void handleClear()   {
        idField.clear();
        nameField.clear();
        emailField.clear();
        ageField.clear();
        tableView.getSelectionModel().clearSelection();
        messageLabel.setText("");
        nameField.requestFocus();
    }
}
