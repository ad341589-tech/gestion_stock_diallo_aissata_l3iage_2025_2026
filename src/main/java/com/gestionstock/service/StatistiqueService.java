package com.gestionstock.service;

public interface StatistiqueService {

    long getNombreProduits();

    long getNombreMouvements();

    double getValeurStock();

    long getNombreProduitsStockBas();

    int getTotalEntrees();

    int getTotalSorties();
}