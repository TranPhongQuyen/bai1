package com.hsf302.ch4.service;

public interface StudentService {
    long count();                                   // TODO 6
    java.util.Optional<com.hsf302.ch4.pojo.Student> findById(Long id);            // TODO 6
}
