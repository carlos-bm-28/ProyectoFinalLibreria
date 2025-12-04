package view.reportes.Reporte2;

import view.reportes.Reporte2.GeneradorPDFSemanal;
import util.TemaCrisol;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import util.Conexion;

public class ReporteSemanalForm extends JFrame {

    private JDateChooser dateDesde, dateHasta;
    private JComboBox<String> cmbSocio, cmbCategoria;

    public ReporteSemanalForm() {
        TemaCrisol.aplicarTemaGlobal();
        setTitle("REPORTE SEMANAL - FILTROS");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel titulo = new JLabel("REPORTE DE PRÉSTAMOS POR RANGO", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial Black", Font.BOLD, 36));

        // ← COLOR NEGRO
        titulo.setForeground(Color.BLACK);

        titulo.setBounds(0, 20, 900, 60);
        add(titulo);

        // Fecha Desde
        JLabel lblDesde = new JLabel("Desde:");
        lblDesde.setForeground(Color.BLACK);
        add(lblDesde).setBounds(100, 120, 150, 30);

        dateDesde = new JDateChooser();
        dateDesde.setDateFormatString("dd/MM/yyyy");
        dateDesde.setBounds(100, 150, 300, 40);
        add(dateDesde);

        // Fecha Hasta
        JLabel lblHasta = new JLabel("Hasta:");
        lblHasta.setForeground(Color.BLACK);
        add(lblHasta).setBounds(480, 120, 150, 30);

        dateHasta = new JDateChooser();
        dateHasta.setDateFormatString("dd/MM/yyyy");
        dateHasta.setBounds(480, 150, 300, 40);
        add(dateHasta);

        // SOCIO
        JLabel lblSocio = new JLabel("Socio (opcional):");
        lblSocio.setForeground(Color.BLACK);
        add(lblSocio).setBounds(100, 220, 200, 30);

        cmbSocio = new JComboBox<>();
        cmbSocio.addItem("Todos los socios");
        cargarSocios();
        cmbSocio.setBounds(100, 250, 300, 40);
        add(cmbSocio);

        // CATEGORÍA
        JLabel lblCategoria = new JLabel("Categoría (opcional):");
        lblCategoria.setForeground(Color.BLACK);
        add(lblCategoria).setBounds(480, 220, 200, 30);

        cmbCategoria = new JComboBox<>();
        cmbCategoria.addItem("Todas las categorías");
        cargarCategorias();
        cmbCategoria.setBounds(480, 250, 300, 40);
        add(cmbCategoria);

        // BOTÓN
        JButton btn = TemaCrisol.boton("GENERAR REPORTE EN PDF");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 28));
        btn.setBounds(150, 380, 600, 80);
        btn.setBackground(new Color(0, 150, 0));

        btn.addActionListener(e -> generarPDF());
        add(btn);

        setVisible(true);
    }

    private void cargarSocios() {
        try {
            String sql = "SELECT dni, CONCAT(dni, ' - ', nombres, ' ', apellidos) AS socio FROM socios ORDER BY apellidos, nombres";
            PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cmbSocio.addItem(rs.getString("socio"));
            }
            rs.close();
            ps.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error cargando socios");
        }
    }

    private void cargarCategorias() {
        try {
            String sql = "SELECT DISTINCT categoria FROM libros WHERE categoria IS NOT NULL ORDER BY categoria";
            PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cmbCategoria.addItem(rs.getString("categoria"));
            }
            rs.close();
            ps.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void generarPDF() {
        if (dateDesde.getDate() == null || dateHasta.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Selecciona ambas fechas", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate desde = dateDesde.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate hasta = dateHasta.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        String seleccionado = (String) cmbSocio.getSelectedItem();
        String dniFiltro = null;
        if (seleccionado != null && !seleccionado.equals("Todos los socios")) {
            dniFiltro = seleccionado.substring(0, 8);
        }

        String catSeleccionada = (String) cmbCategoria.getSelectedItem();
        String catFiltro = catSeleccionada.equals("Todas las categorías") ? null : catSeleccionada;

        GeneradorPDFSemanal.generarReporteRango(desde, hasta, dniFiltro, catFiltro);

        JOptionPane.showMessageDialog(this, "Reporte generado con éxito!\nPDF abierto automáticamente.", "ÉXITO", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        new ReporteSemanalForm();
    }
}
