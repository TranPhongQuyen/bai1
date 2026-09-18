package fe.masv.dao;

import fe.masv.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.List;

public class ProjectDAO {

    // TODO 5.8 - JPQL đếm số nhân viên active và tính tổng lương theo từng project
    public void printProjectStatistics() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT p.projectName, COUNT(e), SUM(e.salary) " +
                          "FROM Project p JOIN p.employees e " +
                          "WHERE e.active = true " +
                          "GROUP BY p.projectName";
            
            Query query = em.createQuery(jpql);
            List<Object[]> results = query.getResultList();

            System.out.println("\n=== THỐNG KÊ NHÂN VIÊN THEO PROJECT ===");
            for (Object[] result : results) {
                String projectName = (String) result[0];
                Long employeeCount = (Long) result[1];
                java.math.BigDecimal totalSalary = (java.math.BigDecimal) result[2];
                System.out.printf("Project: %s | Số nhân viên active: %d | Tổng lương: %s\n", 
                                  projectName, employeeCount, totalSalary);
            }
        } finally {
            em.close();
        }
    }
}
