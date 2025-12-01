package view.proceso;

import dao.impl.*;
import model.*;
import util.TemaCrisol;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;
import util.Conexion;
import java.sql.ResultSet;
import java.sql.PreparedStatement;


public class DevolucionForm extends JFrame {

    private final PrestamoDAOImpl prestamoDAO = new PrestamoDAOImpl();
    private final EjemplarDAOImpl ejemplarDAO = new EjemplarDAOImpl();
    private final SocioDAOImpl socioDAO = new SocioDAOImpl();

    private JComboBox<String> comboDni;
    private DefaultTableModel modelo;
    private JTable tabla;

    public DevolucionForm() {
        TemaCrisol.aplicarTemaGlobal();
        setTitle("DEVOLUCIÓN MASIVA - Librería Crisol");
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(TemaCrisol.AMARILLO);

        // TÍTULO
        JLabel titulo = new JLabel("DEVOLUCIÓN DE LIBROS", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial Black", Font.BOLD, 60));
        titulo.setForeground(TemaCrisol.AZUL);
        titulo.setBounds(0, 20, 1400, 80);
        add(titulo);

        // COMBOBOX DE DNIs CON PRÉSTAMOS ACTIVOS
        JLabel lblDni = new JLabel("Seleccionar Socio:");
        lblDni.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblDni.setBounds(100, 130, 300, 50);
        add(lblDni);

        comboDni = new JComboBox<>();
        comboDni.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        comboDni.setBounds(400, 130, 400, 60);
        comboDni.addActionListener(e -> cargarPrestamosDelSocio());
        add(comboDni);

        JButton btnRefrescar = TemaCrisol.boton("ACTUALIZAR LISTA");
        btnRefrescar.setBounds(820, 130, 200, 60);
        btnRefrescar.addActionListener(e -> cargarDnisConPrestamos());
        add(btnRefrescar);

        // TABLA
        modelo = new DefaultTableModel();
        modelo.addColumn("ID Préstamo");
        modelo.addColumn("Código");
        modelo.addColumn("Título");
        modelo.addColumn("Fecha Préstamo");
        modelo.addColumn("Fecha Prevista");
        modelo.addColumn("Días Retraso");

        tabla = new JTable(modelo);
        TemaCrisol.aplicarEstiloTabla(tabla);
        tabla.setRowHeight(40);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(100, 220, 1200, 350);
        add(scroll);

        // BOTÓN DEVOLVER TODOS
        JButton btnDevolverTodo = TemaCrisol.boton("DEVOLVER TODOS LOS LIBROS");
        btnDevolverTodo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        btnDevolverTodo.setBounds(100, 600, 1200, 100);
        btnDevolverTodo.setBackground(new Color(0, 150, 0));
        btnDevolverTodo.addActionListener(e -> devolverTodosLosLibros());
        add(btnDevolverTodo);

        cargarDnisConPrestamos(); // carga al iniciar
        setVisible(true);
    }

private void cargarDnisConPrestamos() 
{
    comboDni.removeAllItems();
    try {
        List<String> dnis = prestamoDAO.listarDnisConPrestamosActivos();
        if (dnis.isEmpty()) {
            comboDni.addItem("No hay préstamos activos");
            comboDni.setEnabled(false);
        } else {
            comboDni.setEnabled(true);
            for (String dni : dnis) {
                Socio socio = socioDAO.buscarPorDni(dni);
                if (socio != null) {
                    String item = dni + " - " + socio.getNombres() + " " + socio.getApellidos();
                    comboDni.addItem(item);
                    // GUARDAMOS EL DNI REAL COMO "dato extra" en el ítem
                    comboDni.setSelectedItem(item); // solo para que no de error
                }
            }
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error cargando socios con préstamos: " + ex.getMessage());
        ex.printStackTrace();
    }
}


private void cargarPrestamosDelSocio() {
    modelo.setRowCount(0);
    
    if (comboDni.getSelectedItem() == null || 
        comboDni.getSelectedItem().toString().contains("No hay")) {
        return;
    }

    String textoSeleccionado = comboDni.getSelectedItem().toString();
    String dni = textoSeleccionado.split(" - ")[0]; // ← AQUÍ ESTÁ LA CLAVE

    try {
        List<Prestamo> prestamos = prestamoDAO.listarActivosPorDni(dni);
        for (Prestamo p : prestamos) {
            Ejemplar ej = ejemplarDAO.buscarPorId(p.getIdEjemplar());
            Libro libro = new LibroDAOImpl().buscarPorCodigo(ej.getCodigoLibro());

            long diasRetraso = ChronoUnit.DAYS.between(p.getFechaPrevistaDevolucion(), LocalDate.now());
            if (diasRetraso < 0) diasRetraso = 0;

            modelo.addRow(new Object[]{
                p.getId(),
                ej.getCodigoLibro(),
                libro.getTitulo(),
                p.getFechaPrestamo().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                p.getFechaPrevistaDevolucion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                diasRetraso
            });
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error cargando préstamos: " + ex.getMessage());
        ex.printStackTrace();
    }
}


    private void devolverTodosLosLibros() {
        if (modelo.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay libros para devolver");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Devolver TODOS los libros del socio seleccionado?",
            "Confirmar devolución masiva", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        long multaTotal = 0;
        int devueltos = 0;

        try {
            for (int i = 0; i < modelo.getRowCount(); i++) {
                int idPrestamo = (Integer) modelo.getValueAt(i, 0);
                long diasRetraso = (Long) modelo.getValueAt(i, 5);

                Prestamo p = prestamoDAO.buscarPorId(idPrestamo);
                ejemplarDAO.devolver(p.getIdEjemplar());

                p.setEstado("Devuelto");
                p.setFechaDevolucionReal(LocalDate.now());
                prestamoDAO.actualizar(p);

                multaTotal += diasRetraso * 2; // S/ 2 por día
                devueltos++;
            }

            JOptionPane.showMessageDialog(this,
                "<html><h2>DEVOLUCIÓN MASIVA EXITOSA</h2>" +
                "Libros devueltos: <b>" + devueltos + "</b><br>" +
                (multaTotal > 0 ? "<b style='color:red;'>MULTA TOTAL: S/ " + multaTotal + ".00</b>" : "Sin multa") +
                "</html>", "ÉXITO", JOptionPane.INFORMATION_MESSAGE);

            cargarDnisConPrestamos();
            modelo.setRowCount(0);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // MÉTODO NUEVO PARA EL DAO
    // AGREGA ESTE MÉTODO EN PrestamoDAOImpl.java
    public List<String> listarDnisConPrestamosActivos() throws SQLException {
        List<String> dnis = new ArrayList<>();
        String sql = "SELECT DISTINCT s.dni FROM prestamos p JOIN socios s ON p.id_socio = s.id WHERE p.estado = 'Activo'";
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                dnis.add(rs.getString("dni"));
            }
        }
        return dnis;
    }
}