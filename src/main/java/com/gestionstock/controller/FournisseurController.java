package com.gestionstock.controller;

import com.gestionstock.model.Fournisseur;
import com.gestionstock.service.FournisseurService;
import com.gestionstock.service.FournisseurServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class FournisseurController {

    @FXML
    private TableView<Fournisseur> tableFournisseurs;

    @FXML
    private TableColumn<Fournisseur, String> colonneNom;

    @FXML
    private TableColumn<Fournisseur, String> colonneEmail;

    @FXML
    private TableColumn<Fournisseur, String> colonneTel;

    @FXML
    private TableColumn<Fournisseur, Integer> colonneNombreProduits;

    @FXML
    private TextField champNom;

    @FXML
    private TextField champEmail;

    @FXML
    private TextField champTel;

    private final FournisseurService fournisseurService = new FournisseurServiceImpl();

    private ObservableList<Fournisseur> listeFournisseurs;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    private static final Pattern TEL_PATTERN =
            Pattern.compile("^(77|78|75|76|70)\\d{7}$");

    @FXML
    public void initialize() {
        configurerColonnes();
        chargerDonnees();
    }

    private void configurerColonnes() {
        colonneNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colonneEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colonneTel.setCellValueFactory(new PropertyValueFactory<>("tel"));

        colonneNombreProduits.setCellValueFactory(
                data -> javafx.beans.binding.Bindings.createObjectBinding(
                        () -> (int) fournisseurService.countProduitsRattaches(data.getValue().getId())
                )
        );
    }

    private void chargerDonnees() {
        List<Fournisseur> fournisseurs = fournisseurService.findAll();
        listeFournisseurs = FXCollections.observableArrayList(fournisseurs);
        tableFournisseurs.setItems(listeFournisseurs);
    }

    @FXML
    private void ajouterFournisseur() {
        String nom = champNom.getText();
        String email = champEmail.getText();
        String tel = champTel.getText();

        String erreur = validerChamps(nom, email, tel);
        if (erreur != null) {
            afficherAlerte(Alert.AlertType.WARNING, "Validation", erreur);
            return;
        }

        Fournisseur fournisseur = new Fournisseur(nom.trim(), email, tel);

        try {
            fournisseurService.addFournisseur(fournisseur);
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Le fournisseur a été ajouté avec succès.");
            viderChamps();
            chargerDonnees();
        } catch (Exception e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    @FXML
    private void modifierFournisseur() {
        Fournisseur fournisseurSelectionne = tableFournisseurs.getSelectionModel().getSelectedItem();

        if (fournisseurSelectionne == null) {
            afficherAlerte(Alert.AlertType.INFORMATION, "Aucune sélection", "Veuillez sélectionner un fournisseur à modifier.");
            return;
        }

        String nom = champNom.getText();
        String email = champEmail.getText();
        String tel = champTel.getText();

        String erreur = validerChamps(nom, email, tel);
        if (erreur != null) {
            afficherAlerte(Alert.AlertType.WARNING, "Validation", erreur);
            return;
        }

        fournisseurSelectionne.setNom(nom.trim());
        fournisseurSelectionne.setEmail(email);
        fournisseurSelectionne.setTel(tel);

        try {
            fournisseurService.updateFournisseur(fournisseurSelectionne);
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Le fournisseur a été modifié avec succès.");
            viderChamps();
            chargerDonnees();
        } catch (Exception e) {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    @FXML
    private void supprimerFournisseur() {
        Fournisseur fournisseurSelectionne = tableFournisseurs.getSelectionModel().getSelectedItem();

        if (fournisseurSelectionne == null) {
            afficherAlerte(Alert.AlertType.INFORMATION, "Aucune sélection", "Veuillez sélectionner un fournisseur à supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Voulez-vous vraiment supprimer le fournisseur \"" + fournisseurSelectionne.getNom() + "\" ?");

        Optional<ButtonType> reponse = confirmation.showAndWait();

        if (reponse.isPresent() && reponse.get() == ButtonType.OK) {
            try {
                fournisseurService.deleteFournisseur(fournisseurSelectionne.getId());
                afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Le fournisseur a été supprimé avec succès.");
                chargerDonnees();
            } catch (Exception e) {
                afficherAlerte(Alert.AlertType.ERROR, "Suppression impossible", e.getMessage());
            }
        }
    }

    @FXML
    private void selectionnerFournisseur() {
        Fournisseur fournisseurSelectionne = tableFournisseurs.getSelectionModel().getSelectedItem();

        if (fournisseurSelectionne != null) {
            champNom.setText(fournisseurSelectionne.getNom());
            champEmail.setText(fournisseurSelectionne.getEmail());
            champTel.setText(fournisseurSelectionne.getTel());
        }
    }

    @FXML
    private void viderChamps() {
        champNom.clear();
        champEmail.clear();
        champTel.clear();
        tableFournisseurs.getSelectionModel().clearSelection();
    }

    private String validerChamps(String nom, String email, String tel) {
        if (nom == null || nom.isBlank()) {
            return "Le nom du fournisseur est obligatoire.";
        }
        if (nom.trim().length() < 2) {
            return "Le nom doit contenir au moins 2 caractères.";
        }
        if (email != null && !email.isBlank() && !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return "Le format de l'email n'est pas valide.";
        }
        if (tel != null && !tel.isBlank() && !TEL_PATTERN.matcher(tel.trim()).matches()) {
            return "Le téléphone doit contenir 9 chiffres et commencer par 77, 78, 75, 76 ou 70.";
        }
        return null;
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alerte = new Alert(type);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }
}