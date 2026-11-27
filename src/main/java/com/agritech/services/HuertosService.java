
package com.agritech.services;

import com.agritech.models.Huerto;
import com.agritech.repositories.HuertosRepository;

import java.sql.SQLException;
import java.util.List;

/** Gestiona las operaciones de los huertos. */
public class HuertosService {

    private final HuertosRepository repositorio;

    // Recibe el repositorio que utilizará el servicio
    public HuertosService(HuertosRepository repositorio) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio es obligatorio.");
        }

        this.repositorio = repositorio;
    }

    // Comprueba los datos y guarda un huerto
    public int crearHuerto(Huerto huerto) throws SQLException {

        if (huerto == null) {
            throw new IllegalArgumentException("El huerto no puede ser nulo.");
        }

        if (huerto.obtenerIdUsuario() <= 0) {
            throw new IllegalArgumentException("El usuario no es válido.");
        }

        if (huerto.obtenerNombre() == null
                || huerto.obtenerNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        if (huerto.obtenerUbicacion() == null
                || huerto.obtenerUbicacion().trim().isEmpty()) {
            throw new IllegalArgumentException("La ubicación es obligatoria.");
        }

        if (huerto.obtenerTipoUbicacion() == null
                || huerto.obtenerTipoUbicacion().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de ubicación es obligatorio.");
        }

        if (!Double.isFinite(huerto.obtenerAreaTotalM2())
                || huerto.obtenerAreaTotalM2() <= 0) {
            throw new IllegalArgumentException("El área debe ser mayor que cero.");
        }

        if (huerto.obtenerFechaAlta() == null) {
            throw new IllegalArgumentException("La fecha de alta es obligatoria.");
        }

        // Una vez comprobados los datos, los enviamos a SQLite
        return repositorio.crear(huerto);
    }

    // Busca un huerto por su identificador
    public Huerto buscarHuerto(int id) throws SQLException {
        return repositorio.buscarPorId(id);
    }

    // Devuelve los huertos que pertenecen a un usuario
    public List<Huerto> listarHuertos(int idUsuario) throws SQLException {
        return repositorio.listarPorUsuario(idUsuario);
    }
}
