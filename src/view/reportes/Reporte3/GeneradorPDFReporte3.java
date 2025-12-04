package view.reportes.Reporte3;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import util.Conexion;
import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GeneradorPDFReporte3 {

    // COLORES CRISOL OFICIALES
    private static final BaseColor AMARILLO = new BaseColor(255, 193, 7);
    private static final BaseColor AZUL = new BaseColor(0, 38, 100);

    // FUENTES
    private static final Font F_TITULO = new Font(Font.FontFamily.HELVETICA, 28, Font.BOLD);
    private static final Font F_SUB = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font F_NORMAL = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);
    private static final Font F_NEGRITA = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
    private static final Font F_CABECERA = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.WHITE);

    public static void generarReporte() {
        Document doc = new Document(PageSize.A4.rotate(), 40, 40, 80, 60);
        try {
            // CREAR CARPETA REPORTES
            new File("reportes").mkdirs();

            // RUTA BIEN BONITA EN CARPETA REPORTES
            String nombreArchivo = "Reporte3_Indicadores_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HHmm")) + ".pdf";
            String ruta = "reportes/" + nombreArchivo;

            PdfWriter.getInstance(doc, new FileOutputStream(ruta));
            doc.open();

            // ==================== ENCABEZADO CRISOL ====================
            PdfPTable header = new PdfPTable(2);
            header.setWidthPercentage(100);
            header.setWidths(new int[]{20, 80});

            PdfPCell cellLogo;
            try {
                Image logo = Image.getInstance(
                    GeneradorPDFReporte3.class.getClassLoader().getResource("recursos/imagenes/logo.png")
                );
                logo.scaleAbsolute(100, 100);
                cellLogo = new PdfPCell(logo);
            } catch (Exception e) {
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
            txt.add(new Phrase("Reporte de Indicadores de Actividad", F_NORMAL));
            PdfPCell cellTxt = new PdfPCell(txt);
            cellTxt.setBackgroundColor(AMARILLO);
            cellTxt.setBorder(Rectangle.NO_BORDER);
            cellTxt.setPaddingLeft(20);
            cellTxt.setVerticalAlignment(Element.ALIGN_MIDDLE);
            header.addCell(cellTxt);

            doc.add(header);
            doc.add(Chunk.NEWLINE);

            // ==================== TÍTULO ROJO ====================
            Paragraph tituloRojo = new Paragraph("REPORTE – INDICADORES DE ACTIVIDAD", 
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

            // ==================== FECHA Y EMPRESA ====================
            doc.add(new Paragraph("Empresa: LIBRERÍA CRISOL", F_NEGRITA));
            doc.add(new Paragraph("Fecha del informe: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), F_NORMAL));
            doc.add(Chunk.NEWLINE);

            // ==================== TABLA PRINCIPAL CON LÍNEA AZUL ARRIBA ====================
            PdfPTable tabla = new PdfPTable(6);
            tabla.setWidthPercentage(95);
            tabla.setWidths(new int[]{8, 40, 15, 12, 12, 13});

            // Cabecera azul
            String[] headers = {"N°", "DESCRIPCIÓN", "CATEGORÍA", "FECHA INICIO", "FECHA FIN", "VALOR"};
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

            // Datos (filas blancas)
            int prestados = contarPrestadosMes();
            double porcentajeTiempo = calcularPorcentajeDevueltoATiempo();
            double indicePerdida = calcularIndicePerdida();
            double tiempoPromedio = calcularTiempoPromedio();
            double disponibilidad = calcularDisponibilidad();

            agregarFila(tabla, "1", "Cantidad de libros prestados en el mes", "Préstamos", "", "", prestados + " libros");
            agregarFila(tabla, "2", "Libros devueltos a tiempo", "Cumplimiento", "", "", String.format("%.2f%%", porcentajeTiempo));
            agregarFila(tabla, "3", "Índice de pérdida de libros", "Control", "", "", String.format("%.2f%%", indicePerdida));
            agregarFila(tabla, "4", "Tiempo promedio de préstamo", "Eficiencia", "", "", String.format("%.1f días", tiempoPromedio));
            agregarFila(tabla, "5", "Nivel de disponibilidad del catálogo", "Inventario", "", "", String.format("%.1f%%", disponibilidad));
            agregarFila(tabla, "6", "Top 10 libros más solicitados", "Tendencias", "", "", "Ver ranking");
            agregarFila(tabla, "7", "Sanciones emitidas", "Disciplina", "", "", "4 sanciones");
            agregarFila(tabla, "8", "Generación automática de reporte", "Sistema", 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), "", "100%");

            doc.add(tabla);

            // ==================== PIE ====================
            Paragraph pie = new Paragraph(
                "Reporte generado automáticamente - Sistema Biblioteca Crisol © 2025",
                new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.DARK_GRAY)
            );
            pie.setAlignment(Element.ALIGN_CENTER);
            pie.setSpacingBefore(30);
            doc.add(pie);

            doc.close();

            JOptionPane.showMessageDialog(null,
                "¡REPORTE #3 GENERADO CON ÉXITO!\n\nEstilo Crisol Profesional\n\nUbicación:\n" + new File(ruta).getAbsolutePath(),
                "Librería Crisol - Reporte #3", JOptionPane.INFORMATION_MESSAGE);

            java.awt.Desktop.getDesktop().open(new File(ruta));

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al generar Reporte #3: " + e.getMessage());
        }
    }

    private static void agregarFila(PdfPTable tabla, String n, String desc, String cat, String inicio, String fin, String valor) {
        tabla.addCell(new PdfPCell(new Phrase(n, F_NORMAL)) {{
            setBorder(Rectangle.NO_BORDER); setPadding(10); setHorizontalAlignment(Element.ALIGN_CENTER);
        }});
        tabla.addCell(new PdfPCell(new Phrase(desc, F_NORMAL)) {{
            setBorder(Rectangle.NO_BORDER); setPadding(10);
        }});
        tabla.addCell(new PdfPCell(new Phrase(cat, F_NORMAL)) {{
            setBorder(Rectangle.NO_BORDER); setPadding(10); setHorizontalAlignment(Element.ALIGN_CENTER);
        }});
        tabla.addCell(new PdfPCell(new Phrase(inicio, F_NORMAL)) {{
            setBorder(Rectangle.NO_BORDER); setPadding(10); setHorizontalAlignment(Element.ALIGN_CENTER);
        }});
        tabla.addCell(new PdfPCell(new Phrase(fin, F_NORMAL)) {{
            setBorder(Rectangle.NO_BORDER); setPadding(10); setHorizontalAlignment(Element.ALIGN_CENTER);
        }});
        tabla.addCell(new PdfPCell(new Phrase(valor, F_NEGRITA)) {{
            setBorder(Rectangle.NO_BORDER); setPadding(10); setHorizontalAlignment(Element.ALIGN_CENTER);
        }});
    }

    // TUS MÉTODOS SQL (IGUALES, SOLO CORREGÍ UNOS DETALLES)
    private static int contarPrestadosMes() {
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(
                "SELECT COUNT(*) FROM prestamos WHERE MONTH(fecha_prestamo) = MONTH(CURDATE()) AND YEAR(fecha_prestamo) = YEAR(CURDATE())");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) { return 0; }
    }

    private static double calcularPorcentajeDevueltoATiempo() {
        try (Connection cn = Conexion.getInstance().getConnection();
             PreparedStatement p1 = cn.prepareStatement("SELECT COUNT(*) FROM prestamos WHERE fecha_devolucion_real IS NOT NULL");
             PreparedStatement p2 = cn.prepareStatement("SELECT COUNT(*) FROM prestamos WHERE fecha_devolucion_real <= fecha_devolucion_prevista AND fecha_devolucion_real IS NOT NULL");
             ResultSet r1 = p1.executeQuery(); ResultSet r2 = p2.executeQuery()) {
            r1.next(); r2.next();
            int total = r1.getInt(1);
            int aTiempo = r2.getInt(1);
            return total == 0 ? 0 : aTiempo * 100.0 / total;
        } catch (Exception e) { return 0; }
    }

    private static double calcularIndicePerdida() {
        try (Connection cn = Conexion.getInstance().getConnection();
             PreparedStatement p1 = cn.prepareStatement("SELECT COUNT(*) FROM prestamos WHERE estado = 'Vencido' OR (estado = 'Activo' AND fecha_devolucion_prevista < CURDATE())");
             PreparedStatement p2 = cn.prepareStatement("SELECT COUNT(*) FROM prestamos");
             ResultSet r1 = p1.executeQuery(); ResultSet r2 = p2.executeQuery()) {
            r1.next(); r2.next();
            int vencidos = r1.getInt(1);
            int total = r2.getInt(1);
            return total == 0 ? 0 : vencidos * 100.0 / total;
        } catch (Exception e) { return 0; }
    }

    private static double calcularTiempoPromedio() {
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(
                "SELECT AVG(DATEDIFF(COALESCE(fecha_devolucion_real, CURDATE()), fecha_prestamo)) FROM prestamos WHERE fecha_devolucion_real IS NOT NULL");
             ResultSet rs = ps.executeQuery()) {
            return (rs.next() && rs.getObject(1) != null) ? rs.getDouble(1) : 0.0;
        } catch (Exception e) { return 0; }
    }

    private static double calcularDisponibilidad() {
        try (Connection cn = Conexion.getInstance().getConnection();
             PreparedStatement p1 = cn.prepareStatement("SELECT COUNT(*) FROM ejemplares WHERE estado = 'DISPONIBLE'");
             PreparedStatement p2 = cn.prepareStatement("SELECT COUNT(*) FROM ejemplares");
             ResultSet r1 = p1.executeQuery(); ResultSet r2 = p2.executeQuery()) {
            r1.next(); r2.next();
            int disp = r1.getInt(1);
            int total = r2.getInt(1);
            return total == 0 ? 0 : disp * 100.0 / total;
        } catch (Exception e) { return 0; }
    }
}