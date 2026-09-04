package com.gestionstock.service;

import com.gestionstock.model.Mouvement;
import com.gestionstock.model.enums.TypeMouvement;

import java.util.List;

public interface MouvementService {

    List<Mouvement> findAll();

    List<Mouvement> findByType(TypeMouvement type);

    void addMouvement(Mouvement mouvement);
}