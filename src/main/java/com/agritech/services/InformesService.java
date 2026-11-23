/** Contiene la lógica de generación de informes. */
package com.agritech.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

/** Genera un documento informativo estático; no consulta SQLite. */
public class InformesService {

    public void generar(File archivo) throws IOException {
        List<String> lineas = new ArrayList<String>();
        lineas.add("AgriTech - Informe de la plataforma");
        lineas.add("");
        lineas.add("Smart Farming IoT");
        lineas.add("AgriTech es una plataforma de demostracion para el seguimiento y cuidado");
        lineas.add("de cultivos domesticos.");
        lineas.add("");
        lineas.add("Funciones principales");
        lineas.add("- Consulta de medidas simuladas de humedad del suelo.");
        lineas.add("- Gestion de plantas y configuracion de sus umbrales de humedad.");
        lineas.add("- Avisos cuando la humedad simulada baja del umbral configurado.");
        lineas.add("- Activacion y desactivacion del riego simulado desde la aplicacion.");
        lineas.add("");
        lineas.add("Simulacion");
        lineas.add("La aplicacion funciona sin sensores ni dispositivos de riego reales.");
        lineas.add("Las medidas, los avisos y el control del riego son demostrativos.");
        lineas.add("");
        lineas.add("Recorrido");
        lineas.add("1. Inicia sesion para abrir la ventana principal.");
        lineas.add("2. Configura las plantas y sus umbrales de humedad.");
        lineas.add("3. Consulta las medidas y avisos, y prueba el control de riego simulado.");

        try (PDDocument documento = new PDDocument()) {
            PDPage pagina = new PDPage(PDRectangle.A4);
            documento.addPage(pagina);
            try (PDPageContentStream contenido = new PDPageContentStream(documento, pagina)) {
                PDType1Font titulo = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font texto = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                float y = 780;
                for (int indice = 0; indice < lineas.size(); indice++) {
                    String linea = normalizar(lineas.get(indice));
                    contenido.beginText();
                    contenido.setFont(indice == 0 ? titulo : texto, indice == 0 ? 18 : 11);
                    contenido.newLineAtOffset(50, y);
                    contenido.showText(linea);
                    contenido.endText();
                    y -= indice == 0 ? 34 : 22;
                }
            }
            documento.save(archivo);
        }
    }

    private String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }
}
