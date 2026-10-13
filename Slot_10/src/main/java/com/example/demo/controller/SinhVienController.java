package com.example.demo.controller;

import com.example.demo.model.SinhVien;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
public class SinhVienController {

    @GetMapping("/sinhvien")
    public String danhSach(@RequestParam(value = "empty", required = false, defaultValue = "false") boolean empty, Model model) {
        List<SinhVien> danhSach;
        if (empty) {
            danhSach = Collections.emptyList();
        } else {
            danhSach = List.of(
                    new SinhVien("SV001", "Nguyễn Văn An", 8.5),
                    new SinhVien("SV002", "Trần Thị Bình", 6.2),
                    new SinhVien("SV003", "Lê Hoàng Cường", 7.0)
            );
        }
        model.addAttribute("sinhViens", danhSach);
        model.addAttribute("tieuDe", "Danh sách sinh viên");
        return "sinhvien/danh-sach";      // → templates/sinhvien/danh-sach.html
    }
}
