
package com.agritech.services;

import com.agritech.models.Usuario;
import com.agritech.repositories.sqlite.SQLiteUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AutenticacionServiceTest {

    private AutenticacionService autenticacionService;
    private SQLiteUsuarioRepository repositorio;

    private final String nombreUsuario = "test_agritech";
    private final String password = "AgriTech123";

    @BeforeEach
    void preparar() {
        repositorio = new SQLiteUsuarioRepository();
        autenticacionService = new AutenticacionService(repositorio);
    }

    @Test
    void registrarUsuarioCorrectamente() {

        boolean resultado = autenticacionService.registrarUsuario(
                nombreUsuario, password
        );

        assertTrue(resultado);

        Usuario usuario = repositorio
                .buscarPorNombreUsuario(nombreUsuario)
                .orElse(null);

        assertNotNull(usuario);
        assertNotEquals(password, usuario.getPasswordHash());
    }

    @Test
    void rechazarUsuarioDuplicado() {

        autenticacionService.registrarUsuario(nombreUsuario, password);

        boolean resultado = autenticacionService.registrarUsuario(
                nombreUsuario, password
        );

        assertFalse(resultado);
    }

    @Test
    void iniciarSesionCorrectamente() {

        autenticacionService.registrarUsuario(nombreUsuario, password);

        boolean resultado = autenticacionService.login(
                nombreUsuario, password
        );

        assertTrue(resultado);
        assertNotNull(autenticacionService.getUsuarioActual());
    }

    @Test
    void rechazarPasswordIncorrecta() {

        autenticacionService.registrarUsuario(nombreUsuario, password);

        boolean resultado = autenticacionService.login(
                nombreUsuario, "PasswordIncorrecta"
        );

        assertFalse(resultado);
        assertNull(autenticacionService.getUsuarioActual());
    }

    @Test
    void cerrarSesionCorrectamente() {

        autenticacionService.registrarUsuario(nombreUsuario, password);
        autenticacionService.login(nombreUsuario, password);

        autenticacionService.logout();

        assertNull(autenticacionService.getUsuarioActual());
    }
}
