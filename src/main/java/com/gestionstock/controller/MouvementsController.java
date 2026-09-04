package com.gestionstock.controller;

import com.gestionstock.model.Mouvement;
import com.gestionstock.model.enums.TypeMouvement;
import com.gestionstock.service.MouvementService;
import com.gestionstock.service.MouvementServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.util.List;

public class MouvementsController {

    @FXML
    private TableView<Mouvement> tableMouvements;

    @FXML
    private TableColumn<Mouvement, String> colonneProduit;

    @FXML
    private TableColumn<Mouvement, TypeMouvement> colonneType;

    @FXML
    private TableColumn<Mouvement, Integer> colonneQuantite;

    @FXML
    private TableColumn<Mouvement, String> colonneMotif;

    @FXML
    private TableColumn<Mouvement, LocalDateTime> colonneDate;

    private final MouvementService mouvementService =
            new MouvementServiceImpl();

    private ObservableList<Mouvement> listeMouvements;

    @FXML
    public void initialize() {
        configurerColonnes();
        chargerDonnees();
    }

    /**
     * Configuration des colonnes du tableau
     */
    private void configurerColonnes() {

        colonneProduit.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getProduit() != null
                                ? data.getValue().getProduit().getNom()
                                : ""
                )
        );

        colonneType.setCellValueFactory(
                new PropertyValueFactory<>("type")
        );

        colonneQuantite.setCellValueFactory(
                new PropertyValueFactory<>("quantite")
        );

        colonneMotif.setCellValueFactory(
                new PropertyValueFactory<>("motif")
        );

        colonneDate.setCellValueFactory(
                new PropertyValueFactory<>("dateMouvement")
        );
    }

    /**
     * Charger tous les mouvements
     */
    private void chargerDonnees() {

        List<Mouvement> mouvements =
                mouvementService.findAll();

        listeMouvements =
                FXCollections.observableArrayList(mouvements);

        tableMouvements.setItems(listeMouvements);
    }

    /**
     * Afficher uniquement les entrées
     */
    @FXML
    private void afficherEntrees() {

        List<Mouvement> mouvements =
                mouvementService.findByType(TypeMouvement.ENTREE);

        tableMouvements.setItems(
                FXCollections.observableArrayList(mouvements)
        );
    }

    /**
     * Afficher uniquement les sorties
     */
    @FXML
    private void afficherSorties() {

        List<Mouvement> mouvements =
                mouvementService.findByType(TypeMouvement.SORTIE);

        tableMouvements.setItems(
                FXCollections.observableArrayList(mouvements)
        );
    }

    /**
     * Afficher tous les mouvements
     */
    @FXML
    private void afficherTous() {
        tableMouvements.setItems(listeMouvements);
    }
    @FXML
    private void nouveauMouvement() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/gestionstock/AddMouvementDialog.fxml")
            );
            javafx.scene.Parent racine = loader.load();

            AddMouvementDialogController controleur = loader.getController();

            javafx.stage.Stage dialogue = new javafx.stage.Stage();
            dialogue.setTitle("Nouveau Mouvement");
            dialogue.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialogue.setScene(new javafx.scene.Scene(racine));
            dialogue.showAndWait();

            if (controleur.isMouvementEnregistre()) {
                chargerDonnees();
            }

        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Erreur lors de l'ouverture du formulaire.");
        }
    }

    /**
     * Afficher une erreur
     */
    private void afficherErreur(String message) {

        Alert alerte = new Alert(Alert.AlertType.ERROR);

        alerte.setTitle("Erreur");
        alerte.setHeaderText(null);
        alerte.setContentText(message);

        alerte.showAndWait();
    }
}