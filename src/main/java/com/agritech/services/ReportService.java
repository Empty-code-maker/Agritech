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

public class ReportService {

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
}
