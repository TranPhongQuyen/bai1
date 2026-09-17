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
        // ==========================================
        // TODO 2.4: HELPER METHOD DONG BO 2 CHIEU
        // ==========================================
        System.out.println("\n=== TODO 2.4: KIEM TRA HELPER METHOD DONG BO 2 CHIEU ===");
        Department testDept = new Department("IT_Test", "Da Nang");
        Employee testEmp = new Employee("test_sync@company.com", "Test Sync", Gender.MALE,
                new BigDecimal("10000000"), LocalDate.now());
        testDept.addEmployee(testEmp);
        boolean inList = testDept.getEmployees().contains(testEmp);
        boolean deptSet = testEmp.getDepartment() == testDept;
        System.out.println("Contains in list: " + inList + " | Department reference set: " + deptSet);
        if (inList && deptSet) {
            System.out.println("-> TODO 2.4 PASSED: Helper method addEmployee hoat dong chinh xac!");
        }

        DepartmentDAO departmentDAO = new DepartmentDAO();

        // ==========================================
        // TODO 2.7: DEMO LUU CASCADE DEPARTMENT + 3 EMPLOYEES
        // ==========================================
        System.out.println("\n=== TODO 2.7: DEMO LUU CASCADE DEPARTMENT + 3 EMPLOYEES ===");
        long timestamp = System.currentTimeMillis();
        String deptName = "Marketing_" + timestamp;
        Department dept = new Department(deptName, "Ha Noi");

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
        if (found != null) {
            System.out.println("Phong ban: " + found.getName());
            for (Employee e : found.getEmployees()) {
                System.out.println("  - " + e);
            }
        }

        // ==========================================
        // TODO 2.8: TAI HIEN N+1 QUERY PROBLEM
        // ==========================================
        System.out.println("\n=== TODO 2.8: TAI HIEN N+1 QUERY PROBLEM ===");
        System.out.println("Dang chay findAllWithEmployeesNPlusOne() (1 query SELECT Department + N queries SELECT Employee)...");
        departmentDAO.findAllWithEmployeesNPlusOne();

        // ==========================================
        // TODO 2.9: FIX N+1 BANG JOIN FETCH
        // ==========================================
        System.out.println("\n=== TODO 2.9: FIX N+1 BANG JOIN FETCH ALL ===");
        System.out.println("Dang chay findAllWithEmployees() (Chi 1 query SELECT JOIN FETCH cho tat ca Department + Employees)...");
        java.util.List<Department> allDeptsWithEmps = departmentDAO.findAllWithEmployees();
        for (Department d : allDeptsWithEmps) {
            System.out.println("Dept: " + d.getName() + " | Employees count: " + d.getEmployees().size());
        }

        JPAUtil.close();
        System.out.println("\n=== HOAN THANH TAT CA TODO CHAPTER 2 (SLOT 03) ===");
    }
}
