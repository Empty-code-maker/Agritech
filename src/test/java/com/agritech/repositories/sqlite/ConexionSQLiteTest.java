
package com.agritech.repositories.sqlite;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Comprueba que SQLite activa las claves foráneas. */
public class ConexionSQLiteTest {

    @Test
    public void comprobarClavesForaneas() throws SQLException {

        // Abrimos una conexión con nuestra clase
        try (Connection conexion = ConexionSQLite.obtenerConexion();
             Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(
                     "PRAGMA foreign_keys")) {

            // Comprobamos que SQLite devuelve un resultado
            assertTrue(resultado.next());

            // El valor 1 significa que las claves foráneas están activadas
            assertEquals(1, resultado.getInt(1));
        }
    }
}
