
package com.agritech.repositories.sqlite;

import com.agritech.models.Huerto;
import com.agritech.repositories.HuertosRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Guarda y consulta huertos en SQLite. */
public class SQLiteHuertosRepository implements HuertosRepository {




    // Crea un huerto y su estado de riego inicial
    @Override
    public int crear(Huerto huerto) throws SQLException {

        String sqlHuerto = "INSERT INTO huerto "
                + "(usuario_id, nombre, ubicacion, tipo_ubicacion, "
                + "area_total_m2, descripcion, fecha_alta) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        String sqlRiego = "INSERT INTO estado_riego "
                + "(huerto_id, activo, modo, fecha_hora_ultimo_cambio) "
                + "VALUES (?, 0, 'MANUAL', NULL)";

        try (Connection conexion = ConexionSQLite.obtenerConexion()) {

            // Iniciamos una transaccion
            conexion.setAutoCommit(false);

            try {
                int idHuerto;

                // Primero guardamos el huerto
                try (PreparedStatement sentencia =
                             conexion.prepareStatement(
                                     sqlHuerto,
                                     Statement.RETURN_GENERATED_KEYS)) {

                    sentencia.setInt(1, huerto.obtenerIdUsuario());
                    sentencia.setString(2, huerto.obtenerNombre());
                    sentencia.setString(3, huerto.obtenerUbicacion());
                    sentencia.setString(4, huerto.obtenerTipoUbicacion());
                    sentencia.setDouble(5, huerto.obtenerAreaTotalM2());
                    sentencia.setString(6, huerto.obtenerDescripcion());
                    sentencia.setString(7,
                            huerto.obtenerFechaAlta().toString());

                    if (sentencia.executeUpdate() != 1) {
                        throw new SQLException(
                                "No se pudo crear el huerto.");
                    }

                    // Recuperamos el ID generado
                    try (ResultSet claves = sentencia.getGeneratedKeys()) {

                        if (!claves.next()) {
                            throw new SQLException(
                                    "No se pudo obtener el ID del huerto.");
                        }

                        idHuerto = claves.getInt(1);
                    }
                }

                // Creamos el estado de riego del nuevo huerto
                try (PreparedStatement sentenciaRiego =
                             conexion.prepareStatement(sqlRiego)) {

                    sentenciaRiego.setInt(1, idHuerto);

                    if (sentenciaRiego.executeUpdate() != 1) {
                        throw new SQLException(
                                "No se pudo crear el estado de riego.");
                    }
                }

                // Confirmamos ambas operaciones
                conexion.commit();

                return idHuerto;

            } catch (SQLException | RuntimeException error) {

                // Si falla alguna operacion, deshacemos los cambios
                try {
                    conexion.rollback();
                } catch (SQLException errorRollback) {
                    error.addSuppressed(errorRollback);
                }

                throw error;
            }
        }
    }


    // Busca un huerto por su identificador
    @Override
    public Huerto buscarPorId(int id) throws SQLException {

        String sql = "SELECT * FROM huerto WHERE id = ?";

        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return convertirEnHuerto(resultado);
                }
            }
        }

        return null;
    }

    // Obtiene los huertos pertenecientes a un usuario
    @Override
    public List<Huerto> listarPorUsuario(int idUsuario) throws SQLException {

        List<Huerto> huertos = new ArrayList<>();

        String sql = "SELECT * FROM huerto "
                + "WHERE usuario_id = ? ORDER BY id";

        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idUsuario);

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    huertos.add(convertirEnHuerto(resultado));
                }
            }
        }

        return huertos;
    }

    // Convierte una fila de SQLite en un objeto Huerto
    private Huerto convertirEnHuerto(ResultSet resultado)
            throws SQLException {

        return new Huerto(
                resultado.getInt("id"),
                resultado.getInt("usuario_id"),
                resultado.getString("nombre"),
                resultado.getString("ubicacion"),
                resultado.getString("tipo_ubicacion"),
                resultado.getDouble("area_total_m2"),
                resultado.getString("descripcion"),
                LocalDateTime.parse(resultado.getString("fecha_alta"))
        );
    }
}
