/** Implementa el repositorio de usuarios con SQLite. */

package com.agritech.repositories.sqlite;

import com.agritech.models.Usuario;
import com.agritech.repositories.UsuarioRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class SQLiteUsuarioRepository implements UsuarioRepository {

    @Override
    public Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario) {

        String sql = "SELECT id, nombre_usuario, password_hash "
                + "FROM usuario WHERE nombre_usuario = ?";

        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, nombreUsuario);

            try (ResultSet resultado = sentencia.executeQuery()) {

                if (resultado.next()) {

                    Usuario usuario = new Usuario(
                            resultado.getInt("id"),
                            resultado.getString("nombre_usuario"),
                            resultado.getString("password_hash")
                    );

                    return Optional.of(usuario);
                }
            }

        } catch (SQLException excepcion) {
            throw new RuntimeException(
                    "Error al buscar el usuario en SQLite.", excepcion
            );
        }

        return Optional.empty();
    }

    @Override
    public void guardar(Usuario usuario) {

        String sql = "INSERT INTO usuario (nombre_usuario, password_hash) "
                + "VALUES (?, ?)";

        try (Connection conexion = ConexionSQLite.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            sentencia.setString(1, usuario.getNombreUsuario());
            sentencia.setString(2, usuario.getPasswordHash());

            sentencia.executeUpdate();

            try (ResultSet resultado = sentencia.getGeneratedKeys()) {
                if (resultado.next()) {
                    usuario.setId(resultado.getInt(1));
                }
            }

        } catch (SQLException excepcion) {
            throw new RuntimeException(
                    "Error al guardar el usuario en SQLite.", excepcion
            );
        }
    }
}
