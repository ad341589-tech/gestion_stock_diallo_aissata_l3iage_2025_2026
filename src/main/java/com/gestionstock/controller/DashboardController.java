package com.gestionstock.controller;

import com.gestionstock.model.Mouvement;
import com.gestionstock.model.Produit;
import com.gestionstock.model.enums.TypeMouvement;
import com.gestionstock.service.MouvementService;
import com.gestionstock.service.MouvementServiceImpl;
import com.gestionstock.service.ProduitService;
import com.gestionstock.service.ProduitServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;

import java.time.LocalDate;
import java.util.List;

public class DashboardController {

    @FXML
    private Label labelTotalProduits;

    @FXML
    private Label labelStockBas;

    @FXML
    private Label labelValeurStock;

    @FXML
    private Label labelMouvementsJour;

    @FXML
    private TableView<Produit> tableStockBas;

    @FXML
    private TableColumn<Produit, String> colonneNomStockBas;

    @FXML
    private TableColumn<Produit, Integer> colonneStockActuel;

    @FXML
    private TableColumn<Produit, Integer> colonneStockMin;

    private final ProduitService produitService = new ProduitServiceImpl();
    private final MouvementService mouvementService = new MouvementServiceImpl();

    @FXML
    public void initialize() {
        configurerColonnes();
        chargerStatistiques();
    }

    private void configurerColonnes() {
        colonneNomStockBas.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colonneStockActuel.setCellValueFactory(new PropertyValueFactory<>("quantiteStock"));
        colonneStockMin.setCellValueFactory(new PropertyValueFactory<>("quantiteMin"));
    }

    private void chargerStatistiques() {
        List<Produit> tousLesProduits = produitService.findAllProduits();
        List<Produit> produitsStockBas = produitService.findByStockBas();
        List<Mouvement> tousLesMouvements = mouvementService.findAll();

        // 1. Nombre total de produits
        labelTotalProduits.setText(String.valueOf(tousLesProduits.size()));

        // 2. Nombre de produits en stock bas
        labelStockBas.setText(String.valueOf(produitsStockBas.size()));

        // 3. Valeur totale du stock (Σ quantiteStock × prix)
        double valeurTotale = tousLesProduits.stream()
                .mapToDouble(p -> p.getQuantiteStock() * p.getPrix())
                .sum();
        labelValeurStock.setText(String.format("%,.0f FCFA", valeurTotale));

        // 4. Mouvements du jour
        LocalDate aujourdHui = LocalDate.now();
        long mouvementsAujourdhui = tousLesMouvements.stream()
                .filter(m -> m.getDateMouvement() != null
                        && m.getDateMouvement().toLocalDate().equals(aujourdHui))
                .count();
        long entreesJour = tousLesMouvements.stream()
                .filter(m -> m.getDateMouvement() != null
                        && m.getDateMouvement().toLocalDate().equals(aujourdHui)
                        && m.getType() == TypeMouvement.ENTREE)
                .count();
        long sortiesJour = mouvementsAujourdhui - entreesJour;
        labelMouvementsJour.setText(mouvementsAujourdhui + " (↑" + entreesJour + " / ↓" + sortiesJour + ")");

        // Tableau des produits en stock bas
        tableStockBas.setItems(FXCollections.observableArrayList(produitsStockBas));
    }
}