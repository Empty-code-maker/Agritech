package com.agritech.services;

import com.agritech.models.Medida;
import com.agritech.repositories.MedidasRepository;

import java.sql.SQLException;

/** Valida y persiste las lecturas simuladas. */
public class MedidasService {

    private final MedidasRepository repositorioMedidas;

    public MedidasService(MedidasRepository repositorioMedidas) {
        this.repositorioMedidas = repositorioMedidas;
    }

    public Medida registrar(Medida medida) throws SQLException {
        if (medida.obtenerHumedadSuelo() < 0 || medida.obtenerHumedadSuelo() > 100) {
            throw new IllegalArgumentException("La humedad simulada debe estar entre 0 y 100 %.");
        }
        return repositorioMedidas.guardar(medida);
    }
}
