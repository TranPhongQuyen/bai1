package com.hsf302.chapter6.controller;

import com.hsf302.chapter6.dto.StudentForm;
import com.hsf302.chapter6.entity.Major;
import com.hsf302.chapter6.entity.Student;
import com.hsf302.chapter6.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentController {

    private static final String FORM_VIEW = "students/form";

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @ModelAttribute("majors")
    public List<Major> majors() {
        return studentService.getMajors();
    }

    // ==================== READ ALL ====================

    @GetMapping
    public String list(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "5") int size,
                       @RequestParam(value = "sort", defaultValue = "id,asc") String sort,
                       Model model) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        
        Page<Student> studentPage = studentService.findAll(keyword, pageable);
        model.addAttribute("page", studentPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentSort", sortParams[0]);
        model.addAttribute("currentDir", direction.name().toLowerCase());
        model.addAttribute("reverseDir", direction == Sort.Direction.ASC ? "desc" : "asc");
        return "students/list";
    }

    // ==================== READ ONE ====================

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        return studentService.findById(id)
                .map(student -> {
                    model.addAttribute("student", student);
                    return "students/detail";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("errorMsg", "Không tìm thấy sinh viên ID: " + id);
                    return "redirect:/students";
                });
    }

    // ==================== CREATE ====================

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("student", new StudentForm());
        return formView(model, false);
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("student") StudentForm student,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes ra) {
        if (!bindingResult.hasFieldErrors("email")
                && studentService.isEmailTaken(student.getEmail(), null)) {
            bindingResult.rejectValue("email", "duplicate", "Email đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            return formView(model, false);
        }
        try {
            studentService.create(student);
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("email", "duplicate", "Email đã tồn tại");
            return formView(model, false);
        }
        ra.addFlashAttribute("successMsg", "Thêm sinh viên thành công!");
        return "redirect:/students";
    }

    // ==================== UPDATE ====================

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        return studentService.findById(id)
                .map(student -> {
                    StudentForm form = new StudentForm();
                    form.setId(student.getId());
                    form.setName(student.getName());
                    form.setEmail(student.getEmail());
                    form.setAge(student.getAge());
                    form.setGpa(student.getGpa());
                    form.setMajorId(student.getMajor().getId());
                    model.addAttribute("student", form);
                    return formView(model, true);
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("errorMsg", "Không tìm thấy sinh viên ID: " + id);
                    return "redirect:/students";
                });
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("student") StudentForm student,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes ra) {
        student.setId(id);

        if (!bindingResult.hasFieldErrors("email")
                && studentService.isEmailTaken(student.getEmail(), id)) {
            bindingResult.rejectValue("email", "duplicate", "Email đã được sinh viên khác sử dụng");
        }
        if (bindingResult.hasErrors()) {
            return formView(model, true);
        }
        try {
            if (studentService.update(id, student)) {
                ra.addFlashAttribute("successMsg", "Cập nhật thành công!");
            } else {
                ra.addFlashAttribute("errorMsg", "Không tìm thấy sinh viên ID: " + id);
            }
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("email", "duplicate", "Email đã được sinh viên khác sử dụng");
            return formView(model, true);
        }
        return "redirect:/students";
    }

    // ==================== DELETE ====================

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes ra) {
        if (studentService.delete(id)) {
            ra.addFlashAttribute("successMsg", "Xóa sinh viên thành công!");
        } else {
            ra.addFlashAttribute("errorMsg", "Không tìm thấy sinh viên để xóa!");
        }
        return "redirect:/students";
    }

    // ==================== Helper ====================

    private String formView(Model model, boolean isEdit) {
        model.addAttribute("isEdit", isEdit);
        model.addAttribute("pageTitle", isEdit ? "Cập nhật sinh viên" : "Thêm sinh viên mới");
        return FORM_VIEW;
    }
}
