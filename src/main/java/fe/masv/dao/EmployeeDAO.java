package fe.masv.dao;

import fe.masv.pojo.Employee;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

public class EmployeeDAO {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("hsf302FU");

    // ---------- CREATE (TODO 0.3) ----------
    public void save(Employee e) {
        // Truoc dong nay: e dang o trang thai NEW/TRANSIENT
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(e); // -> e chuyen sang MANAGED, se duoc INSERT khi commit
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close(); // sau dong nay, e (neu con giu tham chieu) la DETACHED
        }
    }

    // ---------- READ (TODO 0.4) ----------
    public Employee findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Employee.class, id); // tra ve null neu khong ton tai
        } finally {
            em.close();
        }
    }

    public List<Employee> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT e FROM Employee e", Employee.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // ---------- READ co dieu kien (TODO 0.5) ----------
    public Employee findByEmail(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Employee> result = em.createQuery(
                            "SELECT e FROM Employee e WHERE e.email = :email", Employee.class)
                    .setParameter("email", email)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        } finally {
            em.close();
        }
    }

    public List<Employee> findBySalaryGreaterThanAndActive(BigDecimal minSalary) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT e FROM Employee e WHERE e.salary > :minSalary AND e.active = true",
                            Employee.class)
                    .setParameter("minSalary", minSalary)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // ---------- UPDATE (TODO 0.6) ----------
    public Employee update(Employee e) {
        // e truyen vao co the dang DETACHED (lay tu findById() o mot EntityManager khac)
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Employee merged = em.merge(e); // merge() TRA VE mot entity MANAGED khac
            em.getTransaction().commit();
            return merged; // PHAI dung object nay tiep, khong dung "e" cu
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

}
