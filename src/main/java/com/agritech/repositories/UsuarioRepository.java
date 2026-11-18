/** Define el acceso y persistencia de usuarios. */


package com.agritech.repositories;

import com.agritech.models.Usuario;
import java.util.Optional;

public interface UsuarioRepository {

    Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario);

    void guardar(Usuario usuario);

}

