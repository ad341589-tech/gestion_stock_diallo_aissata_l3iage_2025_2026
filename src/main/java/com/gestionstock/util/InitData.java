package com.gestionstock.util;

import com.gestionstock.model.Utilisateur;
import com.gestionstock.model.enums.RoleUtilisateur;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.mindrot.jbcrypt.BCrypt;

public class InitData {
    public static void main(String[] args) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            String hash = BCrypt.hashpw("password", BCrypt.gensalt());
            Utilisateur admin = new Utilisateur("admin@test.com", "Admin Test", hash, RoleUtilisateur.ADMIN);
            em.persist(admin);

            tx.commit();
            System.out.println("Utilisateur admin créé avec succès !");
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
            JPAUtil.fermer();
        }
    }
}