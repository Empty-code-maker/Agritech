package com.agritech.repositories.sqlite;

import com.agritech.models.Planta;
import com.agritech.models.UmbralPlanta;
import com.agritech.repositories.PlantasRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Guarda plantas y sus umbrales en una transacción SQLite. */
public class SQLitePlantasRepository implements PlantasRepository {

    @Override
    public List<Planta> obtenerTodas() throws SQLException {
        String consulta = "SELECT p.id, p.nombre, u.humedad_suelo_minima "
                + "FROM planta p JOIN umbral_planta u "
                + "ON u.planta_id = p.id ORDER BY p.nombre COLLATE NOCASE";
        List<Planta> plantas = new ArrayList<Planta>();

        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(consulta);
             ResultSet resultados = sentencia.executeQuery()) {

            while (resultados.next()) {
                int id = resultados.getInt("id");
                UmbralPlanta umbral = new UmbralPlanta(
                        id, resultados.getDouble("humedad_suelo_minima"));
                plantas.add(new Planta(id, resultados.getString("nombre"), umbral));
            }
        }
        return plantas;
    }

    @Override
    public Planta crear(Planta planta) throws SQLException {
        String insertarPlanta = "INSERT INTO planta (nombre) VALUES (?)";
        String insertarUmbral = "INSERT INTO umbral_planta "
                + "(planta_id, humedad_suelo_minima, fecha_hora_actualizacion) VALUES (?, ?, ?)";

        try (Connection conexion = ConexionSQLite.obtenerConexion()) {
            conexion.setAutoCommit(false);

            try {
                int id;

                try (PreparedStatement sentencia = conexion.prepareStatement(
                        insertarPlanta, Statement.RETURN_GENERATED_KEYS)) {
                    sentencia.setString(1, planta.obtenerNombre().trim());
                    sentencia.executeUpdate();

                    try (ResultSet claves = sentencia.getGeneratedKeys()) {

                        if (!claves.next()) {
                            throw new SQLException("No se pudo obtener el id de la planta.");
                        }

                        id = claves.getInt(1);
                    }
                }

                try (PreparedStatement sentencia = conexion.prepareStatement(insertarUmbral)) {
                    sentencia.setInt(1, id);
                    sentencia.setDouble(2, planta.obtenerUmbral().obtenerHumedadSueloMinima());
                    sentencia.setString(3, LocalDateTime.now().toString());
                    sentencia.executeUpdate();
                }
                conexion.commit();
                return new Planta(id, planta.obtenerNombre().trim(),
                        new UmbralPlanta(id,
                                planta.obtenerUmbral().obtenerHumedadSueloMinima()));
            
            } catch (SQLException excepcion) {
                conexion.rollback();
                throw excepcion;
            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }

    @Override
    public void actualizar(Planta planta) throws SQLException {
        String actualizarPlanta = "UPDATE planta SET nombre = ? WHERE id = ?";
        String actualizarUmbral = "UPDATE umbral_planta SET humedad_suelo_minima = ?, "
                + "fecha_hora_actualizacion = ? WHERE planta_id = ?";
        String insertarUmbral = "INSERT INTO umbral_planta "
                + "(planta_id, humedad_suelo_minima, fecha_hora_actualizacion) VALUES (?, ?, ?)";
        String fechaActualizacion = LocalDateTime.now().toString();

        try (Connection conexion = ConexionSQLite.obtenerConexion()) {
            conexion.setAutoCommit(false);

            try {

                try (PreparedStatement sentencia = conexion.prepareStatement(actualizarPlanta)) {
                    sentencia.setString(1, planta.obtenerNombre().trim());
                    sentencia.setInt(2, planta.obtenerId());
                    
                    if (sentencia.executeUpdate() != 1) {
                        throw new SQLException("No se encontró la planta que se quiere actualizar.");
                    }
                }

                int filasActualizadas;
                try (PreparedStatement sentencia = conexion.prepareStatement(actualizarUmbral)) {
                    sentencia.setDouble(1, planta.obtenerUmbral().obtenerHumedadSueloMinima());
                    sentencia.setString(2, fechaActualizacion);
                    sentencia.setInt(3, planta.obtenerId());
                    filasActualizadas = sentencia.executeUpdate();
                }

                if (filasActualizadas == 0) {
                    try (PreparedStatement sentencia = conexion.prepareStatement(insertarUmbral)) {
                        sentencia.setInt(1, planta.obtenerId());
                        sentencia.setDouble(2, planta.obtenerUmbral().obtenerHumedadSueloMinima());
                        sentencia.setString(3, fechaActualizacion);
                        sentencia.executeUpdate();
                    }
                }

                conexion.commit();
                
            } catch (SQLException excepcion) {
                conexion.rollback();
                throw excepcion;
            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }
}
