package com.gestionstock.controller;

import com.gestionstock.model.Mouvement;
import com.gestionstock.model.Produit;
import com.gestionstock.model.enums.TypeMouvement;
import com.gestionstock.service.MouvementService;
import com.gestionstock.service.MouvementServiceImpl;
import com.gestionstock.service.ProduitService;
import com.gestionstock.service.ProduitServiceImpl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class AddMouvementDialogController {

    @FXML
    private ComboBox<Produit> comboProduit;

    @FXML
    private RadioButton radioEntree;

    @FXML
    private RadioButton radioSortie;

    @FXML
    private TextField champQuantite;

    @FXML
    private TextArea champMotif;

    @FXML
    private Label labelApercu;

    @FXML
    private Label labelErreur;

    private final ProduitService produitService = new ProduitServiceImpl();
    private final MouvementService mouvementService = new MouvementServiceImpl();

    private boolean mouvementEnregistre = false;

    @FXML
    public void initialize() {
        List<Produit> produits = produitService.findAllProduits();
        comboProduit.setItems(FXCollections.observableArrayList(produits));

        comboProduit.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Produit p) {
                return p == null ? "" : p.getNom() + " (stock: " + p.getQuantiteStock() + ")";
            }

            @Override
            public Produit fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    private void mettreAJourApercu() {
        Produit produit = comboProduit.getValue();
        labelErreur.setText("");

        if (produit == null) {
            labelApercu.setText("");
            return;
        }

        int quantite;
        try {
            quantite = Integer.parseInt(champQuantite.getText().trim());
        } catch (NumberFormatException e) {
            labelApercu.setText("");
            return;
        }

        int stockActuel = produit.getQuantiteStock();
        int stockResultant = radioEntree.isSelected()
                ? stockActuel + quantite
                : stockActuel - quantite;

        labelApercu.setText("Stock actuel : " + stockActuel + " → Stock après mouvement : " + stockResultant);
    }

    @FXML
    private void enregistrer() {
        labelErreur.setText("");

        Produit produit = comboProduit.getValue();
        if (produit == null) {
            labelErreur.setText("Veuillez sélectionner un produit.");
            return;
        }

        int quantite;
        try {
            quantite = Integer.parseInt(champQuantite.getText().trim());
            if (quantite <= 0) {
                labelErreur.setText("La quantité doit être strictement positive.");
                return;
            }
        } catch (NumberFormatException e) {
            labelErreur.setText("La quantité doit être un nombre entier.");
            return;
        }

        TypeMouvement type = radioEntree.isSelected() ? TypeMouvement.ENTREE : TypeMouvement.SORTIE;
        String motif = champMotif.getText() == null ? "" : champMotif.getText().trim();

        if (type == TypeMouvement.SORTIE && motif.isEmpty()) {
            labelErreur.setText("Le motif est obligatoire pour une sortie.");
            return;
        }

        Mouvement mouvement = new Mouvement();
        mouvement.setProduit(produit);
        mouvement.setType(type);
        mouvement.setQuantite(quantite);
        mouvement.setMotif(motif);

        try {
            mouvementService.addMouvement(mouvement);
            mouvementEnregistre = true;
            fermerFenetre();
        } catch (Exception e) {
            labelErreur.setText(e.getMessage());
        }
    }

    @FXML
    private void annuler() {
        fermerFenetre();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) comboProduit.getScene().getWindow();
        stage.close();
    }

    public boolean isMouvementEnregistre() {
        return mouvementEnregistre;
    }
}