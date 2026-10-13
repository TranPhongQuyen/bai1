package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SinhVienControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testDanhSachCoDuLieu() throws Exception {
        mockMvc.perform(get("/sinhvien"))
                .andExpect(status().isOk())
                .andExpect(view().name("sinhvien/danh-sach"))
                .andExpect(model().attributeExists("sinhViens"))
                .andExpect(content().string(containsString("Nguyễn Văn An")))
                .andExpect(content().string(containsString("Giỏi")));
    }

    @Test
    void testDanhSachRong() throws Exception {
        mockMvc.perform(get("/sinhvien").param("empty", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("sinhvien/danh-sach"))
                .andExpect(model().attributeExists("sinhViens"))
                .andExpect(content().string(containsString("Không có sinh viên nào.")));
    }
}
