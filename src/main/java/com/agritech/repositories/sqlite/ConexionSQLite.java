package com.agritech.repositories.sqlite;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Crea conexiones a la base SQLite local del proyecto. */
public final class ConexionSQLite {

    private static final String DIRECTORIO_DATOS = "datos";
    private static final String DIRECTORIO_DATOS_ANTERIOR = ".agritech";
    private static final String ARCHIVO_BASE_DATOS = "agritech.db";

    private ConexionSQLite() {
    }

    public static Connection obtenerConexion() throws SQLException {
        File directorio = new File(System.getProperty("user.dir"), DIRECTORIO_DATOS);
        
        if (!directorio.exists() && !directorio.mkdirs()) {
            throw new SQLException("No se pudo crear el directorio de datos: " + directorio);
        }

        File archivoBaseDatos = new File(directorio, ARCHIVO_BASE_DATOS);
        File archivoAnterior = new File(
                new File(System.getProperty("user.home"), DIRECTORIO_DATOS_ANTERIOR), ARCHIVO_BASE_DATOS);
        
                if (!archivoBaseDatos.exists() && archivoAnterior.isFile()) {
            try {
                Files.copy(archivoAnterior.toPath(), archivoBaseDatos.toPath());

            } catch (IOException excepcion) {
                throw new SQLException("No se pudo migrar la base de datos anterior.", excepcion);
            }
        }
        Connection conexion = DriverManager.getConnection("jdbc:sqlite:" + archivoBaseDatos.getAbsolutePath());
        try (Statement sentencia = conexion.createStatement()) {
            sentencia.execute("PRAGMA foreign_keys = ON");
            return conexion;
            
        } catch (SQLException excepcion) {
            conexion.close();
            throw excepcion;
        }
    }
}
