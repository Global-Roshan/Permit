package org.example.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "methods")
public class Methods {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "method_id")
    private int id;

    @Column(nullable = false, name = "method_name")
    private String method_name;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "ejb_id")
    private Ejb ejb;

    public Methods(){}

    public Methods(String method_name,Ejb ejb){
        this.method_name = method_name;
        this.ejb = ejb;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMethod_name() {
        return method_name;
    }

    public void setMethod_name(String method_name) {
        this.method_name = method_name;
    }

    public Ejb getEjb() {
        return ejb;
    }

    public void setEjb(Ejb ejb) {
        this.ejb = ejb;
    }
}
