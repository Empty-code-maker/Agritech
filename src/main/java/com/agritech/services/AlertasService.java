package com.agritech.services;

import com.agritech.models.Alerta;
import com.agritech.models.Medida;
import com.agritech.repositories.AlertasRepository;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

/** Evalúa el umbral de humedad y coordina el cierre y bloqueo de alertas. */
public class AlertasService {

    private static final Duration TIEMPO_BLOQUEO = Duration.ofSeconds(30);
    private final AlertasRepository repositorioAlertas;

    public AlertasService(AlertasRepository repositorioAlertas) {
        this.repositorioAlertas = repositorioAlertas;
    }

    public Optional<Alerta> evaluar(Medida medida, double humedadMinima) throws SQLException {
        int idPlanta = medida.obtenerIdPlanta();
        Optional<Alerta> alertaActiva = repositorioAlertas.obtenerActiva(
                idPlanta, Alerta.TIPO_HUMEDAD_BAJA);

        if (medida.obtenerHumedadSuelo() >= humedadMinima) {
            if (alertaActiva.isPresent()) {
                repositorioAlertas.cerrar(alertaActiva.get().obtenerId(),
                        medida.obtenerFechaHora(), Alerta.CIERRE_RECUPERADA);
            }
            return Optional.empty();
        }

        if (alertaActiva.isPresent()) {
            return alertaActiva;
        }

        Optional<LocalDateTime> ultimoCierre = repositorioAlertas.obtenerUltimoCierre(
                idPlanta, Alerta.TIPO_HUMEDAD_BAJA);
        if (ultimoCierre.isPresent()
                && Duration.between(ultimoCierre.get(), medida.obtenerFechaHora())
                .compareTo(TIEMPO_BLOQUEO) < 0) {
            return Optional.empty();
        }

        Alerta nuevaAlerta = new Alerta(0, idPlanta, medida.obtenerId(),
                Alerta.TIPO_HUMEDAD_BAJA, medida.obtenerHumedadSuelo(), humedadMinima,
                medida.obtenerFechaHora(), null, null);
        return Optional.of(repositorioAlertas.crear(nuevaAlerta));
    }

    public Optional<Alerta> obtenerActiva(int idPlanta) throws SQLException {
        return repositorioAlertas.obtenerActiva(idPlanta, Alerta.TIPO_HUMEDAD_BAJA);
    }

    public void descartar(long idAlerta) throws SQLException {
        repositorioAlertas.cerrar(idAlerta, LocalDateTime.now(), Alerta.CIERRE_DESCARTADA);
    }
}
