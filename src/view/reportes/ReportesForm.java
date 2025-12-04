package view.reportes;
import view.reportes.Reporte4.GeneradorPDFReporte4;
import view.reportes.Reporte3.GeneradorPDFReporte3;
import util.TemaCrisol;
import view.reportes.Reporte1.GeneradorPDF;
import view.reportes.Reporte2.ReporteSemanalForm;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import util.Conexion;   // ← asegúrate de que esta línea ya exista en tu proyecto
import util.Conexion;
import view.reportes.Reporte5.GeneradorPDFReporte5;
import view.reportes.Reporte6.Reporte6Form;

public class ReportesForm extends JFrame {

    public ReportesForm() {
        TemaCrisol.aplicarTemaGlobal();
        setTitle("REPORTES DEL SISTEMA - ADMINISTRADOR");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(TemaCrisol.AMARILLO);

        JLabel titulo = new JLabel("REPORTES DEL SISTEMA", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial Black", Font.BOLD, 50));
        titulo.setForeground(TemaCrisol.AZUL);
        titulo.setBounds(0, 30, 1200, 80);
        add(titulo);

        int y = 150;
        crearBoton("1. REPORTE DE PRÉSTAMO RECIÉN REALIZADO", y); y += 90;
        crearBoton("2. REPORTE SEMANAL (SELECCIONAR SEMANA)", y); y += 90;
        crearBoton("3. REPORTE GENERAL CON INDICADORES", y); y += 90;
        crearBoton("4. REPORTE MÁXIMOS Y MÍNIMOS", y); y += 90;
        crearBoton("5. REPORTE DE ELIMINACIÓN LÓGICA", y); y += 90;
        crearBoton("6. REPORTE DE INGRESOS / STOCK / UTILIDAD", y);

        setVisible(true);
    }

    private void generarReporteUltimoPrestamo() {
        try {
            String sql = "SELECT p.id, p.id_socio, p.id_ejemplar, p.fecha_prestamo, p.fecha_devolucion_prevista, "
                       + "s.dni, s.nombres, s.apellidos, "
                       + "e.codigo_libro, e.numero_ejemplar, "
                       + "l.titulo, l.autor "
                       + "FROM prestamos p "
                       + "JOIN socios s ON p.id_socio = s.id "
                       + "JOIN ejemplares e ON p.id_ejemplar = e.id "
                       + "JOIN libros l ON e.codigo_libro = l.codigo "
                       + "ORDER BY p.id DESC LIMIT 1"; // quité el WHERE estado='Activo' para que tome el último de todos

            PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                GeneradorPDF.generarTicketPrestamo(
                    rs.getString("dni"),
                    rs.getString("nombres") + " " + rs.getString("apellidos"),
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    rs.getString("codigo_libro") + "-" + rs.getInt("numero_ejemplar"),
                    rs.getDate("fecha_prestamo").toLocalDate(),
                    rs.getDate("fecha_devolucion_prevista").toLocalDate(),
                    "Administrador del Sistema"
                );

                JOptionPane.showMessageDialog(this,
                    "Reporte del último préstamo generado con éxito!\nPDF abierto automáticamente.",
                    "REPORTE #1 - ÉXITO", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No hay préstamos registrados aún.", "SIN DATOS", JOptionPane.WARNING_MESSAGE);
            }
            rs.close();
            ps.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

private void crearBoton(String texto, int y) {
    JButton btn = TemaCrisol.boton(texto);
    btn.setFont(new Font("Segoe UI", Font.BOLD, 26));
    btn.setBounds(100, y, 1000, 80);
    btn.setBackground(TemaCrisol.AZUL);
    btn.setForeground(Color.WHITE);

    btn.addActionListener(e -> {
        int num = Integer.parseInt(texto.substring(0, 1));

        switch (num) {
            case 1:
                generarReporteUltimoPrestamo();
                break;
            case 2:
                new ReporteSemanalForm();  // ← AQUÍ ABRE TU REPORTE SEMANAL ÉPICO
                break;
            case 3:
                GeneradorPDFReporte3.generarReporte();
                break;
            case 4:
                GeneradorPDFReporte4.generarReporteMaxMinMensual();
                break;
            case 5:
                GeneradorPDFReporte5.generarReporteEliminados();
                break;
            case 6:
                new Reporte6Form();
                break;
            default:
                JOptionPane.showMessageDialog(this, "Reporte en construcción, crack", "FUTURO ÉPICO", JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    });

    add(btn);
}



    public static void main(String[] args) {
        new ReportesForm();
    }
}