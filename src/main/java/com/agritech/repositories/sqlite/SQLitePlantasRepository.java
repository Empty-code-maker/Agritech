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
        // Consultamos todos los datos de la planta y su umbral
        String consulta = "SELECT p.id, p.nombre, p.huerto_id, "
                + "p.area_m2, p.especie, p.ubicacion_zona, "
                + "p.fecha_alta, u.humedad_suelo_minima "
                + "FROM planta p JOIN umbral_planta u "
                + "ON u.planta_id = p.id "
                + "ORDER BY p.nombre COLLATE NOCASE";

        List<Planta> plantas = new ArrayList<Planta>();

        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(consulta);
             ResultSet resultados = sentencia.executeQuery()) {

            while (resultados.next()) {

                // Datos principales
                int id = resultados.getInt("id");
                String nombre = resultados.getString("nombre");
                String especie = resultados.getString("especie");
                String ubicacionZona = resultados.getString("ubicacion_zona");

                // Huerto asociado, que puede ser NULL
                int valorHuerto = resultados.getInt("huerto_id");
                Integer idHuerto = resultados.wasNull()
                        ? null : valorHuerto;

                // Superficie individual, que también puede ser NULL
                double valorArea = resultados.getDouble("area_m2");
                Double areaM2 = resultados.wasNull()
                        ? null : valorArea;

                // Fecha de alta
                String textoFecha = resultados.getString("fecha_alta");

                LocalDateTime fechaAlta = textoFecha == null
                        ? null : LocalDateTime.parse(textoFecha);

                // Recuperamos el umbral asociado
                UmbralPlanta umbral = new UmbralPlanta(
                        id, resultados.getDouble("humedad_suelo_minima"));

                // Construimos el objeto con todos sus atributos
                Planta planta = new Planta(
                        id, nombre, umbral,
                        idHuerto, areaM2,
                        especie, ubicacionZona, fechaAlta
                );

                plantas.add(planta);
            }
        }

        return plantas;
    }

    // Obtiene las plantas que pertenecen a un huerto
    @Override
    public List<Planta> obtenerPorHuerto(int idHuerto)
            throws SQLException {

        String consulta = "SELECT p.id, p.nombre, p.huerto_id, "
                + "p.area_m2, p.especie, p.ubicacion_zona, "
                + "p.fecha_alta, u.humedad_suelo_minima "
                + "FROM planta p JOIN umbral_planta u "
                + "ON u.planta_id = p.id "
                + "WHERE p.huerto_id = ? "
                + "ORDER BY p.nombre COLLATE NOCASE";

        List<Planta> plantas = new ArrayList<Planta>();

        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia =
                     conexion.prepareStatement(consulta)) {

            // Indicamos el huerto que queremos consultar
            sentencia.setInt(1, idHuerto);

            try (ResultSet resultados = sentencia.executeQuery()) {

                while (resultados.next()) {

                    int id = resultados.getInt("id");
                    String nombre = resultados.getString("nombre");
                    String especie = resultados.getString("especie");
                    String zona = resultados.getString("ubicacion_zona");

                    // Recuperamos el huerto asociado
                    int valorHuerto = resultados.getInt("huerto_id");
                    Integer huerto = resultados.wasNull()
                            ? null : valorHuerto;

                    // Recuperamos la superficie
                    double valorArea = resultados.getDouble("area_m2");
                    Double area = resultados.wasNull()
                            ? null : valorArea;

                    // Recuperamos la fecha de alta
                    String textoFecha = resultados.getString("fecha_alta");
                    LocalDateTime fechaAlta = textoFecha == null
                            ? null : LocalDateTime.parse(textoFecha);

                    // Recuperamos el umbral de la planta
                    UmbralPlanta umbral = new UmbralPlanta(
                            id,
                            resultados.getDouble("humedad_suelo_minima")
                    );

                    // Añadimos la planta a nuestra lista
                    plantas.add(new Planta(
                            id, nombre, umbral,
                            huerto, area, especie, zona, fechaAlta
                    ));
                }
            }
        }

        return plantas;
    }



    @Override
    public Planta crear(Planta planta) throws SQLException {

        // Obtenemos la fecha de alta
        LocalDateTime fechaAlta = planta.obtenerFechaAlta();

        // Si no tiene fecha, utilizamos la actual
        if (fechaAlta == null) {
            fechaAlta = LocalDateTime.now();
        }

        // Consulta para insertar la planta
        String insertarPlanta = "INSERT INTO planta "
                + "(nombre, huerto_id, area_m2, especie, "
                + "ubicacion_zona, fecha_alta) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        // Consulta para insertar su umbral
        String insertarUmbral = "INSERT INTO umbral_planta "
                + "(planta_id, humedad_suelo_minima, "
                + "fecha_hora_actualizacion) VALUES (?, ?, ?)";

        try (Connection conexion = ConexionSQLite.obtenerConexion()) {

            conexion.setAutoCommit(false);

            try {
                int id;

                // PRIMERA OPERACIÓN: guardar la planta
                try (PreparedStatement sentencia =
                             conexion.prepareStatement(
                                     insertarPlanta,
                                     Statement.RETURN_GENERATED_KEYS)) {

                    // Parámetro 1: nombre
                    sentencia.setString(
                            1, planta.obtenerNombre().trim());

                    // Parámetro 2: huerto
                    if (planta.obtenerIdHuerto() != null) {
                        sentencia.setInt(
                                2, planta.obtenerIdHuerto());
                    } else {
                        sentencia.setNull(
                                2, java.sql.Types.INTEGER);
                    }

                    // Parámetro 3: superficie
                    if (planta.obtenerAreaM2() != null) {
                        sentencia.setDouble(
                                3, planta.obtenerAreaM2());
                    } else {
                        sentencia.setNull(
                                3, java.sql.Types.REAL);
                    }

                    // Parámetro 4: especie
                    sentencia.setString(
                            4, planta.obtenerEspecie());

                    // Parámetro 5: zona del huerto
                    sentencia.setString(
                            5, planta.obtenerUbicacionZona());

                    // Parámetro 6: fecha de alta
                    sentencia.setString(
                            6, fechaAlta.toString());

                    // Ejecutamos la inserción de la planta
                    sentencia.executeUpdate();

                    // Recuperamos el ID generado por SQLite
                    try (ResultSet claves =
                                 sentencia.getGeneratedKeys()) {

                        if (!claves.next()) {
                            throw new SQLException(
                                    "No se pudo obtener el id de la planta.");
                        }

                        id = claves.getInt(1);
                    }
                }

                // SEGUNDA OPERACIÓN: guardar el umbral
                try (PreparedStatement sentencia =
                             conexion.prepareStatement(insertarUmbral)) {

                    sentencia.setInt(1, id);

                    sentencia.setDouble(
                            2, planta.obtenerUmbral()
                                    .obtenerHumedadSueloMinima());

                    sentencia.setString(
                            3, LocalDateTime.now().toString());

                    sentencia.executeUpdate();
                }

                // Confirmamos ambas inserciones
                conexion.commit();

                // Devolvemos la planta con todos sus datos
                return new Planta(
                        id,
                        planta.obtenerNombre().trim(),
                        new UmbralPlanta(
                                id,
                                planta.obtenerUmbral()
                                        .obtenerHumedadSueloMinima()),
                        planta.obtenerIdHuerto(),
                        planta.obtenerAreaM2(),
                        planta.obtenerEspecie(),
                        planta.obtenerUbicacionZona(),
                        fechaAlta
                );

            } catch (SQLException excepcion) {

                // Si falla alguna inserción, deshacemos los cambios
                conexion.rollback();
                throw excepcion;

            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }


    @Override
    public void actualizar(Planta planta) throws SQLException {

        // Actualizamos los datos de la planta
        String actualizarPlanta = "UPDATE planta SET nombre = ?, "
                + "huerto_id = COALESCE(?, huerto_id), "
                + "area_m2 = COALESCE(?, area_m2), "
                + "especie = COALESCE(?, especie), "
                + "ubicacion_zona = COALESCE(?, ubicacion_zona) "
                + "WHERE id = ?";

        // Actualizamos el umbral de humedad
        String actualizarUmbral = "UPDATE umbral_planta "
                + "SET humedad_suelo_minima = ?, "
                + "fecha_hora_actualizacion = ? "
                + "WHERE planta_id = ?";

        // Creamos el umbral si todavía no existe
        String insertarUmbral = "INSERT INTO umbral_planta "
                + "(planta_id, humedad_suelo_minima, "
                + "fecha_hora_actualizacion) VALUES (?, ?, ?)";

        String fechaActualizacion = LocalDateTime.now().toString();

        try (Connection conexion = ConexionSQLite.obtenerConexion()) {

            conexion.setAutoCommit(false);

            try {

                // PRIMERA OPERACIÓN: actualizar la planta
                try (PreparedStatement sentencia =
                             conexion.prepareStatement(actualizarPlanta)) {

                    // Parámetro 1: nombre
                    sentencia.setString(
                            1, planta.obtenerNombre().trim());

                    // Parámetro 2: huerto
                    if (planta.obtenerIdHuerto() != null) {
                        sentencia.setInt(2, planta.obtenerIdHuerto());
                    } else {
                        sentencia.setNull(2, java.sql.Types.INTEGER);
                    }

                    // Parámetro 3: superficie
                    if (planta.obtenerAreaM2() != null) {
                        sentencia.setDouble(3, planta.obtenerAreaM2());
                    } else {
                        sentencia.setNull(3, java.sql.Types.REAL);
                    }

                    // Parámetro 4: especie
                    sentencia.setString(4, planta.obtenerEspecie());

                    // Parámetro 5: zona dentro del huerto
                    sentencia.setString(5, planta.obtenerUbicacionZona());

                    // Parámetro 6: identificador de la planta
                    sentencia.setInt(6, planta.obtenerId());

                    // Comprobamos que la planta existe
                    if (sentencia.executeUpdate() != 1) {
                        throw new SQLException(
                                "No se encontró la planta que se quiere actualizar.");
                    }
                }

                // SEGUNDA OPERACIÓN: actualizar el umbral
                int filasActualizadas;

                try (PreparedStatement sentencia =
                             conexion.prepareStatement(actualizarUmbral)) {

                    sentencia.setDouble(
                            1, planta.obtenerUmbral()
                                    .obtenerHumedadSueloMinima());

                    sentencia.setString(2, fechaActualizacion);

                    sentencia.setInt(3, planta.obtenerId());

                    filasActualizadas = sentencia.executeUpdate();
                }

                // Si la planta no tiene umbral, lo creamos
                if (filasActualizadas == 0) {

                    try (PreparedStatement sentencia =
                                 conexion.prepareStatement(insertarUmbral)) {

                        sentencia.setInt(1, planta.obtenerId());

                        sentencia.setDouble(
                                2, planta.obtenerUmbral()
                                        .obtenerHumedadSueloMinima());

                        sentencia.setString(3, fechaActualizacion);

                        sentencia.executeUpdate();
                    }
                }

                // Confirmamos los cambios
                conexion.commit();

            } catch (SQLException excepcion) {

                // Si falla una operación, deshacemos los cambios
                conexion.rollback();
                throw excepcion;

            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }
}
