package com.gestionstock.controller;

import com.gestionstock.model.Categorie;
import com.gestionstock.model.Fournisseur;
import com.gestionstock.model.Produit;
import com.gestionstock.service.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

public class AddProduitDialogController {

    @FXML
    private Label labelTitre;

    @FXML
    private TextField champNom;

    @FXML
    private ComboBox<Categorie> comboCategorie;

    @FXML
    private ComboBox<Fournisseur> comboFournisseur;

    @FXML
    private TextField champPrix;

    @FXML
    private TextField champPrixPromo;

    @FXML
    private TextField champQuantiteStock;

    @FXML
    private TextField champQuantiteMin;

    @FXML
    private Label labelErreur;

    private final ProduitService produitService = new ProduitServiceImpl();
    private final CategorieService categorieService = new CategorieServiceImpl();
    private final FournisseurService fournisseurService = new FournisseurServiceImpl();

    private Produit produitEnCoursDeModification;
    private boolean produitEnregistre = false;

    @FXML
    public void initialize() {
        List<Categorie> categories = categorieService.findAll();
        comboCategorie.setItems(FXCollections.observableArrayList(categories));
        comboCategorie.setConverter(convertisseur(Categorie::getNom));

        List<Fournisseur> fournisseurs = fournisseurService.findAll();
        comboFournisseur.setItems(FXCollections.observableArrayList(fournisseurs));
        comboFournisseur.setConverter(convertisseur(Fournisseur::getNom));
    }

    // Permet de remplir le formulaire si on modifie un produit existant
    public void setProduitAModifier(Produit produit) {
        this.produitEnCoursDeModification = produit;
        labelTitre.setText("Modifier le Produit");

        champNom.setText(produit.getNom());
        champPrix.setText(String.valueOf(produit.getPrix()));
        champPrixPromo.setText(produit.getPrixPromo() != null ? String.valueOf(produit.getPrixPromo()) : "");
        champQuantiteStock.setText(String.valueOf(produit.getQuantiteStock()));
        champQuantiteMin.setText(String.valueOf(produit.getQuantiteMin()));
        comboCategorie.setValue(produit.getCategorie());
        comboFournisseur.setValue(produit.getFournisseur());
    }

    private <T> StringConverter<T> convertisseur(java.util.function.Function<T, String> getNom) {
        return new StringConverter<>() {
            @Override
            public String toString(T objet) {
                return objet == null ? "" : getNom.apply(objet);
            }

            @Override
            public T fromString(String string) {
                return null;
            }
        };
    }

    @FXML
    private void enregistrer() {
        labelErreur.setText("");

        String nom = champNom.getText();
        if (nom == null || nom.trim().length() < 2) {
            labelErreur.setText("Le nom doit contenir au moins 2 caractères.");
            return;
        }

        double prix;
        try {
            prix = Double.parseDouble(champPrix.getText().trim());
            if (prix <= 0) {
                labelErreur.setText("Le prix doit être strictement positif.");
                return;
            }
        } catch (NumberFormatException e) {
            labelErreur.setText("Le prix doit être un nombre valide.");
            return;
        }

        Double prixPromo = null;
        String texteProfixPromo = champPrixPromo.getText();
        if (texteProfixPromo != null && !texteProfixPromo.isBlank()) {
            try {
                prixPromo = Double.parseDouble(texteProfixPromo.trim());
                if (prixPromo <= 0 || prixPromo >= prix) {
                    labelErreur.setText("Le prix promo doit être positif et strictement inférieur au prix normal.");
                    return;
                }
            } catch (NumberFormatException e) {
                labelErreur.setText("Le prix promo doit être un nombre valide.");
                return;
            }
        }

        int quantiteStock;
        int quantiteMin;
        try {
            quantiteStock = Integer.parseInt(champQuantiteStock.getText().trim());
            quantiteMin = Integer.parseInt(champQuantiteMin.getText().trim());
            if (quantiteStock < 0 || quantiteMin < 0) {
                labelErreur.setText("Les quantités doivent être supérieures ou égales à 0.");
                return;
            }
        } catch (NumberFormatException e) {
            labelErreur.setText("Les quantités doivent être des nombres entiers.");
            return;
        }

        Categorie categorie = comboCategorie.getValue();
        Fournisseur fournisseur = comboFournisseur.getValue();

        if (categorie == null || fournisseur == null) {
            labelErreur.setText("Veuillez sélectionner une catégorie et un fournisseur.");
            return;
        }

        try {
            if (produitEnCoursDeModification == null) {
                Produit nouveauProduit = new Produit(nom.trim(), quantiteStock, quantiteMin, prix, categorie, fournisseur);
                nouveauProduit.setPrixPromo(prixPromo);
                produitService.addProduit(nouveauProduit);
            } else {
                produitEnCoursDeModification.setNom(nom.trim());
                produitEnCoursDeModification.setPrix(prix);
                produitEnCoursDeModification.setPrixPromo(prixPromo);
                produitEnCoursDeModification.setQuantiteStock(quantiteStock);
                produitEnCoursDeModification.setQuantiteMin(quantiteMin);
                produitEnCoursDeModification.setCategorie(categorie);
                produitEnCoursDeModification.setFournisseur(fournisseur);
                produitService.updateProduit(produitEnCoursDeModification);
            }

            produitEnregistre = true;
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
        Stage stage = (Stage) champNom.getScene().getWindow();
        stage.close();
    }

    public boolean isProduitEnregistre() {
        return produitEnregistre;
    }
}