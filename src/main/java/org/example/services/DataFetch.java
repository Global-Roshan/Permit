package org.example.services;

import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.entity.Ejb;
import org.example.entity.Roles;
import org.example.entity.Methods;


import java.util.*;

public class DataFetch {

    private static final Logger logger = LogManager.getLogger(DataFetch.class);


    public List<Roles> fetchRoles(EntityManager entityManager) {
        logger.info("Fetching Roles");
        try {
            if (entityManager == null) {
                logger.error("EntityManager is null in Roles");
                throw new IllegalStateException("EntityManager is not injected.");
            }
            String jpql = "SELECT r FROM Roles r";
            TypedQuery<Roles> query = entityManager.createQuery(jpql, Roles.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error fetching roles", e);
            throw e;
        }
    }

    public List<Ejb> fetchEjbs(EntityManager entityManager) {
        logger.info("Fetching Ejbs");
        try {
            if (entityManager == null) {
                logger.error("EntityManager is null in EJBS");
                throw new IllegalStateException("EntityManager is not injected.");
            }
            String jpql = "SELECT e FROM Ejb e"; // Ensure entity name is correct
            TypedQuery<Ejb> query = entityManager.createQuery(jpql, Ejb.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error fetching ejbs", e);
            throw e;
        }
    }

    public List<Methods> fetchMethods(EntityManager entityManager) {
        logger.info("Fetching Methods");
        try {
            if (entityManager == null) {
                logger.error("EntityManager is null in Methods");
                throw new IllegalStateException("EntityManager is not injected.");
            }
            String jpql = "SELECT m FROM Methods m"; // Ensure entity name is correct
            TypedQuery<Methods> query = entityManager.createQuery(jpql, Methods.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error fetching methods", e);
            throw e;
        }
    }

}
