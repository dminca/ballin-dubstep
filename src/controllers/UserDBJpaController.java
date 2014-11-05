/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import controllers.exceptions.IllegalOrphanException;
import controllers.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.DepartamentDB;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.UserDB;

/**
 *
 * @author student
 */
public class UserDBJpaController implements Serializable {

    public UserDBJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(UserDB userDB) {
        if (userDB.getDepartamentDBCollection() == null) {
            userDB.setDepartamentDBCollection(new ArrayList<DepartamentDB>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<DepartamentDB> attachedDepartamentDBCollection = new ArrayList<DepartamentDB>();
            for (DepartamentDB departamentDBCollectionDepartamentDBToAttach : userDB.getDepartamentDBCollection()) {
                departamentDBCollectionDepartamentDBToAttach = em.getReference(departamentDBCollectionDepartamentDBToAttach.getClass(), departamentDBCollectionDepartamentDBToAttach.getId());
                attachedDepartamentDBCollection.add(departamentDBCollectionDepartamentDBToAttach);
            }
            userDB.setDepartamentDBCollection(attachedDepartamentDBCollection);
            em.persist(userDB);
            for (DepartamentDB departamentDBCollectionDepartamentDB : userDB.getDepartamentDBCollection()) {
                UserDB oldUserOfDepartamentDBCollectionDepartamentDB = departamentDBCollectionDepartamentDB.getUser();
                departamentDBCollectionDepartamentDB.setUser(userDB);
                departamentDBCollectionDepartamentDB = em.merge(departamentDBCollectionDepartamentDB);
                if (oldUserOfDepartamentDBCollectionDepartamentDB != null) {
                    oldUserOfDepartamentDBCollectionDepartamentDB.getDepartamentDBCollection().remove(departamentDBCollectionDepartamentDB);
                    oldUserOfDepartamentDBCollectionDepartamentDB = em.merge(oldUserOfDepartamentDBCollectionDepartamentDB);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(UserDB userDB) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserDB persistentUserDB = em.find(UserDB.class, userDB.getId());
            Collection<DepartamentDB> departamentDBCollectionOld = persistentUserDB.getDepartamentDBCollection();
            Collection<DepartamentDB> departamentDBCollectionNew = userDB.getDepartamentDBCollection();
            List<String> illegalOrphanMessages = null;
            for (DepartamentDB departamentDBCollectionOldDepartamentDB : departamentDBCollectionOld) {
                if (!departamentDBCollectionNew.contains(departamentDBCollectionOldDepartamentDB)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DepartamentDB " + departamentDBCollectionOldDepartamentDB + " since its user field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<DepartamentDB> attachedDepartamentDBCollectionNew = new ArrayList<DepartamentDB>();
            for (DepartamentDB departamentDBCollectionNewDepartamentDBToAttach : departamentDBCollectionNew) {
                departamentDBCollectionNewDepartamentDBToAttach = em.getReference(departamentDBCollectionNewDepartamentDBToAttach.getClass(), departamentDBCollectionNewDepartamentDBToAttach.getId());
                attachedDepartamentDBCollectionNew.add(departamentDBCollectionNewDepartamentDBToAttach);
            }
            departamentDBCollectionNew = attachedDepartamentDBCollectionNew;
            userDB.setDepartamentDBCollection(departamentDBCollectionNew);
            userDB = em.merge(userDB);
            for (DepartamentDB departamentDBCollectionNewDepartamentDB : departamentDBCollectionNew) {
                if (!departamentDBCollectionOld.contains(departamentDBCollectionNewDepartamentDB)) {
                    UserDB oldUserOfDepartamentDBCollectionNewDepartamentDB = departamentDBCollectionNewDepartamentDB.getUser();
                    departamentDBCollectionNewDepartamentDB.setUser(userDB);
                    departamentDBCollectionNewDepartamentDB = em.merge(departamentDBCollectionNewDepartamentDB);
                    if (oldUserOfDepartamentDBCollectionNewDepartamentDB != null && !oldUserOfDepartamentDBCollectionNewDepartamentDB.equals(userDB)) {
                        oldUserOfDepartamentDBCollectionNewDepartamentDB.getDepartamentDBCollection().remove(departamentDBCollectionNewDepartamentDB);
                        oldUserOfDepartamentDBCollectionNewDepartamentDB = em.merge(oldUserOfDepartamentDBCollectionNewDepartamentDB);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = userDB.getId();
                if (findUserDB(id) == null) {
                    throw new NonexistentEntityException("The userDB with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UserDB userDB;
            try {
                userDB = em.getReference(UserDB.class, id);
                userDB.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The userDB with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<DepartamentDB> departamentDBCollectionOrphanCheck = userDB.getDepartamentDBCollection();
            for (DepartamentDB departamentDBCollectionOrphanCheckDepartamentDB : departamentDBCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This UserDB (" + userDB + ") cannot be destroyed since the DepartamentDB " + departamentDBCollectionOrphanCheckDepartamentDB + " in its departamentDBCollection field has a non-nullable user field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(userDB);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<UserDB> findUserDBEntities() {
        return findUserDBEntities(true, -1, -1);
    }

    public List<UserDB> findUserDBEntities(int maxResults, int firstResult) {
        return findUserDBEntities(false, maxResults, firstResult);
    }

    private List<UserDB> findUserDBEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(UserDB.class));
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

    public UserDB findUserDB(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(UserDB.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserDBCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<UserDB> rt = cq.from(UserDB.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public UserDB getUserByUsername(String username){
        EntityManager em = getEntityManager();
        Query q = em.createNamedQuery("UserDB.findByUsername");
        q.setParameter("username", username);
        
        try{
            return (UserDB) q.getSingleResult();
        } catch(Exception e){
            return null;
        }
    }
    
    public boolean inregistrare(String username, String parola){
        UserDB user = userController.getUserByUsername(username);
        
        if(user == null){
            user = new UserDB();
            user.setUsername(username);
            user.setParola(parola);
            userController.create(user);
            return true;
        }
        
        return false;
    }
}
