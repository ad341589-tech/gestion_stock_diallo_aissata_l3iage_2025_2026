package com.gestionstock.service;

import com.gestionstock.model.Utilisateur;
import com.gestionstock.util.JPAUtil;
import jakarta.persistence.EntityManager;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class UtilisateurServiceImpl implements UtilisateurService {

    @Override
    public Optional<Utilisateur> authentifier(String email, String motDePasse) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Utilisateur utilisateur = em.createQuery(
                            "SELECT u FROM Utilisateur u WHERE u.email = :email", Utilisateur.class)
                    .setParameter("email", email)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (utilisateur == null || !utilisateur.isActif()) {
                return Optional.empty();
            }

            boolean motDePasseValide = BCrypt.checkpw(motDePasse, utilisateur.getMotDePasseHash());
            if (!motDePasseValide) {
                return Optional.empty();
            }

            return Optional.of(utilisateur);
        } finally {
            em.close();
        }
    }
}