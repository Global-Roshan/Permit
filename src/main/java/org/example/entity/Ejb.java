package org.example.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ejbs")
public class Ejb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ejb_id")
    private int id;

    @Column(nullable = false, name = "ejb_name")
    private String ejb_name;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "role_id")
    private Roles roles;

    public Ejb(){}

    public Ejb(String ejb_name,Roles roles){
        this.ejb_name = ejb_name;
        this.roles = roles;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getEjb_name() {
        return ejb_name;
    }

    public void setEjb_name(String ejb_name) {
        this.ejb_name = ejb_name;
    }

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }
}
