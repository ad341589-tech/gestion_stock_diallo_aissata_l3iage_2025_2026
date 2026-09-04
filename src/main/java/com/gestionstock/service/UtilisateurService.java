package com.gestionstock.service;

import com.gestionstock.model.Utilisateur;
import java.util.Optional;

public interface UtilisateurService {
    Optional<Utilisateur> authentifier(String email, String motDePasse);
}