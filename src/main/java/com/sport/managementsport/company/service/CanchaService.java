package com.sport.managementsport.company.service;

import com.sport.managementsport.company.domain.Cancha;
import java.util.List;
import java.util.Optional;

public interface CanchaService {

    Cancha createCancha(Cancha cancha);

    Optional<Cancha> getCanchaById(Integer id);

    List<Cancha> getAllCanchas();

    Cancha updateCancha(Integer id, Cancha canchaDetails);

    void deleteCancha(Integer id);
}
