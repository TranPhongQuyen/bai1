package com.hsf302.ch4.service;

public interface DepartmentService {
    long count();                                   // TODO 6
    boolean existsById(Long id);                    // TODO 6
    java.util.List<com.hsf302.ch4.pojo.Department> findDepartmentsWithoutStudents();  // TODO 11d
}
