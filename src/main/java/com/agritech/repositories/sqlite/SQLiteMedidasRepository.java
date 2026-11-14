package com.agritech.repositories.sqlite;

import com.agritech.models.Medida;
import com.agritech.repositories.MedidasRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/** Persiste las lecturas simuladas de humedad. */
public class SQLiteMedidasRepository implements MedidasRepository {

    @Override
    public Medida guardar(Medida medida) throws SQLException {
        String insertar = "INSERT INTO medida "
                + "(planta_id, humedad_suelo, fecha_hora) VALUES (?, ?, ?)";

        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(insertar)) {
            sentencia.setInt(1, medida.obtenerIdPlanta());
            sentencia.setDouble(2, medida.obtenerHumedadSuelo());
            sentencia.setString(3, medida.obtenerFechaHora().toString());
            sentencia.executeUpdate();

            try (PreparedStatement sentenciaId = conexion.prepareStatement("SELECT last_insert_rowid()");
                 java.sql.ResultSet resultadoId = sentenciaId.executeQuery()) {
                if (!resultadoId.next()) {
                    throw new SQLException("No se pudo obtener el id de la medición.");
                }
                return new Medida(resultadoId.getLong(1),
                        medida.obtenerIdPlanta(), medida.obtenerHumedadSuelo(),
                        medida.obtenerFechaHora());
            }
        }
    }
}
