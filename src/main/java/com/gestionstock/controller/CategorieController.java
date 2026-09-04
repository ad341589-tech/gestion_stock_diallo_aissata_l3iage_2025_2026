package com.gestionstock.controller;

import com.gestionstock.model.Categorie;
import com.gestionstock.service.CategorieService;
import com.gestionstock.service.CategorieServiceImpl;
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

public class CategorieController {

    @FXML
    private TableView<Categorie> tableCategories;

    @FXML
    private TableColumn<Categorie, String> colonneNom;

    @FXML
    private TableColumn<Categorie, String> colonneDescription;

    @FXML
    private TableColumn<Categorie, Integer> colonneNombreProduits;

    @FXML
    private TextField champNom;

    @FXML
    private TextField champDescription;

    private final CategorieService categorieService = new CategorieServiceImpl();

    private ObservableList<Categorie> listeCategories;

    @FXML
    public void initialize() {
        configurerColonnes();
        chargerDonnees();
    }

    /**
     * Configuration des colonnes du tableau
     */
    private void configurerColonnes() {

        colonneNom.setCellValueFactory(
                new PropertyValueFactory<>("nom")
        );

        colonneDescription.setCellValueFactory(
                new PropertyValueFactory<>("description")
        );

        colonneNombreProduits.setCellValueFactory(
                data -> javafx.beans.binding.Bindings.createObjectBinding(
                        () -> data.getValue().getProduits().size()
                )
        );
    }

    /**
     * Charger toutes les catégories depuis la base de données
     */
    private void chargerDonnees() {

        List<Categorie> categories = categorieService.findAll();

        listeCategories = FXCollections.observableArrayList(categories);

        tableCategories.setItems(listeCategories);
    }

    /**
     * Ajouter une catégorie
     */
    @FXML
    private void ajouterCategorie() {

        String nom = champNom.getText();
        String description = champDescription.getText();

        if (nom == null || nom.isBlank()) {
            afficherAlerte(
                    Alert.AlertType.WARNING,
                    "Validation",
                    "Le nom de la catégorie est obligatoire."
            );
            return;
        }

        if (nom.trim().length() < 2) {
            afficherAlerte(
                    Alert.AlertType.WARNING,
                    "Validation",
                    "Le nom doit contenir au moins 2 caractères."
            );
            return;
        }

        Categorie categorie = new Categorie(
                description,
                nom.trim()
        );

        try {
            categorieService.addCategorie(categorie);

            afficherAlerte(
                    Alert.AlertType.INFORMATION,
                    "Succès",
                    "La catégorie a été ajoutée avec succès."
            );

            viderChamps();
            chargerDonnees();

        } catch (Exception e) {
            afficherAlerte(
                    Alert.AlertType.ERROR,
                    "Erreur",
                    e.getMessage()
            );
        }
    }

    /**
     * Préparer la modification d'une catégorie
     */
    @FXML
    private void modifierCategorie() {

        Categorie categorieSelectionnee =
                tableCategories.getSelectionModel().getSelectedItem();

        if (categorieSelectionnee == null) {
            afficherAlerte(
                    Alert.AlertType.INFORMATION,
                    "Aucune sélection",
                    "Veuillez sélectionner une catégorie à modifier."
            );
            return;
        }

        String nom = champNom.getText();
        String description = champDescription.getText();

        if (nom == null || nom.isBlank()) {
            afficherAlerte(
                    Alert.AlertType.WARNING,
                    "Validation",
                    "Le nom de la catégorie est obligatoire."
            );
            return;
        }

        if (nom.trim().length() < 2) {
            afficherAlerte(
                    Alert.AlertType.WARNING,
                    "Validation",
                    "Le nom doit contenir au moins 2 caractères."
            );
            return;
        }

        categorieSelectionnee.setNom(nom.trim());
        categorieSelectionnee.setDescription(description);

        try {
            categorieService.updateCategorie(categorieSelectionnee);

            afficherAlerte(
                    Alert.AlertType.INFORMATION,
                    "Succès",
                    "La catégorie a été modifiée avec succès."
            );

            viderChamps();
            chargerDonnees();

        } catch (Exception e) {
            afficherAlerte(
                    Alert.AlertType.ERROR,
                    "Erreur",
                    e.getMessage()
            );
        }
    }

    /**
     * Supprimer une catégorie
     */
    @FXML
    private void supprimerCategorie() {

        Categorie categorieSelectionnee =
                tableCategories.getSelectionModel().getSelectedItem();

        if (categorieSelectionnee == null) {
            afficherAlerte(
                    Alert.AlertType.INFORMATION,
                    "Aucune sélection",
                    "Veuillez sélectionner une catégorie à supprimer."
            );
            return;
        }

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText(
                "Voulez-vous vraiment supprimer la catégorie \""
                        + categorieSelectionnee.getNom()
                        + "\" ?"
        );

        Optional<ButtonType> reponse =
                confirmation.showAndWait();

        if (reponse.isPresent()
                && reponse.get() == ButtonType.OK) {

            try {
                categorieService.deleteCategorie(
                        categorieSelectionnee.getId()
                );

                afficherAlerte(
                        Alert.AlertType.INFORMATION,
                        "Succès",
                        "La catégorie a été supprimée avec succès."
                );

                chargerDonnees();

            } catch (Exception e) {

                afficherAlerte(
                        Alert.AlertType.ERROR,
                        "Suppression impossible",
                        e.getMessage()
                );
            }
        }
    }

    /**
     * Charger les informations de la catégorie sélectionnée
     * dans le formulaire.
     */
    @FXML
    private void selectionnerCategorie() {

        Categorie categorieSelectionnee =
                tableCategories.getSelectionModel().getSelectedItem();

        if (categorieSelectionnee != null) {

            champNom.setText(categorieSelectionnee.getNom());

            champDescription.setText(
                    categorieSelectionnee.getDescription()
            );
        }
    }

    /**
     * Vider le formulaire
     */
    @FXML
    private void viderChamps() {

        champNom.clear();
        champDescription.clear();

        tableCategories.getSelectionModel().clearSelection();
    }

    /**
     * Afficher une alerte
     */
    private void afficherAlerte(
            Alert.AlertType type,
            String titre,
            String message) {

        Alert alerte = new Alert(type);

        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(message);

        alerte.showAndWait();
    }
}