package com.gestionstock.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class InitData {
    public static void main(String[] args) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            em.createNativeQuery(
                    "ALTER TABLE mouvements DROP CONSTRAINT mouvements_type_check"
            ).executeUpdate();

            em.createNativeQuery(
                    "ALTER TABLE mouvements ADD CONSTRAINT mouvements_type_check " +
                            "CHECK (type IN ('ENTREE', 'SORTIE'))"
            ).executeUpdate();

            tx.commit();
            System.out.println("Contrainte corrigée avec succès !");
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
            JPAUtil.fermer();
        }
    }
}