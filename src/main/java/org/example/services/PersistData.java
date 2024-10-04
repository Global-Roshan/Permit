package org.example.services;

import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.entity.Ejb;
import org.example.entity.Methods;
import org.example.entity.Roles;


import java.util.List;


public class PersistData {


    private static final Logger logger = LogManager.getLogger(PersistData.class);


    public void PersistRole(Roles roles,EntityManager em)
    {
        logger.info("Persisting {} Role into db", roles);
        em.getTransaction().begin();
        em.persist(roles);
        em.flush();
        em.getTransaction().commit();
    }

    public void PersistEjb(Ejb ejbs,EntityManager em)
    {
        logger.info("Persisting {} Ejb into db",ejbs);
        em.getTransaction().begin();
        em.persist(ejbs);
        em.flush();
        em.getTransaction().commit();
    }

    public void PersistMethod(List<Methods> methodList,EntityManager em)
    {
        logger.info("Persisting {} Method into db", methodList);
        em.getTransaction().begin();
        for(Methods method : methodList)
        {
            em.persist(method);
            em.flush();
        }
        em.getTransaction().commit();
    }

}
