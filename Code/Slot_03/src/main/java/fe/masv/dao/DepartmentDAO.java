package fe.masv.dao;

import fe.masv.pojo.Department;
import fe.masv.util.JPAUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class DepartmentDAO {

    public void save(Department d) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(d);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public Department findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Department.class, id);
        } finally {
            em.close();
        }
    }

    public List<Department> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT d FROM Department d", Department.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Department update(Department d) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Department updated = em.merge(d);
            em.getTransaction().commit();
            return updated;
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Department d = em.find(Department.class, id);
            if (d != null) {
                em.remove(d);
            }
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    // TODO 2.6 - JPQL JOIN FETCH theo ID
    public Department findByIdWithEmployees(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT d FROM Department d JOIN FETCH d.employees WHERE d.id = :id",
                            Department.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }

    // TODO 2.8 - Tai hien N+1 Query Problem (1 query cho Department + N queries truy cap employees)
    public List<Department> findAllWithEmployeesNPlusOne() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Department> list = em.createQuery("SELECT d FROM Department d", Department.class)
                    .getResultList();
            for (Department d : list) {
                // Truy cap vao d.getEmployees() khi EntityManager dang mo
                // Do lazy loading, Hibernate se thuc thi 1 cau SQL SELECT N+1 cho moi phong ban
                int count = d.getEmployees().size();
                System.out.println("  [N+1 Query] Dept ID: " + d.getId() + " (" + d.getName() + ") -> Employee count: " + count);
            }
            return list;
        } finally {
            em.close();
        }
    }

    // TODO 2.9 - Fix N+1 bang JOIN FETCH (Lay tat ca Department va Employees trong 1 query duy nhat)
    public List<Department> findAllWithEmployees() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT DISTINCT d FROM Department d JOIN FETCH d.employees",
                            Department.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
