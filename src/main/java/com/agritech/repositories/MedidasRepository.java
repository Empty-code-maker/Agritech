package com.agritech.repositories;

import com.agritech.models.Medida;

import java.sql.SQLException;

/** Define la persistencia de lecturas simuladas. */
public interface MedidasRepository {

    Medida guardar(Medida medida) throws SQLException;
}
