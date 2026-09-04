package com.gestionstock.service;

import com.gestionstock.util.JPAUtil;
import jakarta.persistence.EntityManager;

public class StatistiqueServiceImpl implements StatistiqueService {

    @Override
    public long getNombreProduits() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                    "SELECT COUNT(p) FROM Produit p",
                    Long.class
            ).getSingleResult();
        }
    }

    @Override
    public long getNombreMouvements() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                    "SELECT COUNT(m) FROM Mouvement m",
                    Long.class
            ).getSingleResult();
        }
    }

    @Override
    public double getValeurStock() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            Double valeur = em.createQuery(
                    "SELECT SUM(p.prix * p.quantiteStock) FROM Produit p",
                    Double.class
            ).getSingleResult();

            return valeur != null ? valeur : 0.0;
        }
    }

    @Override
    public long getNombreProduitsStockBas() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                    "SELECT COUNT(p) FROM Produit p " +
                            "WHERE p.quantiteStock <= p.quantiteMin",
                    Long.class
            ).getSingleResult();
        }
    }

    @Override
    public int getTotalEntrees() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            Long total = em.createQuery(
                    "SELECT SUM(m.quantite) FROM Mouvement m " +
                            "WHERE m.type = com.gestionstock.model.enums.TypeMouvement.ENTREE",
                    Long.class
            ).getSingleResult();

            return total != null ? total.intValue() : 0;
        }
    }

    @Override
    public int getTotalSorties() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            Long total = em.createQuery(
                    "SELECT SUM(m.quantite) FROM Mouvement m " +
                            "WHERE m.type = com.gestionstock.model.enums.TypeMouvement.SORTIE",
                    Long.class
            ).getSingleResult();

            return total != null ? total.intValue() : 0;
        }
    }
}