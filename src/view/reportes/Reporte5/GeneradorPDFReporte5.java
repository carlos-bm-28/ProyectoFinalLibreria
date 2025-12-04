package view.reportes.Reporte5;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import util.Conexion;
import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneradorPDFReporte5 {

    // COLORES OFICIALES CRISOL
    private static final BaseColor AMARILLO = new BaseColor(255, 193, 7);
    private static final BaseColor AZUL = new BaseColor(0, 38, 100);
    private static final BaseColor GRIS_CLARO = new BaseColor(245, 245, 245);

    // FUENTES
    private static final Font F_TITULO = new Font(Font.FontFamily.HELVETICA, 28, Font.BOLD);
    private static final Font F_SUB = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font F_NORMAL = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
    private static final Font F_NEGRITA = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
    private static final Font F_CABECERA = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.WHITE);

    public static void generarReporteEliminados() {
        // CREAR CARPETA REPORTES
        new File("reportes").mkdirs();

        // RUTA FINAL EN CARPETA REPORTES
        String archivo = "reportes/Reporte5_Socios_Inactivos_" +
                new SimpleDateFormat("dd-MM-yyyy_HHmm").format(new Date()) + ".pdf";

        Document doc = new Document(PageSize.A4.rotate(), 40, 40, 80, 60);
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(archivo));
            doc.open();

            // ENCABEZADO CRISOL OFICIAL
            PdfPTable header = new PdfPTable(2);
            header.setWidthPercentage(100);
            header.setWidths(new int[]{20, 80});

            // LOGO LOCAL (NUNCA FALLA)
            Image logo;
            try {
                logo = Image.getInstance(
                    GeneradorPDFReporte5.class.getClassLoader().getResource("recursos/imagenes/logo.png")
                );
                logo.scaleAbsolute(100, 100);
            } catch (Exception e) {
                Paragraph fallback = new Paragraph("CRISOL", new Font(Font.FontFamily.HELVETICA, 40, Font.BOLD));
                PdfPCell cellFallback = new PdfPCell(fallback);
                cellFallback.setBackgroundColor(AMARILLO);
                cellFallback.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellFallback.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellFallback.setFixedHeight(100f);
                header.addCell(cellFallback);
                e.printStackTrace();
                logo = null;
            }

            if (logo != null) {
                PdfPCell cellLogo = new PdfPCell(logo);
                cellLogo.setBackgroundColor(AMARILLO);
                cellLogo.setBorder(Rectangle.NO_BORDER);
                cellLogo.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellLogo.setVerticalAlignment(Element.ALIGN_MIDDLE);
                header.addCell(cellLogo);
            }

            Paragraph txt = new Paragraph();
            txt.add(new Phrase("LIBRERÍA CRISOL\n", F_TITULO));
            txt.add(new Phrase("LIBROS Y MÁS\n\n", F_SUB));
            txt.add(new Phrase("Reporte de Socios Inactivos", F_NORMAL));

            PdfPCell cellTxt = new PdfPCell(txt);
            cellTxt.setBackgroundColor(AMARILLO);
            cellTxt.setBorder(Rectangle.NO_BORDER);
            cellTxt.setPaddingLeft(20);
            cellTxt.setVerticalAlignment(Element.ALIGN_MIDDLE);
            header.addCell(cellTxt);

            doc.add(header);
            doc.add(Chunk.NEWLINE);

            // TÍTULO ROJO
            Paragraph tituloRojo = new Paragraph("REPORTE DE SOCIOS INACTIVOS", 
                new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.WHITE));
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

            // FECHA
            doc.add(new Paragraph("Generado el " +
                    new SimpleDateFormat("dd/MM/yyyy 'a las' HH:mm").format(new Date()), F_NEGRITA));
            doc.add(Chunk.NEWLINE);

            // TABLA DE SOCIOS INACTIVOS
            int total = crearTablaSocios(doc);

            // NOTA
            Paragraph nota = new Paragraph(
                "Nota: Los socios se consideran inactivos cuando no han realizado préstamos en los últimos 12 meses.",
                new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.DARK_GRAY));
            nota.setSpacingBefore(20);
            doc.add(nota);

            // TOTAL DESTACADO
            Paragraph totalParrafo = new Paragraph("TOTAL SOCIOS INACTIVOS: " + total,
                    new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, AZUL));
            totalParrafo.setAlignment(Element.ALIGN_RIGHT);
            totalParrafo.setSpacingBefore(20);
            doc.add(totalParrafo);

            // PIE
            Paragraph pie = new Paragraph(
                "Sistema Biblioteca Crisol • Reporte confidencial • " + 
                new SimpleDateFormat("yyyy").format(new Date()),
                new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.DARK_GRAY)
            );
            pie.setAlignment(Element.ALIGN_CENTER);
            pie.setSpacingBefore(30);
            doc.add(pie);

            doc.close();

            JOptionPane.showMessageDialog(null,
                "¡REPORTE #5 GENERADO CON ÉXITO!\n\nTotal socios inactivos: " + total + 
                "\n\nUbicación:\n" + new File(archivo).getAbsolutePath(),
                "Librería Crisol - Reporte #5", JOptionPane.INFORMATION_MESSAGE);

            java.awt.Desktop.getDesktop().open(new File(archivo));

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al generar Reporte #5: " + e.getMessage());
        }
    }

    private static int crearTablaSocios(Document doc) throws Exception {
        String sql = """
            SELECT dni, nombres, apellidos, telefono
            FROM socios
            WHERE estado = 'Inactivo'
            ORDER BY apellidos, nombres
            """;

        int contador = 0;
        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(95);
        tabla.setWidths(new float[]{8, 20, 30, 30, 12});

        // CABECERA CON LÍNEA AZUL SOLO ARRIBA
        String[] headers = {"N°", "DNI", "NOMBRES", "APELLIDOS", "TELÉFONO"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, F_CABECERA));
            cell.setBackgroundColor(AZUL);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setBorderWidthTop(4f);
            cell.setBorderColorTop(AZUL);
            cell.setPadding(12);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
        }

        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int n = 1;
            while (rs.next()) {
                BaseColor fondo = (n % 2 == 0) ? GRIS_CLARO : BaseColor.WHITE;

                tabla.addCell(celda(String.valueOf(n++), fondo, Element.ALIGN_CENTER));
                tabla.addCell(celda(rs.getString("dni"), fondo, Element.ALIGN_CENTER));
                tabla.addCell(celda(rs.getString("nombres"), fondo, Element.ALIGN_LEFT));
                tabla.addCell(celda(rs.getString("apellidos"), fondo, Element.ALIGN_LEFT));
                tabla.addCell(celda(rs.getString("telefono") != null ? rs.getString("telefono") : "-", fondo, Element.ALIGN_CENTER));

                contador++;
            }

            if (contador == 0) {
                PdfPCell vacio = new PdfPCell(new Phrase("No hay socios inactivos registrados", F_NEGRITA));
                vacio.setColspan(5);
                vacio.setPadding(20);
                vacio.setHorizontalAlignment(Element.ALIGN_CENTER);
                vacio.setBackgroundColor(new BaseColor(250, 250, 250));
                tabla.addCell(vacio);
            }
        }

        doc.add(tabla);
        return contador;
    }

    private static PdfPCell celda(String texto, BaseColor fondo, int align) {
        PdfPCell c = new PdfPCell(new Phrase(texto, F_NORMAL));
        c.setBackgroundColor(fondo);
        c.setPadding(10);
        c.setHorizontalAlignment(align);
        c.setBorder(Rectangle.NO_BORDER);
        return c;
    }
}