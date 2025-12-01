/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.proceso;

import dao.impl.*;
import model.*;
import util.TemaCrisol;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import util.Conexion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HistorialPrestamosForm extends JFrame {

    private final PrestamoDAOImpl prestamoDAO = new PrestamoDAOImpl();

    private final SocioDAOImpl socioDAO = new SocioDAOImpl();

    private final EjemplarDAOImpl ejemplarDAO = new EjemplarDAOImpl();

    private final LibroDAOImpl libroDAO = new LibroDAOImpl();

    private JTable tabla;

    private DefaultTableModel modelo;

    private JComboBox<String> cmbDni;

    private JTextField txtTitulo;

    private JComboBox<String> cmbEstado;

    private String filtroDniActual = ""; // para guardar el filtro del combo

    public HistorialPrestamosForm() {
        TemaCrisol.aplicarTemaGlobal();
        setTitle("HISTORIAL COMPLETO DE PRÉSTAMOS - ADMINISTRADOR");
        setSize(1600, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(TemaCrisol.AMARILLO);

        // TÍTULO
        JLabel titulo = new JLabel("HISTORIAL Y SEGUIMIENTO DE PRÉSTAMOS", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial Black", Font.BOLD, 40));
        titulo.setForeground(TemaCrisol.AZUL);
        titulo.setBounds(0, 10, 1350, 80);
        add(titulo);

        // === FILTROS ===
        JLabel lblFiltros = new JLabel("FILTROS:");
        lblFiltros.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblFiltros.setBounds(50, 80, 500, 40);
        add(lblFiltros);

        // COMBOBOX DE SOCIOS
        cmbDni = new JComboBox<>();
        cmbDni.addItem("Todos los socios");
        try {
            List<String> dnis = prestamoDAO.listarDnisConPrestamosActivos(); // ← este método ya lo tienes
            for (String dni : dnis) {
                Socio s = socioDAO.buscarPorDni(dni);
                if (s != null) {
                    cmbDni.addItem(dni + " - " + s.getNombres() + " " + s.getApellidos());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cmbDni.setBounds(50, 150, 255, 55);
        add(cmbDni);

        JLabel lblDni = new JLabel("Socio:");
        lblDni.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblDni.setBounds(50, 120, 260, 30);
        add(lblDni);

        txtTitulo = new JTextField();
        txtTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        txtTitulo.setBounds(340, 150, 340, 55);
        add(txtTitulo);

        JLabel lblTitulo = new JLabel("Título:");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setBounds(340, 120, 340, 30);
        add(lblTitulo);

        cmbEstado = new JComboBox<>(new String[]{"Todos", "Activo", "Devuelto", "Vencido"});
        cmbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        cmbEstado.setBounds(710, 150, 220, 55);
        add(cmbEstado);

        // BOTÓN BUSCAR QUE SÍ FUNCIONA
        JButton btnBuscar = TemaCrisol.boton("BUSCAR");
        btnBuscar.setBounds(960, 150, 200, 55);
        btnBuscar.addActionListener(e -> cargarTabla()); // ← DIRECTO, SIN NADA MÁS
        add(btnBuscar);

        // TABLA
        modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("DNI");
        modelo.addColumn("Socio");
        modelo.addColumn("Título");
        modelo.addColumn("F. Préstamo");
        modelo.addColumn("F. Devolución");
        modelo.addColumn("F. Real");
        modelo.addColumn("Estado");
        modelo.addColumn("Retraso");
        modelo.addColumn("Multa");

        tabla = new JTable(modelo);
        TemaCrisol.aplicarEstiloTabla(tabla);
        tabla.setRowHeight(38);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(25, 230, 1300, 590);
        add(scroll);

        // CARGA INICIAL
        cargarTabla();
        setVisible(true);
    }

    private void cargarTabla() {
        modelo.setRowCount(0);

        try {
            String seleccion = (String) cmbDni.getSelectedItem();
            String tituloFiltro = txtTitulo.getText().trim().toLowerCase();
            String estadoFiltro = (String) cmbEstado.getSelectedItem();

            String sql = """
            SELECT 
                p.id, p.id_socio, p.id_ejemplar, p.fecha_prestamo, 
                p.fecha_devolucion_prevista, p.fecha_devolucion_real, p.estado,
                s.dni, s.nombres, s.apellidos,
                l.codigo AS codigo_libro, l.titulo
            FROM prestamos p
            JOIN socios s ON p.id_socio = s.id
            JOIN ejemplares e ON p.id_ejemplar = e.id
            JOIN libros l ON e.codigo_libro = l.codigo
            """;

            // FILTRO POR SOCIO (si no es "Todos los socios")
            if (seleccion != null && !seleccion.equals("Todos los socios") && seleccion.contains(" - ")) {
                String dni = seleccion.split(" - ")[0];
                sql += " WHERE s.dni = '" + dni + "'";
            }

            sql += " ORDER BY p.fecha_prestamo DESC";

            try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

                boolean hayResultados = false;

                while (rs.next()) {
                    int idSocio = rs.getInt("id_socio");
                    String dni = rs.getString("dni");
                    String nombres = rs.getString("nombres") + " " + rs.getString("apellidos");
                    String tituloLibro = rs.getString("titulo");
                    String estadoBD = rs.getString("estado");
                    LocalDate fechaPrevista = rs.getDate("fecha_devolucion_prevista").toLocalDate();

                    // CÁLCULO DE ESTADO MOSTRADO Y RETRASO
                    String estadoMostrado = estadoBD;
                    long retraso = 0;
                    double multa = 0.0;

                    if ("Activo".equals(estadoBD)) {
                        if (LocalDate.now().isAfter(fechaPrevista)) {
                            estadoMostrado = "Vencido";
                            retraso = ChronoUnit.DAYS.between(fechaPrevista, LocalDate.now());
                            multa = retraso * 2.0;
                        }
                    }

                    // FILTROS ADICIONALES
                    if (!tituloFiltro.isEmpty() && !tituloLibro.toLowerCase().contains(tituloFiltro)) {
                        continue;
                    }
                    if (!"Todos".equals(estadoFiltro)) {
                        if ("Vencido".equals(estadoFiltro) && !"Vencido".equals(estadoMostrado)) {
                            continue;
                        }
                        if ("Activo".equals(estadoFiltro) && !"Activo".equals(estadoBD)) {
                            continue;
                        }
                        if ("Devuelto".equals(estadoFiltro) && !"Devuelto".equals(estadoBD)) {
                            continue;
                        }
                    }

                    hayResultados = true;
                    modelo.addRow(new Object[]{
                        rs.getInt("id"),
                        dni,
                        nombres,
                        tituloLibro,
                        rs.getDate("fecha_prestamo").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        fechaPrevista.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        rs.getDate("fecha_devolucion_real") != null
                        ? rs.getDate("fecha_devolucion_real").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        : "-",
                        estadoMostrado,
                        retraso > 0 ? retraso + " días" : "-",
                        multa > 0 ? "S/ " + String.format("%.2f", multa) : "-"
                    });
                }

                if (!hayResultados) {
                    modelo.addRow(new Object[]{"", "SIN RESULTADOS", "", "No hay préstamos con los filtros aplicados", "", "", "", "", "", ""});
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error cargando historial: " + ex.getMessage());
        }
    }

    private void mostrarMorosos() {

        StringBuilder sb = new StringBuilder("<html><h2 style='color:red'>REPORTE DE MOROSOSOS</h2><ul style='font-size:15px'>");

        int totalMorosos = 0;

        double deudaTotal = 0;

        for (int i = 0; i < modelo.getRowCount(); i++) {

            String estado = (String) modelo.getValueAt(i, 7);

            Object retrasoObj = modelo.getValueAt(i, 8);

            if ("Vencido".equals(estado) && retrasoObj != null && !"-".equals(retrasoObj.toString())) {

                long dias = (long) retrasoObj;

                String socio = (String) modelo.getValueAt(i, 2);

                String libro = (String) modelo.getValueAt(i, 3);

                double multa = dias * 2.0;

                sb.append("<li><b>").append(socio).append("</b> → ").append(libro)
                        .append(" <span style='color:red'><b>(").append(dias).append(" días atrasado - S/ ").append(String.format("%.2f", multa)).append(")</b></span></li>");

                totalMorosos++;

                deudaTotal += multa;

            }

        }

        sb.append("</ul><br><b>Total morosos: ").append(totalMorosos)
                .append(" | Deuda total: <span style='color:red'><b>S/ ").append(String.format("%.2f", deudaTotal)).append("</b></span></b></html>");

        if (totalMorosos == 0) {

            sb = new StringBuilder("<html><h2 style='color:green'>¡EXCELENTE!</h2><h3>Todos los socios están al día con sus devoluciones</h3></html>");

        }

        JOptionPane.showMessageDialog(this, sb.toString(), "REPORTE DE MOROSOS",
                totalMorosos > 0 ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);

    }

}
