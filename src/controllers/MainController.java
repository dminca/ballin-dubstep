/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author student
 */
public class MainController {
    private static MainController singleton;
    
    private EntityManagerFactory emf;
    private UserDBJpaController userController;
    private DepartamentDBJpaController departamentController;
    private AngajatDBJpaController angajatController;

    private MainController() {
        emf = Persistence.createEntityManagerFactory("java2c5e1PU");
        userController = new UserDBJpaController(emf);
        departamentController = new DepartamentDBJpaController(emf);
        angajatController = new AngajatDBJpaController(emf);
    }
    
    private static MainController getInstance(){
        if(singleton == null){
            singleton = new MainController();
        }
        
        return singleton;
    }
    
    
    
}
