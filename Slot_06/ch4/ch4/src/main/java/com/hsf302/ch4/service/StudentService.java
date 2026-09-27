package com.hsf302.ch4.service;

public interface StudentService {
    long count();                                   // TODO 6
    java.util.Optional<com.hsf302.ch4.pojo.Student> findById(Long id);            // TODO 6
    java.util.List<com.hsf302.ch4.pojo.Student> findAllOrderByGpaDesc();                              // TODO 7a
    org.springframework.data.domain.Page<com.hsf302.ch4.pojo.Student> findPage(int pageIndex, int size, String sortField);  // TODO 7b
    java.util.Optional<com.hsf302.ch4.pojo.Student> findByStudentCode(String studentCode);   // TODO 8a
    boolean isEmailExisted(String email);                      // TODO 8b
    long countActive();                                        // TODO 8c
    java.util.List<com.hsf302.ch4.pojo.Student> searchByName(String keyword);        // TODO 9a
    java.util.List<com.hsf302.ch4.pojo.Student> findByEmailDomain(String domain);    // TODO 9b
    java.util.List<com.hsf302.ch4.pojo.Student> findWithoutEmail();                  // TODO 9c
    java.util.List<com.hsf302.ch4.pojo.Student> findByGpaRange(double min, double max);   // TODO 10a
    java.util.List<com.hsf302.ch4.pojo.Student> findActiveByGender(com.hsf302.ch4.pojo.Gender gender);        // TODO 10b
    java.util.List<com.hsf302.ch4.pojo.Student> findBornAfter(java.time.LocalDate date);            // TODO 10c
    java.util.List<com.hsf302.ch4.pojo.Student> findByDepartment(String deptCode);    // TODO 11a
    long countByDepartment(String deptCode);            // TODO 11b (dùng lại ở TODO 22)
    java.util.List<com.hsf302.ch4.pojo.Student> findTop3ByGpa();                      // TODO 11c
    java.util.List<com.hsf302.ch4.pojo.Student> findGoodStudents(String deptCode, double minGpa);   // TODO 12
    java.util.List<com.hsf302.ch4.pojo.Student> searchByKeyword(String keyword);   // TODO 13
    java.util.List<com.hsf302.ch4.pojo.Student> findAboveAverageGpa();   // TODO 15
    java.util.List<com.hsf302.ch4.pojo.Student> findTopNInDepartment(String deptCode, int n);   // TODO 17
    java.util.List<com.hsf302.ch4.dto.StudentSummary> getActiveSummaries();   // TODO 18
    org.springframework.data.domain.Page<com.hsf302.ch4.pojo.Student> findActiveByDepartment(String deptCode, int pageIndex, int size);   // TODO 19
    java.util.List<com.hsf302.ch4.pojo.Student> search(String kw, String deptCode, Double minGpa, Boolean active);   // TODO 24
}
