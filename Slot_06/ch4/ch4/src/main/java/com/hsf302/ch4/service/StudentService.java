package com.hsf302.ch4.service;

public interface StudentService {
    long count();                                   // TODO 6
    java.util.Optional<com.hsf302.ch4.pojo.Student> findById(Long id);            // TODO 6
    java.util.List<com.hsf302.ch4.pojo.Student> findAllOrderByGpaDesc();                              // TODO 7a
    org.springframework.data.domain.Page<com.hsf302.ch4.pojo.Student> findPage(int pageIndex, int size, String sortField);  // TODO 7b
}
