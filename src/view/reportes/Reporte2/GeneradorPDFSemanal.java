package view.reportes.Reporte2;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import util.Conexion;
import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GeneradorPDFSemanal {

    // COLORES OFICIALES CRISOL
    private static final BaseColor AMARILLO = new BaseColor(255, 193, 7);
    private static final BaseColor AZUL = new BaseColor(0, 38, 100);

    // FUENTES
    private static final Font F_TITULO = new Font(Font.FontFamily.HELVETICA, 28, Font.BOLD);
    private static final Font F_SUB = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font F_NORMAL = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
    private static final Font F_NEGRITA = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
    private static final Font F_CABECERA = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);

    public static void generarReporteRango(LocalDate desde, LocalDate hasta, String dniFiltro, String categoriaFiltro) {
        // CREAR CARPETA REPORTES
        new File("reportes").mkdirs();

        // NOMBRE DEL ARCHIVO
        String nombreArchivo = "reportes/Reporte_Prestamos_" +
                desde.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) +
                "_al_" + hasta.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".pdf";

        Document documento = new Document(PageSize.A4.rotate(), 40, 40, 80, 60);
        try {
            PdfWriter.getInstance(documento, new FileOutputStream(nombreArchivo));
            documento.open();

            // ==================== ENCABEZADO CRISOL OFICIAL ====================
            PdfPTable header = new PdfPTable(2);
            header.setWidthPercentage(100);
            header.setWidths(new int[]{20, 80});

            // LOGO LOCAL (100% SEGURO)
            Image logo;
            try {
                logo = Image.getInstance(
                    GeneradorPDFSemanal.class.getClassLoader().getResource("recursos/imagenes/logo.png")
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
            txt.add(new Phrase("Reporte Detallado de Préstamos", F_NORMAL));

            PdfPCell cellTxt = new PdfPCell(txt);
            cellTxt.setBackgroundColor(AMARILLO);
            cellTxt.setBorder(Rectangle.NO_BORDER);
            cellTxt.setPaddingLeft(20);
            cellTxt.setVerticalAlignment(Element.ALIGN_MIDDLE);
            header.addCell(cellTxt);

            documento.add(header);
            documento.add(Chunk.NEWLINE);

            // ==================== TÍTULO ROJO ====================
            Paragraph tituloRojo = new Paragraph("REPORTE DE PRÉSTAMOS", 
                new Font(Font.FontFamily.HELVETICA, 26, Font.BOLD, BaseColor.WHITE));
            tituloRojo.setAlignment(Element.ALIGN_CENTER);
            PdfPTable tablaTitulo = new PdfPTable(1);
            tablaTitulo.setWidthPercentage(100);
            PdfPCell cellTitulo = new PdfPCell(tituloRojo);
            cellTitulo.setBackgroundColor(AZUL);
            cellTitulo.setPadding(15);
            cellTitulo.setBorder(Rectangle.NO_BORDER);
            tablaTitulo.addCell(cellTitulo);
            documento.add(tablaTitulo);
            documento.add(Chunk.NEWLINE);

            // ==================== PERÍODO Y FILTROS ====================
            documento.add(new Paragraph("Período: " + 
                desde.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " al " + 
                hasta.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), F_NEGRITA));
            
            if (dniFiltro != null && !dniFiltro.trim().isEmpty())
                documento.add(new Paragraph("Filtro por DNI: " + dniFiltro, F_NORMAL));
            if (categoriaFiltro != null && !categoriaFiltro.trim().isEmpty())
                documento.add(new Paragraph("Categoría: " + categoriaFiltro, F_NORMAL));

            documento.add(new Paragraph("Generado el: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), F_NORMAL));
            documento.add(Chunk.NEWLINE);

            // ==================== TABLA CON LÍNEA AZUL ARRIBA ====================
            PdfPTable tabla = new PdfPTable(9);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{6f, 12f, 22f, 28f, 15f, 12f, 12f, 12f, 10f});

            String[] headers = {"ID", "DNI", "Socio", "Título del Libro", "Autor", "Categoría", "F. Préstamo", "F. Devolución", "Estado"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, F_CABECERA));
                cell.setBackgroundColor(AZUL);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setBorderWidthTop(4f);
                cell.setBorderColorTop(AZUL);
                cell.setPadding(10);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tabla.addCell(cell);
            }

            // ==================== DATOS ====================
            String sql = "SELECT p.id, s.dni, CONCAT(s.nombres, ' ', s.apellidos) AS socio, l.titulo, l.autor, l.categoria, " +
                         "p.fecha_prestamo, p.fecha_devolucion_prevista, p.estado " +
                         "FROM prestamos p " +
                         "JOIN socios s ON p.id_socio = s.id " +
                         "JOIN ejemplares e ON p.id_ejemplar = e.id " +
                         "JOIN libros l ON e.codigo_libro = l.codigo " +
                         "WHERE p.fecha_prestamo BETWEEN ? AND ? ";
            if (dniFiltro != null && !dniFiltro.trim().isEmpty()) sql += "AND s.dni = ? ";
            if (categoriaFiltro != null && !categoriaFiltro.trim().isEmpty()) sql += "AND l.categoria = ? ";
            sql += "ORDER BY p.fecha_prestamo DESC";

            PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql);
            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(hasta));
            int index = 3;
            if (dniFiltro != null && !dniFiltro.trim().isEmpty()) ps.setString(index++, dniFiltro);
            if (categoriaFiltro != null && !categoriaFiltro.trim().isEmpty()) ps.setString(index++, categoriaFiltro);

            ResultSet rs = ps.executeQuery();
            int total = 0;
            while (rs.next()) {
                total++;
                tabla.addCell(celda(rs.getString("id")));
                tabla.addCell(celda(rs.getString("dni")));
                tabla.addCell(celda(rs.getString("socio")));
                tabla.addCell(celda(rs.getString("titulo")));
                tabla.addCell(celda(rs.getString("autor")));
                tabla.addCell(celda(rs.getString("categoria")));
                tabla.addCell(celda(rs.getDate("fecha_prestamo").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
                tabla.addCell(celda(rs.getDate("fecha_devolucion_prevista").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
                tabla.addCell(celdaEstado(rs.getString("estado")));
            }

            if (total == 0) {
                PdfPCell vacio = new PdfPCell(new Phrase("No se encontraron préstamos en este período", F_NEGRITA));
                vacio.setColspan(9);
                vacio.setPadding(20);
                vacio.setHorizontalAlignment(Element.ALIGN_CENTER);
                tabla.addCell(vacio);
            }

            documento.add(tabla);

            // ==================== TOTAL ====================
            Paragraph totalParrafo = new Paragraph("\nTOTAL DE PRÉSTAMOS: " + total, 
                new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, AZUL));
            totalParrafo.setAlignment(Element.ALIGN_RIGHT);
            totalParrafo.setSpacingBefore(20);
            documento.add(totalParrafo);

            // ==================== PIE ====================
            Paragraph pie = new Paragraph("Sistema Biblioteca Crisol • Reporte generado automáticamente", 
                new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.DARK_GRAY));
            pie.setAlignment(Element.ALIGN_CENTER);
            pie.setSpacingBefore(30);
            documento.add(pie);

            documento.close();

            JOptionPane.showMessageDialog(null,
                "¡REPORTE GENERADO CON ÉXITO!\n\nTotal de préstamos: " + total + 
                "\n\nUbicación:\n" + new File(nombreArchivo).getAbsolutePath(),
                "Librería Crisol - Reporte de Préstamos", JOptionPane.INFORMATION_MESSAGE);

            java.awt.Desktop.getDesktop().open(new File(nombreArchivo));

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error generando el reporte: " + e.getMessage());
        }
    }

    private static PdfPCell celda(String texto) {
        PdfPCell c = new PdfPCell(new Phrase(texto, F_NORMAL));
        c.setBorder(Rectangle.NO_BORDER);
        c.setPadding(8);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        return c;
    }

private static PdfPCell celdaEstado(String estado) {
    Font fontEstado = switch (estado) {
        case "Activo"   -> new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, new BaseColor(0, 150, 0));   // Verde
        case "Devuelto" -> new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, new BaseColor(0, 100, 200)); // Azul
        case "Vencido"  -> new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.RED);              // Rojo
        default         -> F_NEGRITA;
    };

    PdfPCell c = new PdfPCell(new Phrase(estado, fontEstado));
    c.setBorder(Rectangle.NO_BORDER);
    c.setPadding(8);
    c.setHorizontalAlignment(Element.ALIGN_CENTER);
    return c;
}

}