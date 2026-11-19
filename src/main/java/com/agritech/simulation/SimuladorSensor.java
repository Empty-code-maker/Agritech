package com.agritech.simulation;

import com.agritech.models.Medida;
import com.agritech.models.Planta;

import java.time.LocalDateTime;

/** Genera un ciclo repetible de humedad relativa al umbral de la planta. */
public class SimuladorSensor {

    private static final double[] DESPLAZAMIENTOS = {
            10, 5, 2, -1, -5, -10, -5, 0, 2, 6,
            8, 4, 1, -2, -4, -8, -3, 1, 4, 8
    };

    public Medida generarMedida(Planta planta, int paso) {
        double umbral = planta.obtenerUmbral().obtenerHumedadSueloMinima();
        double desplazamiento = DESPLAZAMIENTOS[Math.floorMod(paso, DESPLAZAMIENTOS.length)];
        double humedad = Math.max(0, Math.min(100, umbral + desplazamiento));
        humedad = Math.round(humedad * 10.0) / 10.0;
        return new Medida(0, planta.obtenerId(), humedad, LocalDateTime.now());
    }
}
