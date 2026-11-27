
package com.agritech.repositories;

import com.agritech.models.Huerto;
import java.sql.SQLException;
import java.util.List;

/** Operaciones para gestionar los huertos. */
public interface HuertosRepository {

    // Guarda un huerto y devuelve su ID generado
    int crear(Huerto huerto) throws SQLException;

    // Busca un huerto utilizando su identificador
    Huerto buscarPorId(int id) throws SQLException;

    // Devuelve todos los huertos de un usuario
    List<Huerto> listarPorUsuario(int idUsuario)
            throws SQLException;
}
