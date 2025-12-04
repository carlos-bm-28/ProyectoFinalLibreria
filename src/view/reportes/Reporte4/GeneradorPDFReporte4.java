package view.reportes.Reporte4;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import util.Conexion;
import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneradorPDFReporte4 {

    private static final BaseColor AZUL_CRISOL = new BaseColor(0, 38, 100);
    private static final BaseColor AMARILLO_CRISOL = new BaseColor(255, 193, 7);
    private static final BaseColor AMARILLO_LOGO = new BaseColor(255, 193, 7);

    private static final Font F_TITULO = new Font(Font.FontFamily.HELVETICA, 26, Font.BOLD, AZUL_CRISOL);
    private static final Font F_SUBTITULO = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, AZUL_CRISOL);
    private static final Font F_SUB = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.WHITE);
    private static final Font F_NORMAL = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
    private static final Font F_NEGRITA = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);

    public static void generarReporteMaxMinMensual() {
        // CREAR CARPETA REPORTES
        new File("reportes").mkdirs();

        // RUTA FINAL EN CARPETA REPORTES
        String archivo = "reportes/Reporte4_Prestamos_Mensual_" + 
                         new SimpleDateFormat("dd-MM-yyyy_HHmm").format(new Date()) + ".pdf";

        Document doc = new Document(PageSize.A4, 36, 36, 70, 60);
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(archivo));
            doc.open();

            // ===================== ENCABEZADO CRISOL OFICIAL =====================
            PdfPTable header = new PdfPTable(2);
            header.setWidthPercentage(100);
            header.setWidths(new float[]{20, 80});

            // LOGO LOCAL (NUNCA FALLA)
            Image logo;
            try {
                logo = Image.getInstance(
                    GeneradorPDFReporte4.class.getClassLoader().getResource("recursos/imagenes/logo.png")
                );
                logo.scaleAbsolute(100, 100);
            } catch (Exception e) {
                // Fallback si no encuentra el logo
                Paragraph fallback = new Paragraph("CRISOL", new Font(Font.FontFamily.HELVETICA, 36, Font.BOLD));
                PdfPCell cellFallback = new PdfPCell(fallback);
                cellFallback.setBackgroundColor(AMARILLO_LOGO);
                cellFallback.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellFallback.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellFallback.setFixedHeight(100f);
                header.addCell(cellFallback);
                e.printStackTrace();
                logo = null;
            }

            if (logo != null) {
                PdfPCell cellLogo = new PdfPCell(logo);
                cellLogo.setBackgroundColor(AMARILLO_LOGO);
                cellLogo.setBorder(Rectangle.NO_BORDER);
                cellLogo.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellLogo.setVerticalAlignment(Element.ALIGN_MIDDLE);
                header.addCell(cellLogo);
            }

            Paragraph txt = new Paragraph();
            txt.add(new Phrase("LIBRERÍA CRISOL\n", F_TITULO));
            txt.add(new Phrase("LIBROS Y MÁS\n\n", F_SUBTITULO));
            txt.add(new Phrase("Reporte Estadístico Mensual de Préstamos", F_NORMAL));

            PdfPCell cellTxt = new PdfPCell(txt);
            cellTxt.setBackgroundColor(AMARILLO_LOGO);
            cellTxt.setBorder(Rectangle.NO_BORDER);
            cellTxt.setPaddingLeft(20);
            cellTxt.setVerticalAlignment(Element.ALIGN_MIDDLE);
            header.addCell(cellTxt);

            doc.add(header);
            doc.add(Chunk.NEWLINE);

            // ===================== TÍTULO ROJO =====================
            Paragraph titulo = new Paragraph("REPORTE DE PRÉSTAMOS - MÁXIMO Y MÍNIMO MENSUAL", 
                new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, BaseColor.WHITE));
            titulo.setAlignment(Element.ALIGN_CENTER);

            PdfPTable tablaTitulo = new PdfPTable(1);
            tablaTitulo.setWidthPercentage(100);
            PdfPCell cellTitulo = new PdfPCell(titulo);
            cellTitulo.setBackgroundColor(new BaseColor(220, 53, 69));
            cellTitulo.setPadding(15);
            cellTitulo.setBorder(Rectangle.NO_BORDER);
            tablaTitulo.addCell(cellTitulo);
            doc.add(tablaTitulo);
            doc.add(Chunk.NEWLINE);

            // ===================== CONSULTA =====================
            String sql = """
                SELECT DATE_FORMAT(fecha_prestamo, '%Y-%m') AS mes, COUNT(*) AS total
                FROM prestamos
                GROUP BY DATE_FORMAT(fecha_prestamo, '%Y-%m')
                ORDER BY total DESC
                """;
            String mesMax = "", mesMin = "";
            int max = 0, min = Integer.MAX_VALUE, totalPrestamos = 0, meses = 0;

            try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    meses++;
                    int cant = rs.getInt("total");
                    totalPrestamos += cant;
                    String m = formatoMes(rs.getString("mes"));
                    if (cant > max) { max = cant; mesMax = m; }
                    if (cant < min) { min = cant; mesMin = m; }
                }
            }

            // ===================== TABLA ESTILO "LOS VENECOS" =====================
            PdfPTable tabla = new PdfPTable(3);
            tabla.setWidthPercentage(90);
            tabla.setWidths(new float[]{45, 30, 25});
            tabla.setSpacingBefore(20);

            // Cabecera con línea azul solo arriba
            PdfPCell c1 = new PdfPCell(new Phrase("DESCRIPCIÓN", F_SUB));
            PdfPCell c2 = new PdfPCell(new Phrase("MES", F_SUB));
            PdfPCell c3 = new PdfPCell(new Phrase("PRÉSTAMOS", F_SUB));
            c1.setBackgroundColor(AZUL_CRISOL);
            c2.setBackgroundColor(AZUL_CRISOL);
            c3.setBackgroundColor(AZUL_CRISOL);
            c1.setBorder(Rectangle.NO_BORDER);
            c2.setBorder(Rectangle.NO_BORDER);
            c3.setBorder(Rectangle.NO_BORDER);
            c1.setBorderWidthTop(4f);
            c2.setBorderWidthTop(4f);
            c3.setBorderWidthTop(4f);
            c1.setBorderColorTop(AZUL_CRISOL);
            c2.setBorderColorTop(AZUL_CRISOL);
            c3.setBorderColorTop(AZUL_CRISOL);
            c1.setPadding(12);
            c2.setPadding(12);
            c3.setPadding(12);
            tabla.addCell(c1); tabla.addCell(c2); tabla.addCell(c3);

            // Filas blancas
            agregarFilaBlanca(tabla, "MES CON MÁS PRÉSTAMOS", mesMax, String.valueOf(max));
            agregarFilaBlanca(tabla, "MES CON MENOS PRÉSTAMOS", mesMin, String.valueOf(min));
            double promedio = meses > 0 ? totalPrestamos / (double) meses : 0;
            agregarFilaBlanca(tabla, "PROMEDIO MENSUAL", "—", String.format("%.1f", promedio));

            doc.add(tabla);

            // ===================== PIE DE PÁGINA =====================
            doc.add(Chunk.NEWLINE);
            Paragraph pie = new Paragraph(
                "Reporte generado el " + new SimpleDateFormat("dd/MM/yyyy 'a las' HH:mm").format(new Date()) + 
                " - Sistema Biblioteca Crisol", 
                new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.DARK_GRAY)
            );
            pie.setAlignment(Element.ALIGN_CENTER);
            doc.add(pie);

            doc.close();

            JOptionPane.showMessageDialog(null, 
                "¡REPORTE #4 GENERADO CON ÉXITO!\n\nEstilo Crisol Profesional\n\nUbicación:\n" + new File(archivo).getAbsolutePath(),
                "Librería Crisol", JOptionPane.INFORMATION_MESSAGE);

            java.awt.Desktop.getDesktop().open(new File(archivo));

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al generar Reporte #4: " + e.getMessage());
        }
    }

    private static void agregarFilaBlanca(PdfPTable tabla, String desc, String mes, String valor) {
        PdfPCell c1 = new PdfPCell(new Phrase(desc, F_NORMAL));
        PdfPCell c2 = new PdfPCell(new Phrase(mes, F_NEGRITA));
        PdfPCell c3 = new PdfPCell(new Phrase(valor, F_NEGRITA));
        c1.setBorder(Rectangle.NO_BORDER);
        c2.setBorder(Rectangle.NO_BORDER);
        c3.setBorder(Rectangle.NO_BORDER);
        c1.setPadding(10);
        c2.setPadding(10);
        c3.setPadding(10);
        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
        c3.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(c1); tabla.addCell(c2); tabla.addCell(c3);
    }

    private static String formatoMes(String yyyyMM) {
        try {
            return new SimpleDateFormat("MMMM 'de' yyyy")
                .format(new SimpleDateFormat("yyyy-MM").parse(yyyyMM))
                .toUpperCase();
        } catch (Exception e) {
            return yyyyMM;
        }
    }
}