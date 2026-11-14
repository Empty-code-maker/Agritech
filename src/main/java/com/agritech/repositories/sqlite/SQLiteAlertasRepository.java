package com.agritech.repositories.sqlite;

import com.agritech.models.Alerta;
import com.agritech.repositories.AlertasRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

/** Persiste las alertas activas y conserva sus cierres para aplicar el bloqueo. */
public class SQLiteAlertasRepository implements AlertasRepository {

    @Override
    public Optional<Alerta> obtenerActiva(int idPlanta, String tipo) throws SQLException {
        String consulta = "SELECT * FROM alerta WHERE planta_id = ? AND tipo = ? "
                + "AND fecha_hora_cierre IS NULL ORDER BY id DESC LIMIT 1";

        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idPlanta);
            sentencia.setString(2, tipo);

            try (ResultSet resultados = sentencia.executeQuery()) {
                return resultados.next() ? Optional.of(leerAlerta(resultados)) : Optional.<Alerta>empty();
            }
        }
    }

    @Override
    public Optional<LocalDateTime> obtenerUltimoCierre(int idPlanta, String tipo) throws SQLException {
        String consulta = "SELECT fecha_hora_cierre FROM alerta WHERE planta_id = ? AND tipo = ? "
                + "AND fecha_hora_cierre IS NOT NULL ORDER BY id DESC LIMIT 1";

        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, idPlanta);
            sentencia.setString(2, tipo);

            try (ResultSet resultados = sentencia.executeQuery()) {
                return resultados.next()
                        ? Optional.of(LocalDateTime.parse(resultados.getString("fecha_hora_cierre")))
                        : Optional.<LocalDateTime>empty();
            }
        }
    }

    @Override
    public Alerta crear(Alerta alerta) throws SQLException {
        String insertar = "INSERT INTO alerta (planta_id, medida_id, tipo, "
                + "valor_observado, valor_umbral, fecha_hora_creacion) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(insertar)) {
            sentencia.setInt(1, alerta.obtenerIdPlanta());
            sentencia.setLong(2, alerta.obtenerIdMedida());
            sentencia.setString(3, alerta.obtenerTipo());
            sentencia.setDouble(4, alerta.obtenerValorObservado());
            sentencia.setDouble(5, alerta.obtenerValorUmbral());
            sentencia.setString(6, alerta.obtenerFechaHoraCreacion().toString());
            sentencia.executeUpdate();
            
            try (PreparedStatement consultaId = conexion.prepareStatement("SELECT last_insert_rowid()");
                 ResultSet resultadoId = consultaId.executeQuery()) {
                
                    if (!resultadoId.next()) {
                    throw new SQLException("No se pudo obtener el id de la alerta.");
                }
                
                return new Alerta(resultadoId.getLong(1), alerta.obtenerIdPlanta(),
                        alerta.obtenerIdMedida(), alerta.obtenerTipo(),
                        alerta.obtenerValorObservado(), alerta.obtenerValorUmbral(),
                        alerta.obtenerFechaHoraCreacion(), null, null);
            }
        }
    }

    @Override
    public void cerrar(long id, LocalDateTime fechaHora, String motivo) throws SQLException {
        String actualizar = "UPDATE alerta SET fecha_hora_cierre = ?, motivo_cierre = ? "
                + "WHERE id = ? AND fecha_hora_cierre IS NULL";
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(actualizar)) {
            sentencia.setString(1, fechaHora.toString());
            sentencia.setString(2, motivo);
            sentencia.setLong(3, id);
            sentencia.executeUpdate();
        }
    }

    private Alerta leerAlerta(ResultSet resultados) throws SQLException {
        String fechaCierre = resultados.getString("fecha_hora_cierre");
        return new Alerta(resultados.getLong("id"),
                resultados.getInt("planta_id"),
                resultados.getLong("medida_id"),
                resultados.getString("tipo"),
                resultados.getDouble("valor_observado"),
                resultados.getDouble("valor_umbral"),
                LocalDateTime.parse(resultados.getString("fecha_hora_creacion")),
                fechaCierre == null ? null : LocalDateTime.parse(fechaCierre),
                resultados.getString("motivo_cierre"));
    }
}
