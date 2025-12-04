package view.reportes.Reporte6;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import util.Conexion;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class GeneradorPDFReporte6 {

    private static final BaseColor AMARILLO_LOGO = new BaseColor(255, 193, 7);
    private static final BaseColor AZUL_CRISOL = new BaseColor(0, 38, 100);

    private static final Font F_GRANDE = new Font(Font.FontFamily.HELVETICA, 28, Font.BOLD, AZUL_CRISOL);
    private static final Font F_TITULO = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.WHITE);
    private static final Font F_CABECERA = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.WHITE);
    private static final Font F_NORMAL = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, AZUL_CRISOL);
    private static final Font F_NEGRITA = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);

    private static void agregarEncabezado(Document doc) throws Exception {

        PdfPTable header = new PdfPTable(2);           // 2 columnas
        header.setWidths(new int[]{20, 80});           // proporciones
        header.setWidthPercentage(100);

        Image logo = Image.getInstance(new URL("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQsnpbe1RXpr9E8bzzdiy-i4aKZEiDs2axmpA&s"));
        logo.scaleAbsolute(100, 100);
        PdfPCell cellLogo = new PdfPCell(logo);
        cellLogo.setBorder(Rectangle.NO_BORDER);
        cellLogo.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellLogo.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(cellLogo);

        Paragraph txt = new Paragraph();
        txt.add(new Phrase("LIBRERÍA CRISOL\n", F_GRANDE));
        txt.add(new Phrase("LIBROS Y MÁS\n\n", new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD)));
        txt.add(new Phrase("Sistema de Gestión de Biblioteca", F_NORMAL));
        PdfPCell cellTxt = new PdfPCell(txt);
        cellTxt.setBackgroundColor(AMARILLO_LOGO);
        cellTxt.setBorder(Rectangle.NO_BORDER);
        cellTxt.setPaddingLeft(20);
        cellTxt.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.addCell(cellTxt);

        doc.add(header);
        doc.add(Chunk.NEWLINE);
    }

    private static void agregarTituloRojo(Document doc, String titulo) throws Exception {
        Paragraph p = new Paragraph(titulo, F_TITULO);
        p.setAlignment(Element.ALIGN_CENTER);
        PdfPTable tablaTitulo = new PdfPTable(1);
        tablaTitulo.setWidthPercentage(95);
        PdfPCell cell = new PdfPCell(p);
        cell.setBackgroundColor(AZUL_CRISOL);
        cell.setPadding(15);
        cell.setBorder(Rectangle.NO_BORDER);
        tablaTitulo.addCell(cell);
        doc.add(tablaTitulo);
        doc.add(Chunk.NEWLINE);
    }

    private static PdfPTable crearTablaAzul(int columnas, int[] anchos, String[] cabeceras) {
        PdfPTable tabla = new PdfPTable(columnas);
        tabla.setWidthPercentage(95);
        try {
            tabla.setWidths(anchos);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        for (String cabecera : cabeceras) {
            PdfPCell cell = new PdfPCell(new Phrase(cabecera, F_CABECERA));
            cell.setBackgroundColor(AZUL_CRISOL);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setBorderWidthTop(4f);
            cell.setBorderColorTop(AZUL_CRISOL);
            cell.setPadding(12);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
        }
        return tabla;
    }

    private static void agregarFila(PdfPTable tabla, String... datos) {
        for (String dato : datos) {
            PdfPCell cell = new PdfPCell(new Phrase(dato != null ? dato : "-", F_NORMAL));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setPadding(10);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
        }
    }

    // ==================== LIBROS ====================
public static void generarPDFLibros() {
    String archivo = "Reporte6_PDFLibros_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HHmm")) + ".pdf";
    String ruta = "reportes/" + archivo;  // Ajusta la ruta

    Document doc = new Document(PageSize.A4.rotate(), 30, 30, 80, 60);

    try {
        PdfWriter.getInstance(doc, new FileOutputStream(ruta));  // Usa la ruta completa
        doc.open();
        agregarEncabezado(doc);
        agregarTituloRojo(doc, "REPORTE #6 - LIBROS REGISTRADOS");

        PdfPTable tabla = crearTablaAzul(7, new int[]{10, 30, 18, 15, 8, 12, 10},
                new String[]{"CÓDIGO", "TÍTULO", "AUTOR", "EDITORIAL", "AÑO", "CATEGORÍA", "STOCK"});

        String sql = """
            SELECT l.codigo, l.titulo, l.autor, l.editorial, l.anio, l.categoria,
                   (SELECT COUNT(*) FROM ejemplares e WHERE e.codigo_libro = l.codigo) AS stock
            FROM libros l ORDER BY l.titulo
            """;

        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            int total = 0;
            while (rs.next()) {
                total++;
                agregarFila(tabla,
                        rs.getString("codigo"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("editorial"),
                        rs.getString("anio"),
                        rs.getString("categoria"),
                        String.valueOf(rs.getInt("stock"))
                );
            }
            doc.add(tabla);

            Paragraph pie = new Paragraph("TOTAL DE LIBROS REGISTRADOS: " + total, F_NEGRITA);
            pie.setAlignment(Element.ALIGN_RIGHT);
            pie.setSpacingBefore(20);
            doc.add(pie);
        }

        doc.close();
        abrirPDF(ruta, "Libros");

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al generar PDF de Libros");
    }
}

    // ==================== SOCIOS ====================
public static void generarPDFSocios() {
    String archivo = "Reporte6_PDFSocios" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HHmm")) + ".pdf";
    String ruta = "reportes/" + archivo;  // Ajusta la ruta

    Document doc = new Document(PageSize.A4.rotate(), 30, 30, 80, 60);

    try {
        PdfWriter.getInstance(doc, new FileOutputStream(ruta));  // Usa la ruta completa
        doc.open();
        agregarEncabezado(doc);
        agregarTituloRojo(doc, "REPORTE #6 - SOCIOS REGISTRADOS");

        PdfPTable tabla = crearTablaAzul(6, new int[]{12, 22, 22, 25, 12, 12},
                new String[]{"DNI", "NOMBRES", "APELLIDOS", "DIRECCIÓN", "TELÉFONO", "ESTADO"});

        String sql = "SELECT dni, nombres, apellidos, direccion, telefono, estado FROM socios ORDER BY apellidos";
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            int total = 0;
            while (rs.next()) {
                total++;
                agregarFila(tabla,
                        rs.getString("dni"),
                        rs.getString("nombres"),
                        rs.getString("apellidos"),
                        rs.getString("direccion"),
                        rs.getString("telefono"),
                        rs.getString("estado")
                );
            }
            doc.add(tabla);

            Paragraph pie = new Paragraph("TOTAL DE SOCIOS: " + total, F_NEGRITA);
            pie.setAlignment(Element.ALIGN_RIGHT);
            pie.setSpacingBefore(20);
            doc.add(pie);
        }

        doc.close();
        abrirPDF(ruta, "Socios");

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    // ==================== TRABAJADORES ====================
public static void generarPDFTrabajadores() {
    String archivo = "Reporte6_PDFTtrabajadores" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HHmm")) + ".pdf";
    String ruta = "reportes/" + archivo;  // Ajusta la ruta

    Document doc = new Document(PageSize.A4, 40, 40, 80, 60);

    try {
        PdfWriter.getInstance(doc, new FileOutputStream(ruta));  // Usa la ruta completa
        doc.open();
        agregarEncabezado(doc);
        agregarTituloRojo(doc, "REPORTE #6 - TRABAJADORES DEL SISTEMA");

        PdfPTable tabla = crearTablaAzul(3, new int[]{25, 45, 30},
                new String[]{"USUARIO", "NOMBRE COMPLETO", "PERFIL"});

        String sql = "SELECT username, nombre_completo, perfil FROM usuarios WHERE perfil IN ('Administrador', 'Empleado')";
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            int total = 0;
            while (rs.next()) {
                total++;
                agregarFila(tabla,
                        rs.getString("username"),
                        rs.getString("nombre_completo"),
                        rs.getString("perfil")
                );
            }
            doc.add(tabla);

            Paragraph pie = new Paragraph("TOTAL DE TRABAJADORES: " + total, F_NEGRITA);
            pie.setAlignment(Element.ALIGN_RIGHT);
            pie.setSpacingBefore(20);
            doc.add(pie);
        }

        doc.close();
        abrirPDF(ruta, "Trabajadores");

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    private static void abrirPDF(String ruta, String tipo) {
        JOptionPane.showMessageDialog(null,
                "PDF de " + tipo + " generado exitosamente!\n\nUbicación:\n" + new File(ruta).getAbsolutePath(),
                "Librería Crisol - Reporte #6", JOptionPane.INFORMATION_MESSAGE);
        try {
            java.awt.Desktop.getDesktop().open(new File(ruta));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
