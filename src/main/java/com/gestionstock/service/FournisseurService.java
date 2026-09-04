package com.gestionstock.service;

import com.gestionstock.model.Fournisseur;

import java.util.List;
import java.util.Optional;

public interface FournisseurService {
    List<Fournisseur> findAll();
    Optional<Fournisseur> findById(int id);
    void addFournisseur(Fournisseur f);
    void updateFournisseur(Fournisseur f);
    void deleteFournisseur(int id);
    long countProduitsRattaches(int fournisseurId);
}