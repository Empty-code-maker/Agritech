package com.agritech.repositories.sqlite;

import com.agritech.models.EstadoRiego;
import com.agritech.repositories.RiegoRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/** Persistencia SQLite del estado de riego, con una única fila para el huerto inicial. */
public class SQLiteRiegoRepository implements RiegoRepository {

    @Override
    public EstadoRiego obtenerEstadoActual() throws SQLException {
        String consulta = "SELECT activo, fecha_hora_ultimo_cambio FROM estado_riego WHERE id = 1";

        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(consulta);
             ResultSet resultados = sentencia.executeQuery()) {
            
                if (!resultados.next()) {
                throw new SQLException("No se encontró el estado inicial del riego.");
            }

            String fechaGuardada = resultados.getString("fecha_hora_ultimo_cambio");
            LocalDateTime fechaHoraUltimoCambio = fechaGuardada == null
                    ? null
                    : LocalDateTime.parse(fechaGuardada);
            return new EstadoRiego(resultados.getInt("activo") == 1, fechaHoraUltimoCambio);
        }
    }

    @Override
    public void guardarEstado(EstadoRiego estado) throws SQLException {
        String consulta = "UPDATE estado_riego SET activo = ?, fecha_hora_ultimo_cambio = ? WHERE id = 1";

        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setInt(1, estado.estaActivo() ? 1 : 0);
            sentencia.setString(2, estado.obtenerFechaHoraUltimoCambio().toString());
            
            if (sentencia.executeUpdate() != 1) {
                throw new SQLException("No se pudo guardar el estado del riego.");
            }
        }
    }
}
