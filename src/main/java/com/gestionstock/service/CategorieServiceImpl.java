package com.gestionstock.service;

import com.gestionstock.model.Categorie;
import com.gestionstock.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class CategorieServiceImpl implements CategorieService {

    @Override
    public List<Categorie> findAll() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                    "SELECT c FROM Categorie c ORDER BY c.nom",
                    Categorie.class
            ).getResultList();
        }
    }

    @Override
    public Optional<Categorie> findById(int id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return Optional.ofNullable(em.find(Categorie.class, id));
        }
    }

    @Override
    public void addCategorie(Categorie c) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(c);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de la sauvegarde de la catégorie");
        } finally {
            em.close();
        }
    }

    @Override
    public void updateCategorie(Categorie c) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(c);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de la modification de la catégorie");
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteCategorie(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Categorie categorie = em.find(Categorie.class, id);

            if (categorie == null) {
                throw new RuntimeException("Catégorie introuvable");
            }

            if (!categorie.getProduits().isEmpty()) {
                throw new RuntimeException(
                        "Impossible de supprimer : " + categorie.getProduits().size()
                                + " produit(s) rattaché(s) à cette catégorie."
                );
            }

            em.remove(categorie);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException(e.getMessage());
        } finally {
            em.close();
        }
    }
}