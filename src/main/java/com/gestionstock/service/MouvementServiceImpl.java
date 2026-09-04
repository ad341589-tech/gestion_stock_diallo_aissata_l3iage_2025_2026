package com.gestionstock.service;

import com.gestionstock.model.Mouvement;
import com.gestionstock.model.Produit;
import com.gestionstock.model.enums.TypeMouvement;
import com.gestionstock.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

public class MouvementServiceImpl implements MouvementService {

    @Override
    public List<Mouvement> findAll() {
        try (EntityManager em = JPAUtil.getEntityManager()) {

            return em.createQuery(
                    "SELECT m FROM Mouvement m " +
                            "JOIN FETCH m.produit " +
                            "ORDER BY m.dateMouvement DESC",
                    Mouvement.class
            ).getResultList();
        }
    }

    @Override
    public List<Mouvement> findByType(TypeMouvement type) {
        try (EntityManager em = JPAUtil.getEntityManager()) {

            return em.createQuery(
                            "SELECT m FROM Mouvement m " +
                                    "JOIN FETCH m.produit " +
                                    "WHERE m.type = :type " +
                                    "ORDER BY m.dateMouvement DESC",
                            Mouvement.class
                    )
                    .setParameter("type", type)
                    .getResultList();
        }
    }

    @Override
    public void addMouvement(Mouvement mouvement) {

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            // Récupérer le produit depuis la base
            Produit produit = em.find(
                    Produit.class,
                    mouvement.getProduit().getId()
            );

            if (produit == null) {
                throw new RuntimeException(
                        "Produit introuvable."
                );
            }

            // Vérifier la quantité
            if (mouvement.getQuantite() <= 0) {
                throw new RuntimeException(
                        "La quantité doit être strictement positive."
                );
            }

            // Entrée de stock
            if (mouvement.getType() == TypeMouvement.ENTREE) {

                produit.setQuantiteStock(
                        produit.getQuantiteStock()
                                + mouvement.getQuantite()
                );
            }

            // Sortie de stock
            else if (mouvement.getType() == TypeMouvement.SORTIE) {

                if (mouvement.getQuantite()
                        > produit.getQuantiteStock()) {

                    throw new RuntimeException(
                            "Stock insuffisant. Stock disponible : "
                                    + produit.getQuantiteStock()
                    );
                }

                produit.setQuantiteStock(
                        produit.getQuantiteStock()
                                - mouvement.getQuantite()
                );
            }

            else {
                throw new RuntimeException(
                        "Type de mouvement invalide."
                );
            }

            // Date automatique si elle n'est pas renseignée
            if (mouvement.getDateMouvement() == null) {
                mouvement.setDateMouvement(
                        LocalDateTime.now()
                );
            }

            // Enregistrer le mouvement
            em.persist(mouvement);

            // Mettre à jour le produit
            em.merge(produit);

            // Valider la transaction
            em.getTransaction().commit();

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw new RuntimeException(
                    e.getMessage()
            );

        } finally {
            em.close();
        }
    }
}