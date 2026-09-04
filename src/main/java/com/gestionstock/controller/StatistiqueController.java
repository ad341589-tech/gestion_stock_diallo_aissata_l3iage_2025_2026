package com.gestionstock.controller;

import com.gestionstock.service.StatistiqueService;
import com.gestionstock.service.StatistiqueServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StatistiqueController {

    @FXML
    private Label labelNombreProduits;

    @FXML
    private Label labelNombreMouvements;

    @FXML
    private Label labelValeurStock;

    @FXML
    private Label labelStockBas;

    @FXML
    private Label labelTotalEntrees;

    @FXML
    private Label labelTotalSorties;

    private final StatistiqueService statistiqueService =
            new StatistiqueServiceImpl();

    @FXML
    public void initialize() {
        chargerStatistiques();
    }

    private void chargerStatistiques() {

        labelNombreProduits.setText(
                String.valueOf(statistiqueService.getNombreProduits())
        );

        labelNombreMouvements.setText(
                String.valueOf(statistiqueService.getNombreMouvements())
        );

        labelValeurStock.setText(
                String.format(
                        "%,.0f FCFA",
                        statistiqueService.getValeurStock()
                )
        );

        labelStockBas.setText(
                String.valueOf(
                        statistiqueService.getNombreProduitsStockBas()
                )
        );

        labelTotalEntrees.setText(
                String.valueOf(
                        statistiqueService.getTotalEntrees()
                )
        );

        labelTotalSorties.setText(
                String.valueOf(
                        statistiqueService.getTotalSorties()
                )
        );
    }
}