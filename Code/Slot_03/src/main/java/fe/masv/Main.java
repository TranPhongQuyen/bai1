package fe.masv;

import fe.masv.dao.DepartmentDAO;
import fe.masv.pojo.Department;
import fe.masv.pojo.Employee;
import fe.masv.pojo.Gender;
import fe.masv.util.JPAUtil;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        DepartmentDAO departmentDAO = new DepartmentDAO();

        System.out.println("=== TODO 2.7: DEMO LUU CASCADE DEPARTMENT + 3 EMPLOYEES ===");
        String deptName = "Marketing_" + System.currentTimeMillis();
        Department dept = new Department(deptName, "Ha Noi");

        long timestamp = System.currentTimeMillis();
        Employee e1 = new Employee("a_" + timestamp + "@company.com", "Nguyen Van A", Gender.MALE,
                new BigDecimal("15000000"), LocalDate.of(2022, 1, 10));
        Employee e2 = new Employee("b_" + timestamp + "@company.com", "Tran Thi B", Gender.FEMALE,
                new BigDecimal("18000000"), LocalDate.of(2021, 6, 1));
        Employee e3 = new Employee("c_" + timestamp + "@company.com", "Le Van C", Gender.OTHER,
                new BigDecimal("12000000"), LocalDate.of(2023, 3, 15));

        dept.addEmployee(e1);
        dept.addEmployee(e2);
        dept.addEmployee(e3);

        // Chi can persist department - cascade = ALL tu lo phan Employee
        departmentDAO.save(dept);
        System.out.println("Luu thanh cong Department, id = " + dept.getId());

        // Tim lai kem employees bang JOIN FETCH (TODO 2.6)
        Department found = departmentDAO.findByIdWithEmployees(dept.getId());
        System.out.println("Phong ban: " + found.getName());
        for (Employee e : found.getEmployees()) {
            System.out.println("  - " + e);
        }

        JPAUtil.close();
    }
}
