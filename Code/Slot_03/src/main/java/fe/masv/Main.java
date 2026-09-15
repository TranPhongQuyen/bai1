package fe.masv;

import fe.masv.pojo.Department;
import fe.masv.pojo.Employee;
import fe.masv.pojo.Gender;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== TODO 2.4: TEST HELPER METHOD DONG BO 2 CHIEU ===");
        Department dept = new Department("IT", "Ha Noi");
        Employee emp = new Employee("test@company.com", "Nguyen Van Test", Gender.OTHER,
                new BigDecimal("10000000"), LocalDate.now());

        dept.addEmployee(emp);

        System.out.println("dept.getEmployees().contains(emp): " + dept.getEmployees().contains(emp));
        System.out.println("emp.getDepartment() == dept: " + (emp.getDepartment() == dept));
    }
}
