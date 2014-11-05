/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author student
 */
@Entity
@Table(name = "departamente")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DepartamentDB.findAll", query = "SELECT d FROM DepartamentDB d"),
    @NamedQuery(name = "DepartamentDB.findById", query = "SELECT d FROM DepartamentDB d WHERE d.id = :id"),
    @NamedQuery(name = "DepartamentDB.findByUser", query = "SELECT d FROM DepartamentDB d WHERE d.user = :user"),
    @NamedQuery(name = "DepartamentDB.findByNume", query = "SELECT d FROM DepartamentDB d WHERE d.nume = :nume")})
public class DepartamentDB implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "nume")
    private String nume;
    @JoinColumn(name = "user", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private UserDB user;

    public DepartamentDB() {
    }

    public DepartamentDB(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public UserDB getUser() {
        return user;
    }

    public void setUser(UserDB user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DepartamentDB)) {
            return false;
        }
        DepartamentDB other = (DepartamentDB) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.DepartamentDB[ id=" + id + " ]";
    }
    
}
