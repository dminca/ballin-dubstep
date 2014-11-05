/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import controllers.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.DepartamentDB;
import model.UserDB;

/**
 *
 * @author student
 */
public class DepartamentDBJpaController implements Serializable {

    public DepartamentDBJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DepartamentDB departamentDB) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserDB user = departamentDB.getUser();
            if (user != null) {
                user = em.getReference(user.getClass(), user.getId());
                departamentDB.setUser(user);
            }
            em.persist(departamentDB);
            if (user != null) {
                user.getDepartamentDBCollection().add(departamentDB);
                user = em.merge(user);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(DepartamentDB departamentDB) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DepartamentDB persistentDepartamentDB = em.find(DepartamentDB.class, departamentDB.getId());
            UserDB userOld = persistentDepartamentDB.getUser();
            UserDB userNew = departamentDB.getUser();
            if (userNew != null) {
                userNew = em.getReference(userNew.getClass(), userNew.getId());
                departamentDB.setUser(userNew);
            }
            departamentDB = em.merge(departamentDB);
            if (userOld != null && !userOld.equals(userNew)) {
                userOld.getDepartamentDBCollection().remove(departamentDB);
                userOld = em.merge(userOld);
            }
            if (userNew != null && !userNew.equals(userOld)) {
                userNew.getDepartamentDBCollection().add(departamentDB);
                userNew = em.merge(userNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = departamentDB.getId();
                if (findDepartamentDB(id) == null) {
                    throw new NonexistentEntityException("The departamentDB with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DepartamentDB departamentDB;
            try {
                departamentDB = em.getReference(DepartamentDB.class, id);
                departamentDB.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The departamentDB with id " + id + " no longer exists.", enfe);
            }
            UserDB user = departamentDB.getUser();
            if (user != null) {
                user.getDepartamentDBCollection().remove(departamentDB);
                user = em.merge(user);
            }
            em.remove(departamentDB);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<DepartamentDB> findDepartamentDBEntities() {
        return findDepartamentDBEntities(true, -1, -1);
    }

    public List<DepartamentDB> findDepartamentDBEntities(int maxResults, int firstResult) {
        return findDepartamentDBEntities(false, maxResults, firstResult);
    }

    private List<DepartamentDB> findDepartamentDBEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DepartamentDB.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public DepartamentDB findDepartamentDB(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DepartamentDB.class, id);
        } finally {
            em.close();
        }
    }

    public int getDepartamentDBCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DepartamentDB> rt = cq.from(DepartamentDB.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
