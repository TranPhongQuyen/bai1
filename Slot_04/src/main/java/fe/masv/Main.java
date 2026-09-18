package fe.masv;

import fe.masv.dao.EmployeeDAO;
import fe.masv.dao.ProjectDAO;
import fe.masv.pojo.Employee;
import fe.masv.pojo.Gender;
import fe.masv.pojo.Project;
import fe.masv.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // TODO 5.7 - Viết Main demo: tạo 3 Employee, 2 Project, phân công chéo
            // Khởi tạo 2 Project
            Project projectA = new Project("P01", "Project A", new BigDecimal("100000"), LocalDate.now(), LocalDate.now().plusMonths(6));
            Project projectB = new Project("P02", "Project B", new BigDecimal("200000"), LocalDate.now(), LocalDate.now().plusMonths(12));

            // Khởi tạo 3 Employee
            Employee emp1 = new Employee("Nguyen Van A", new BigDecimal("15000"), LocalDate.now(), "a@gmail.com", Gender.MALE, true);
            Employee emp2 = new Employee("Tran Thi B", new BigDecimal("18000"), LocalDate.now(), "b@gmail.com", Gender.FEMALE, true);
            Employee emp3 = new Employee("Le Van C", new BigDecimal("12000"), LocalDate.now(), "c@gmail.com", Gender.MALE, true);

            // Phân công chéo
            // NV1 tham gia Project A+B
            emp1.assignToProject(projectA);
            emp1.assignToProject(projectB);
            
            // NV2 tham gia Project B
            emp2.assignToProject(projectB);

            // NV3 tham gia Project A
            emp3.assignToProject(projectA);

            // Lưu vào cơ sở dữ liệu
            // Do chúng ta không dùng cascade = ALL cho quan hệ ManyToMany theo yêu cầu checklist
            // (vì dễ gây lỗi xóa nhầm), ta phải lưu riêng lẻ từng đối tượng.
            em.persist(projectA);
            em.persist(projectB);
            em.persist(emp1);
            em.persist(emp2);
            em.persist(emp3);

            tx.commit();

            System.out.println("\n=== DANH SÁCH PROJECT CỦA TỪNG NHÂN VIÊN ===");
            System.out.println(emp1.getFullName() + " tham gia: ");
            emp1.getProjects().forEach(p -> System.out.println("- " + p.getProjectName()));
            
            System.out.println(emp2.getFullName() + " tham gia: ");
            emp2.getProjects().forEach(p -> System.out.println("- " + p.getProjectName()));

            System.out.println(emp3.getFullName() + " tham gia: ");
            emp3.getProjects().forEach(p -> System.out.println("- " + p.getProjectName()));

            // TODO 5.8: Demo thống kê
            ProjectDAO projectDAO = new ProjectDAO();
            projectDAO.printProjectStatistics();
            
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        
        JPAUtil.close();
    }
}
