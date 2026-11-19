/** Contiene la lógica de generación de informes. */
package com.agritech.services;

// Clases de PDFBox para crear el documento
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

//clases para trabajar con archivos
import java.io.File;
import java.io.IOException;

public class InformesService {

    // Recibe el archivo donde se guardará el PDF
    public void generar(File archivo) throws IOException {

        // Creamos un documento PDF vacío
        try (PDDocument documento = new PDDocument()) {

            // Creamos una página de tamaño A4
            PDPage pagina = new PDPage(PDRectangle.A4);

            // Añadimos la página al documento
            documento.addPage(pagina);

            // Abrimos la página para escribir el título
            try (PDPageContentStream contenido =
                         new PDPageContentStream(documento, pagina)) {

                // Elegimos la fuente y el tamaño del título
                PDType1Font fuente = new PDType1Font(
                        Standard14Fonts.FontName.HELVETICA_BOLD
                );

                contenido.beginText();

                contenido.setFont(fuente, 18);

                // Situamos el texto en la parte superior
                contenido.newLineAtOffset(50, 770);

                // Escribimos el título del informe
                contenido.showText("AgriTech - Informe de mediciones");

                contenido.endText();
            }

            // Guardamos documento en archivo
            documento.save(archivo);
        }
    }

    // Genera una guía con las funciones de AgriTech
    public void generarGuia(File archivo) throws IOException {

        // Creamos el documento y una página A4
        try (PDDocument documento = new PDDocument()) {

            PDPage pagina = new PDPage(PDRectangle.A4);
            documento.addPage(pagina);

            // Escribimos el contenido de la guía
            try (PDPageContentStream contenido =
                         new PDPageContentStream(documento, pagina)) {

                contenido.beginText();

                // Título principal
                PDType1Font titulo = new PDType1Font(
                        Standard14Fonts.FontName.HELVETICA_BOLD
                );

                contenido.setFont(titulo, 18);
                contenido.newLineAtOffset(50, 780);
                contenido.showText("AgriTech - Guia de funcionamiento");

                // Explicación de las funcionalidades
                PDType1Font texto = new PDType1Font(
                        Standard14Fonts.FontName.HELVETICA
                );

                contenido.setFont(texto, 12);
                contenido.setLeading(25);
                contenido.newLineAtOffset(0, -45);

                String[] pasos = {
                        "1. Inicio: acceso a las funciones de AgriTech.",
                        "2. Plantas: gestionar plantas y sus umbrales.",
                        "3. Riego: consultar y controlar el riego simulado.",
                        "4. Informes: generar y guardar documentos PDF.",
                        "5. Datos: almacenamiento local mediante SQLite.",
                        "6. API: futura integracion de mediciones externas."
                };

                // Escribimos cada paso en una línea diferente
                for (String paso : pasos) {
                    contenido.showText(paso);
                    contenido.newLine();
                }

                contenido.endText();
            }

            // Guardamos el documento
            documento.save(archivo);
        }
    }

}
