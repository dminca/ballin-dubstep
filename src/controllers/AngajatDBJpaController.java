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
import model.AngajatDB;
import model.DepartamentDB;

/**
 *
 * @author student
 */
public class AngajatDBJpaController implements Serializable {

    public AngajatDBJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(AngajatDB angajatDB) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(angajatDB);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(AngajatDB angajatDB) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            angajatDB = em.merge(angajatDB);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = angajatDB.getId();
                if (findAngajatDB(id) == null) {
                    throw new NonexistentEntityException("The angajatDB with id " + id + " no longer exists.");
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
            AngajatDB angajatDB;
            try {
                angajatDB = em.getReference(AngajatDB.class, id);
                angajatDB.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The angajatDB with id " + id + " no longer exists.", enfe);
            }
            em.remove(angajatDB);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<AngajatDB> findAngajatDBEntities() {
        return findAngajatDBEntities(true, -1, -1);
    }

    public List<AngajatDB> findAngajatDBEntities(int maxResults, int firstResult) {
        return findAngajatDBEntities(false, maxResults, firstResult);
    }

    private List<AngajatDB> findAngajatDBEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(AngajatDB.class));
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

    public AngajatDB findAngajatDB(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(AngajatDB.class, id);
        } finally {
            em.close();
        }
    }

    public int getAngajatDBCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<AngajatDB> rt = cq.from(AngajatDB.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public List<AngajatDB> getAngajat(DepartamentDB d){
        EntityManager em = getEntityManager();
        Query q = em.createNamedQuery("AngajatDB.findByDepartament");
        q.setParameter("departament", d);
        
        return q.getResultList();
    }
    
}
