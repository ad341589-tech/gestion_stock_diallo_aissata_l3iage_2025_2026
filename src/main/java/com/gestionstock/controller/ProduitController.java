package com.gestionstock.controller;

import com.gestionstock.model.Categorie;
import com.gestionstock.model.Fournisseur;
import com.gestionstock.model.Produit;
import com.gestionstock.service.ProduitService;
import com.gestionstock.service.ProduitServiceImpl;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javafx.scene.control.cell.PropertyValueFactory;

import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;


public class ProduitController {

    // =========================================================
    // ÉLÉMENTS DU FXML
    // =========================================================

    @FXML
    private TableView<Produit> tableProduits;

    @FXML
    private TableColumn<Produit, String> colonneNom;

    @FXML
    private TableColumn<Produit, Double> colonnePrix;

    @FXML
    private TableColumn<Produit, Integer> colonneStock;

    @FXML
    private TableColumn<Produit, Integer> colonneStockMin;

    @FXML
    private TableColumn<Produit, String> colonneCategorie;

    @FXML
    private TableColumn<Produit, String> colonneFournisseur;

    // Nouvelle colonne Actions
    @FXML
    private TableColumn<Produit, Void> colonneActions;

    @FXML
    private TextField champRecherche;


    // =========================================================
    // SERVICE
    // =========================================================

    private final ProduitService produitService = new ProduitServiceImpl();

    private ObservableList<Produit> listeProduits;


    // =========================================================
    // INITIALISATION
    // =========================================================

    @FXML
    public void initialize() {

        configurerColonnes();

        chargerDonnees();
    }


    // =========================================================
    // CONFIGURATION DES COLONNES
    // =========================================================

    private void configurerColonnes() {

        // Nom
        colonneNom.setCellValueFactory(
                new PropertyValueFactory<>("nom")
        );


        // Prix
        colonnePrix.setCellValueFactory(
                new PropertyValueFactory<>("prix")
        );


        // Stock
        colonneStock.setCellValueFactory(
                new PropertyValueFactory<>("quantiteStock")
        );


        // Stock minimum
        colonneStockMin.setCellValueFactory(
                new PropertyValueFactory<>("quantiteMin")
        );


        // Catégorie
        colonneCategorie.setCellValueFactory(data -> {

            Categorie categorie = data.getValue().getCategorie();

            return new SimpleStringProperty(
                    categorie != null
                            ? categorie.getNom()
                            : ""
            );
        });


        // Fournisseur
        colonneFournisseur.setCellValueFactory(data -> {

            Fournisseur fournisseur = data.getValue().getFournisseur();

            return new SimpleStringProperty(
                    fournisseur != null
                            ? fournisseur.getNom()
                            : ""
            );
        });


        // =====================================================
        // BOUTON MODIFIER POUR CHAQUE LIGNE
        // =====================================================

        colonneActions.setCellFactory(param -> new TableCell<>() {

            private final Button boutonModifier =
                    new Button("Modifier");


            {
                boutonModifier.setOnAction(event -> {

                    // Récupérer le produit de cette ligne
                    Produit produit = getTableView()
                            .getItems()
                            .get(getIndex());

                    // Ouvrir le formulaire de modification
                    ouvrirDialogue(produit);
                });
            }


            @Override
            protected void updateItem(Void item, boolean empty) {

                super.updateItem(item, empty);

                if (empty) {

                    setGraphic(null);

                } else {

                    setGraphic(boutonModifier);
                }
            }
        });
    }


    // =========================================================
    // CHARGER LES PRODUITS
    // =========================================================

    private void chargerDonnees() {

        List<Produit> produits =
                produitService.findAllProduits();

        listeProduits =
                FXCollections.observableArrayList(produits);

        tableProduits.setItems(listeProduits);
    }


    // =========================================================
    // RECHERCHE
    // =========================================================

    @FXML
    private void rechercherProduits() {

        String recherche = champRecherche.getText();


        // Si la recherche est vide
        if (recherche == null || recherche.isBlank()) {

            tableProduits.setItems(listeProduits);

            return;
        }


        String rechercheMinuscule =
                recherche.trim().toLowerCase();


        ObservableList<Produit> resultats =
                listeProduits.filtered(produit ->

                        produit.getNom() != null
                                && produit.getNom()
                                .toLowerCase()
                                .contains(rechercheMinuscule)
                );


        tableProduits.setItems(resultats);
    }


    // =========================================================
    // AJOUTER UN PRODUIT
    // =========================================================

    @FXML
    private void ouvrirDialogueAjout() {

        ouvrirDialogue(null);
    }


    // =========================================================
    // MODIFIER UN PRODUIT
    // =========================================================

    @FXML
    private void ouvrirDialogueModification() {

        Produit produitSelectionne =
                tableProduits
                        .getSelectionModel()
                        .getSelectedItem();


        if (produitSelectionne == null) {

            Alert alerteInfo =
                    new Alert(Alert.AlertType.INFORMATION);

            alerteInfo.setTitle("Aucune sélection");

            alerteInfo.setHeaderText(null);

            alerteInfo.setContentText(
                    "Veuillez sélectionner un produit à modifier."
            );

            alerteInfo.showAndWait();

            return;
        }


        ouvrirDialogue(produitSelectionne);
    }


    // =========================================================
    // OUVRIR LE FORMULAIRE AJOUT / MODIFICATION
    // =========================================================

    private void ouvrirDialogue(Produit produitAModifier) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/com/gestionstock/AddProduitDialog.fxml"
                            )
                    );


            Parent racine = loader.load();


            AddProduitDialogController controleur =
                    loader.getController();


            // Si modification
            if (produitAModifier != null) {

                controleur.setProduitAModifier(
                        produitAModifier
                );
            }


            Stage dialogue = new Stage();


            if (produitAModifier == null) {

                dialogue.setTitle("Nouveau Produit");

            } else {

                dialogue.setTitle("Modifier le Produit");
            }


            dialogue.initModality(
                    Modality.APPLICATION_MODAL
            );


            dialogue.setScene(
                    new Scene(racine)
            );


            dialogue.showAndWait();


            // Recharger le tableau après enregistrement
            if (controleur.isProduitEnregistre()) {

                chargerDonnees();
            }


        } catch (Exception e) {

            e.printStackTrace();


            Alert alerteErreur =
                    new Alert(Alert.AlertType.ERROR);


            alerteErreur.setTitle("Erreur");

            alerteErreur.setHeaderText(
                    "Impossible d'ouvrir le formulaire"
            );

            alerteErreur.setContentText(
                    e.getMessage()
            );

            alerteErreur.showAndWait();
        }
    }


    // =========================================================
    // SUPPRIMER UN PRODUIT
    // =========================================================

    @FXML
    private void supprimerProduit() {

        Produit produitSelectionne =
                tableProduits
                        .getSelectionModel()
                        .getSelectedItem();


        // Aucun produit sélectionné
        if (produitSelectionne == null) {

            Alert alerteInfo =
                    new Alert(Alert.AlertType.INFORMATION);

            alerteInfo.setTitle("Aucune sélection");

            alerteInfo.setHeaderText(null);

            alerteInfo.setContentText(
                    "Veuillez sélectionner un produit à supprimer."
            );

            alerteInfo.showAndWait();

            return;
        }


        // Confirmation
        Alert alerteConfirmation =
                new Alert(Alert.AlertType.CONFIRMATION);


        alerteConfirmation.setTitle(
                "Confirmation de suppression"
        );


        alerteConfirmation.setHeaderText(null);


        alerteConfirmation.setContentText(
                "Voulez-vous vraiment supprimer le produit \""
                        + produitSelectionne.getNom()
                        + "\" ?"
        );


        Optional<ButtonType> reponse =
                alerteConfirmation.showAndWait();


        if (reponse.isPresent()
                && reponse.get() == ButtonType.OK) {


            produitService.deleteProduit(
                    produitSelectionne.getId()
            );


            // Actualiser le tableau
            chargerDonnees();
        }
    }
}