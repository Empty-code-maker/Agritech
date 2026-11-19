package com.agritech.utils;

import com.agritech.repositories.sqlite.ConexionSQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

/** Crea el esquema inicial y el estado inactivo por defecto del riego. */
public final class InicializadorBaseDatos {

    private InicializadorBaseDatos() {
    }

    public static void inicializar() throws SQLException {
        String crearTablaRiego = "CREATE TABLE IF NOT EXISTS estado_riego ("
                + "id INTEGER PRIMARY KEY CHECK (id = 1), "
                + "activo INTEGER NOT NULL CHECK (activo IN (0, 1)), "
                + "fecha_hora_ultimo_cambio TEXT NULL"
                + ")";
        String crearTablaPlanta = "CREATE TABLE IF NOT EXISTS planta ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nombre TEXT NOT NULL COLLATE NOCASE UNIQUE"
                + ")";
        String crearTablaUmbral = "CREATE TABLE IF NOT EXISTS umbral_planta ("
                + "planta_id INTEGER PRIMARY KEY REFERENCES planta (id) ON DELETE CASCADE, "
                + "humedad_suelo_minima REAL NOT NULL CHECK (humedad_suelo_minima > 0 AND humedad_suelo_minima <= 100), "
                + "fecha_hora_actualizacion TEXT NOT NULL"
                + ")";
        String crearTablaMedida = "CREATE TABLE IF NOT EXISTS medida ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "planta_id INTEGER NOT NULL REFERENCES planta (id), "
                + "humedad_suelo REAL NOT NULL CHECK (humedad_suelo >= 0 AND humedad_suelo <= 100), "
                + "fecha_hora TEXT NOT NULL"
                + ")";
        String crearTablaAlerta = "CREATE TABLE IF NOT EXISTS alerta ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "planta_id INTEGER NOT NULL REFERENCES planta (id), "
                + "medida_id INTEGER NOT NULL REFERENCES medida (id), "
                + "tipo TEXT NOT NULL, "
                + "valor_observado REAL NOT NULL, "
                + "valor_umbral REAL NOT NULL, "
                + "fecha_hora_creacion TEXT NOT NULL, "
                + "fecha_hora_cierre TEXT NULL, "
                + "motivo_cierre TEXT NULL CHECK (motivo_cierre IN ('RECUPERADA', 'DESCARTADA') OR motivo_cierre IS NULL)"
                + ")";

        try (Connection conexion = ConexionSQLite.obtenerConexion();
             Statement sentencia = conexion.createStatement()) {
            sentencia.execute(crearTablaRiego);
            sentencia.execute(crearTablaPlanta);
            sentencia.execute(crearTablaUmbral);
            sentencia.execute(crearTablaMedida);
            sentencia.execute(crearTablaAlerta);
            sentencia.execute("CREATE INDEX IF NOT EXISTS indice_medida_planta_fecha "
                    + "ON medida (planta_id, fecha_hora)");
            sentencia.execute("CREATE INDEX IF NOT EXISTS indice_alerta_planta_cierre "
                    + "ON alerta (planta_id, tipo, fecha_hora_cierre)");
            sentencia.execute("CREATE UNIQUE INDEX IF NOT EXISTS indice_alerta_activa_por_tipo "
                    + "ON alerta (planta_id, tipo) WHERE fecha_hora_cierre IS NULL");

            try (PreparedStatement insertarEstado = conexion.prepareStatement(
                    "INSERT OR IGNORE INTO estado_riego (id, activo, fecha_hora_ultimo_cambio) VALUES (1, 0, NULL)")) {
                insertarEstado.executeUpdate();
            }
            
            try (PreparedStatement insertarPlanta = conexion.prepareStatement(
                    "INSERT OR IGNORE INTO planta (id, nombre) VALUES (1, 'Planta de demostración')")) {
                insertarPlanta.executeUpdate();
            }

            try (PreparedStatement insertarUmbral = conexion.prepareStatement(
                    "INSERT OR IGNORE INTO umbral_planta "
                            + "(planta_id, humedad_suelo_minima, fecha_hora_actualizacion) "
                            + "VALUES (1, 30, ?)")) {
                insertarUmbral.setString(1, LocalDateTime.now().toString());
                insertarUmbral.executeUpdate();
            }
        }
    }

}
