package com.gestionstock.controller;

import com.gestionstock.model.Utilisateur;
import com.gestionstock.service.UtilisateurService;
import com.gestionstock.service.UtilisateurServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LoginController {

    @FXML
    private TextField champEmail;

    @FXML
    private PasswordField champMotDePasse;

    @FXML
    private Label labelErreur;

    private final UtilisateurService utilisateurService = new UtilisateurServiceImpl();

    @FXML
    private void seConnecter() {
        String email = champEmail.getText().trim();
        String motDePasse = champMotDePasse.getText();

        if (email.isEmpty() || motDePasse.isEmpty()) {
            labelErreur.setText("Veuillez remplir tous les champs.");
            return;
        }

        Optional<Utilisateur> resultat = utilisateurService.authentifier(email, motDePasse);

        if (resultat.isEmpty()) {
            labelErreur.setText("Email ou mot de passe incorrect, ou compte désactivé.");
            return;
        }

        Utilisateur utilisateur = resultat.get();
        System.out.println("Connexion réussie : " + utilisateur.getEmail() + " (" + utilisateur.getRole() + ")");

        ouvrirMenuPrincipal();
    }

    private void ouvrirMenuPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gestionstock/main.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) champEmail.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion Stock IAGE");
        } catch (IOException e) {
            labelErreur.setText("Erreur lors du chargement du menu principal.");
            e.printStackTrace();
        }
    }
}