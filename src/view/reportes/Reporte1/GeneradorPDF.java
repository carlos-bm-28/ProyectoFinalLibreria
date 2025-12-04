package view.reportes.Reporte1;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;

public class GeneradorPDF {

    // COLORES CRISOL
    private static final BaseColor AMARILLO = new BaseColor(255, 193, 7);
    private static final BaseColor AZUL = new BaseColor(0, 38, 100);

    // FUENTES
    private static final Font F_TITULO = new Font(Font.FontFamily.HELVETICA, 28, Font.BOLD,AZUL);
    private static final Font F_SUB = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font F_NORMAL = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
    private static final Font F_NEGRITA = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
    private static final Font F_CABECERA = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.WHITE);

    public static void generarTicketPrestamo(
            String dniSocio, String nombreSocio,
            String tituloLibro, String autorLibro, String codigoEjemplar,
            LocalDate fechaPrestamo, LocalDate fechaDevolucion,
            String empleado) {

        Document doc = new Document(PageSize.A4, 40, 40, 80, 60);
        try {
            // CREAR CARPETA REPORTES
            new File("reportes").mkdirs();

            // RUTA DEL PDF
            String nombreArchivo = "prestamo_" + dniSocio + "_"
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
            String ruta = "reportes/" + nombreArchivo;

            PdfWriter.getInstance(doc, new FileOutputStream(ruta));
            doc.open();

            // ==================== ENCABEZADO NARANJA + LOGO ====================
            // ==================== ENCABEZADO NARANJA + LOGO LOCAL ====================
            PdfPTable header = new PdfPTable(2);
            header.setWidthPercentage(100);
            header.setWidths(new int[]{20, 80});

            PdfPCell cellLogo;
            try {
                // TU LOGO LOCAL → NUNCA FALLA
                Image logo = Image.getInstance(
                        GeneradorPDF.class.getClassLoader().getResource("recursos/imagenes/logo.png")
                );
                logo.scaleAbsolute(100, 100);
                cellLogo = new PdfPCell(logo);
            } catch (Exception ex) {
                // FALLBACK SI NO ENCUENTRA LA IMAGEN
                cellLogo = new PdfPCell(new Phrase("CRISOL", new Font(Font.FontFamily.HELVETICA, 40, Font.BOLD, BaseColor.BLACK)));
            }

            cellLogo.setBackgroundColor(AMARILLO);
            cellLogo.setBorder(Rectangle.NO_BORDER);
            cellLogo.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellLogo.setVerticalAlignment(Element.ALIGN_MIDDLE);
            header.addCell(cellLogo);

            Paragraph txt = new Paragraph();
            txt.add(new Phrase("LIBRERÍA CRISOL\n", F_TITULO));
            txt.add(new Phrase("LIBROS Y MÁS\n\n", F_SUB));
            txt.add(new Phrase("Comprobante de Préstamo de Libro", F_NORMAL));
            PdfPCell cellTxt = new PdfPCell(txt);
            cellTxt.setBackgroundColor(AMARILLO);
            cellTxt.setBorder(Rectangle.NO_BORDER);
            cellTxt.setPaddingLeft(20);
            cellTxt.setVerticalAlignment(Element.ALIGN_MIDDLE);
            header.addCell(cellTxt);

            doc.add(header);
            doc.add(Chunk.NEWLINE);

            // ==================== TÍTULO ROJO ====================
            Paragraph tituloRojo = new Paragraph("COMPROBANTE DE PRÉSTAMO", new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.WHITE));
            tituloRojo.setAlignment(Element.ALIGN_CENTER);
            PdfPTable tablaTitulo = new PdfPTable(1);
            tablaTitulo.setWidthPercentage(100);
            PdfPCell cellTitulo = new PdfPCell(tituloRojo);
            cellTitulo.setBackgroundColor(AZUL);
            cellTitulo.setPadding(15);
            cellTitulo.setBorder(Rectangle.NO_BORDER);
            tablaTitulo.addCell(cellTitulo);
            doc.add(tablaTitulo);
            doc.add(Chunk.NEWLINE);

            // ==================== INFO GENERAL ====================
            doc.add(new Paragraph("Fecha y hora de emisión: "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), F_NORMAL));
            doc.add(new Paragraph("Empleado responsable: " + empleado, F_NEGRITA));
            doc.add(Chunk.NEWLINE);

            // ==================== TABLA ESTILO LOS VENECOS ====================
            PdfPTable tabla = new PdfPTable(2);
            tabla.setWidthPercentage(90);
            tabla.setWidths(new int[]{35, 65});

            // Cabecera con línea azul solo arriba
            PdfPCell c1 = new PdfPCell(new Phrase("DATO", F_CABECERA));
            PdfPCell c2 = new PdfPCell(new Phrase("INFORMACIÓN", F_CABECERA));
            c1.setBackgroundColor(AZUL);
            c2.setBackgroundColor(AZUL);
            c1.setBorder(Rectangle.NO_BORDER);
            c2.setBorder(Rectangle.NO_BORDER);
            c1.setBorderWidthTop(4f);
            c2.setBorderWidthTop(4f);
            c1.setBorderColorTop(AZUL);
            c2.setBorderColorTop(AZUL);
            c1.setPadding(12);
            c2.setPadding(12);
            tabla.addCell(c1);
            tabla.addCell(c2);

            // Filas blancas
            agregarFila(tabla, "Socio", dniSocio + " - " + nombreSocio);
            agregarFila(tabla, "Título del libro", tituloLibro);
            agregarFila(tabla, "Autor", autorLibro);
            agregarFila(tabla, "Código del ejemplar", codigoEjemplar);
            agregarFila(tabla, "Fecha de préstamo", fechaPrestamo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            agregarFila(tabla, "Fecha de devolución", fechaDevolucion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            agregarFila(tabla, "Multa por retraso", "S/ 2.00 por día");

            doc.add(tabla);

            // ==================== PIE ====================
            Paragraph pie = new Paragraph(
                    "\nGracias por confiar en Librería Crisol.\nPor favor conserve este comprobante.",
                    new Font(Font.FontFamily.HELVETICA, 11, Font.ITALIC, BaseColor.DARK_GRAY)
            );
            pie.setAlignment(Element.ALIGN_CENTER);
            pie.setSpacingBefore(30);
            doc.add(pie);

            doc.close();

            JOptionPane.showMessageDialog(null,
                    "¡Comprobante generado con éxito!\nEstilo Crisol profesional\n\nUbicación: " + new File(ruta).getAbsolutePath(),
                    "Librería Crisol", JOptionPane.INFORMATION_MESSAGE);

            java.awt.Desktop.getDesktop().open(new File(ruta));

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al generar el comprobante: " + e.getMessage());
        }
    }

    private static void agregarFila(PdfPTable tabla, String campo, String valor) {
        PdfPCell c1 = new PdfPCell(new Phrase(campo, F_NEGRITA));
        PdfPCell c2 = new PdfPCell(new Phrase(valor, F_NORMAL));
        c1.setBorder(Rectangle.NO_BORDER);
        c2.setBorder(Rectangle.NO_BORDER);
        c1.setPadding(10);
        c2.setPadding(10);
        tabla.addCell(c1);
        tabla.addCell(c2);
    }
}
