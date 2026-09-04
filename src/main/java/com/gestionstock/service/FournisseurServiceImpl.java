package com.gestionstock.service;

import com.gestionstock.model.Fournisseur;
import com.gestionstock.util.JPAUtil;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class FournisseurServiceImpl implements FournisseurService {

    @Override
    public List<Fournisseur> findAll() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                    "SELECT f FROM Fournisseur f ORDER BY f.nom",
                    Fournisseur.class
            ).getResultList();
        }
    }

    @Override
    public Optional<Fournisseur> findById(int id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return Optional.ofNullable(em.find(Fournisseur.class, id));
        }
    }

    @Override
    public void addFournisseur(Fournisseur f) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(f);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de la sauvegarde du fournisseur");
        } finally {
            em.close();
        }
    }

    @Override
    public void updateFournisseur(Fournisseur f) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(f);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Erreur lors de la modification du fournisseur");
        } finally {
            em.close();
        }
    }

    @Override
    public long countProduitsRattaches(int fournisseurId) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                    "SELECT COUNT(p) FROM Produit p WHERE p.fournisseur.id = :fid",
                    Long.class
            ).setParameter("fid", fournisseurId).getSingleResult();
        }
    }

    @Override
    public void deleteFournisseur(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Fournisseur fournisseur = em.find(Fournisseur.class, id);

            if (fournisseur == null) {
                throw new RuntimeException("Fournisseur introuvable");
            }

            long nbProduits = countProduitsRattaches(id);
            if (nbProduits > 0) {
                throw new RuntimeException(
                        "Impossible de supprimer : " + nbProduits
                                + " produit(s) rattaché(s) à ce fournisseur."
                );
            }

            em.remove(fournisseur);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException(e.getMessage());
        } finally {
            em.close();
        }
    }
}