
/**
 * Contiene los casos de uso de autenticación.
 */

package com.agritech.services;

import com.agritech.models.Usuario;
import com.agritech.repositories.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

/**
 * Gestiona la autenticación y la sesión del usuario.
 */
public class AutenticacionService {

    private final UsuarioRepository usuarioRepository;
    private Usuario usuarioActual;

    public AutenticacionService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Inicia sesión si las credenciales son correctas.
     */
    public boolean login(String nombreUsuario, String password) {

        // Evitamos mantener una sesión anterior si falla el acceso.
        usuarioActual = null;

        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()
                || password == null || password.isEmpty()) {
            return false;
        }

        Optional<Usuario> resultado =
                usuarioRepository.buscarPorNombreUsuario(nombreUsuario.trim());

        if (!resultado.isPresent()) {
            return false;
        }

        Usuario usuario = resultado.get();

        if (!validateCredentials(usuario, password)) {
            return false;
        }

        usuarioActual = usuario;

        return true;
    }

    /**
     * Comprueba si la contraseña coincide con el hash almacenado.
     */
    private boolean validateCredentials(Usuario usuario, String password) {

        return BCrypt.checkpw(password, usuario.getPasswordHash());
    }

    /**
     * Cierra la sesión actual.
     */
    public void logout() {
        usuarioActual = null;
    }

    /**
     * Devuelve el usuario autenticado.
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
}
