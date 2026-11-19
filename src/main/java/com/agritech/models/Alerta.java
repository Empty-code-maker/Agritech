package com.agritech.models;

import java.time.LocalDateTime;

/** Incidencia de humedad con los valores que la originaron y su resolución. */
public final class Alerta {

    public static final String TIPO_HUMEDAD_BAJA = "HUMEDAD_BAJA";
    public static final String CIERRE_RECUPERADA = "RECUPERADA";
    public static final String CIERRE_DESCARTADA = "DESCARTADA";

    private final long id;
    private final int idPlanta;
    private final long idMedida;
    private final String tipo;
    private final double valorObservado;
    private final double valorUmbral;
    private final LocalDateTime fechaHoraCreacion;
    private final LocalDateTime fechaHoraCierre;
    private final String motivoCierre;

    public Alerta(long id, int idPlanta, long idMedida,
                 String tipo, double valorObservado, double valorUmbral,
                 LocalDateTime fechaHoraCreacion, LocalDateTime fechaHoraCierre,
                 String motivoCierre) {
        this.id = id;
        this.idPlanta = idPlanta;
        this.idMedida = idMedida;
        this.tipo = tipo;
        this.valorObservado = valorObservado;
        this.valorUmbral = valorUmbral;
        this.fechaHoraCreacion = fechaHoraCreacion;
        this.fechaHoraCierre = fechaHoraCierre;
        this.motivoCierre = motivoCierre;
    }

    public long obtenerId() {
        return id;
    }

    public int obtenerIdPlanta() {
        return idPlanta;
    }

    public long obtenerIdMedida() {
        return idMedida;
    }

    public String obtenerTipo() {
        return tipo;
    }

    public double obtenerValorObservado() {
        return valorObservado;
    }

    public double obtenerValorUmbral() {
        return valorUmbral;
    }

    public LocalDateTime obtenerFechaHoraCreacion() {
        return fechaHoraCreacion;
    }

    public LocalDateTime obtenerFechaHoraCierre() {
        return fechaHoraCierre;
    }

    public String obtenerMotivoCierre() {
        return motivoCierre;
    }
}