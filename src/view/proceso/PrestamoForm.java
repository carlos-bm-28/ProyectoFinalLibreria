package view.proceso;

import dao.impl.*;
import dao.interfaces.EjemplarDAO;
import model.*;
import util.TemaCrisol;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import util.Conexion;
import view.LoginForm;

public class PrestamoForm extends JFrame {

    private final SocioDAOImpl socioDAO = new SocioDAOImpl();
    private final EjemplarDAO ejemplarDAO = new EjemplarDAOImpl();
    private final PrestamoDAOImpl prestamoDAO = new PrestamoDAOImpl();

    private JComboBox<String> cmbDniSocio;
    private JLabel lblSocioInfo;
    private JComboBox<String> cmbCategoria;
    private JTextField txtBuscarLibro;
    private JTable tablaLibros, tablaSeleccionados;
    private DefaultTableModel modeloLibros, modeloSeleccionados;
    private JTextField txtFechaDevolucion;
    private JButton btnAgregar, btnQuitar, btnPrestar;
    private Socio socioSeleccionado = null;
    private List<Ejemplar> librosParaPrestar = new ArrayList<>();

        public PrestamoForm() {
        TemaCrisol.aplicarTemaGlobal();
        setTitle("PRÉSTAMO DE LIBROS - Librería Crisol");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(null);
        panel.setBackground(TemaCrisol.AMARILLO);
        panel.setPreferredSize(new Dimension(1400, 1300)); // ← IMPORTANTE: tamaño del contenido

        // ====== TODO TU CÓDIGO DE COMPONENTES (TÍTULO, SOCIO, TABLAS, BOTONES, ETC.) ======
        // (Todo el código que ya tenías: título, socio, búsqueda, tablas, fechas, botones...)
        // PEGA AQUÍ TODOS LOS COMPONENTES QUE YA TENÍAS (desde JLabel titulo hasta btnVolver)

        // TÍTULO
        JLabel titulo = new JLabel("PRÉSTAMO DE LIBROS", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial Black", Font.BOLD, 70));
        titulo.setForeground(TemaCrisol.AZUL);
        titulo.setBounds(0, 10, 1450, 90);
        panel.add(titulo);

        // 1. SOCIO
        JLabel lblSocio = new JLabel("1. Seleccionar Socio:");
        lblSocio.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblSocio.setBounds(50, 110, 400, 40);
        panel.add(lblSocio);

        cmbDniSocio = new JComboBox<>();
        cmbDniSocio.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        cmbDniSocio.setBounds(50, 160, 300, 50);
        cargarDnisActivos();
        panel.add(cmbDniSocio);

        JButton btnCargar = TemaCrisol.boton("CARGAR SOCIO");
        btnCargar.setBounds(370, 160, 250, 50);
        btnCargar.addActionListener(e -> cargarSocio());
        panel.add(btnCargar);

        lblSocioInfo = new JLabel("Socio: Ninguno seleccionado");
        lblSocioInfo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblSocioInfo.setBounds(50, 220, 800, 40);
        panel.add(lblSocioInfo);

        // 2. BUSCAR LIBROS
        JLabel lblBuscar = new JLabel("2. Buscar Libros Disponibles:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblBuscar.setBounds(50, 280, 500, 40);
        panel.add(lblBuscar);

        cmbCategoria = new JComboBox<>(new String[]{"Todas", "Novela", "Ciencia", "Infantil", "Historia", "Terror", "Romántica", "Fantasía", "Autoayuda"});
        cmbCategoria.setBounds(50, 330, 250, 50);
        panel.add(cmbCategoria);

        txtBuscarLibro = new JTextField();
        txtBuscarLibro.setBounds(320, 330, 500, 50);
        txtBuscarLibro.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        panel.add(txtBuscarLibro);

        JButton btnBuscar = TemaCrisol.boton("BUSCAR");
        btnBuscar.setBounds(840, 330, 200, 50);
        btnBuscar.addActionListener(e -> buscarLibros());
        panel.add(btnBuscar);

        // TABLA DE BÚSQUEDA
        modeloLibros = new DefaultTableModel();
        modeloLibros.addColumn("Código");
        modeloLibros.addColumn("Título");
        modeloLibros.addColumn("Autor");
        modeloLibros.addColumn("Categoría");
        modeloLibros.addColumn("Disponibles");

        tablaLibros = new JTable(modeloLibros);
        TemaCrisol.aplicarEstiloTabla(tablaLibros);
        tablaLibros.setRowHeight(35);

        JScrollPane scrollLibros = new JScrollPane(tablaLibros);
        scrollLibros.setBounds(50, 400, 1350, 220);
        panel.add(scrollLibros);

        // BOTÓN AGREGAR
        btnAgregar = TemaCrisol.boton("AGREGAR A PRÉSTAMO");
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 24));
        btnAgregar.setBounds(50, 640, 400, 70);
        btnAgregar.addActionListener(e -> agregarLibro());
        panel.add(btnAgregar);

        // TABLA DE SELECCIONADOS
        JLabel lblSeleccion = new JLabel("3. Libros a Prestar (máximo 3):");
        lblSeleccion.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblSeleccion.setBounds(50, 730, 600, 40);
        panel.add(lblSeleccion);

        modeloSeleccionados = new DefaultTableModel();
        modeloSeleccionados.addColumn("Código");
        modeloSeleccionados.addColumn("Título");
        modeloSeleccionados.addColumn("Autor");

        tablaSeleccionados = new JTable(modeloSeleccionados);
        TemaCrisol.aplicarEstiloTabla(tablaSeleccionados);
        tablaSeleccionados.setRowHeight(40);

        JScrollPane scrollSeleccion = new JScrollPane(tablaSeleccionados);
        scrollSeleccion.setBounds(50, 780, 1000, 140);
        panel.add(scrollSeleccion);

        btnQuitar = TemaCrisol.boton("QUITAR");
        btnQuitar.setBounds(1070, 820, 150, 70);
        btnQuitar.addActionListener(e -> quitarLibro());
        panel.add(btnQuitar);

        // FECHAS
        JLabel lblFechas = new JLabel("Fecha Préstamo:");
        lblFechas.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblFechas.setBounds(50, 940, 200, 40);
        panel.add(lblFechas);

        JLabel lblHoy = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblHoy.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHoy.setBounds(260, 940, 200, 40);
        panel.add(lblHoy);

        JLabel lblDev = new JLabel("Fecha Máxima Devolución (máx 30 días):");
        lblDev.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblDev.setBounds(500, 940, 500, 40);
        panel.add(lblDev);

        txtFechaDevolucion = new JTextField(LocalDate.now().plusDays(15).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        txtFechaDevolucion.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        txtFechaDevolucion.setBounds(1000, 935, 180, 50);
        panel.add(txtFechaDevolucion);

        // BOTONES FINALES
        btnPrestar = TemaCrisol.boton("REALIZAR PRÉSTAMO");
        btnPrestar.setFont(new Font("Segoe UI", Font.BOLD, 36));
        btnPrestar.setBounds(50, 1010, 700, 100);
        btnPrestar.addActionListener(e -> realizarPrestamoFinal());
        panel.add(btnPrestar);

       JButton btnVolver = TemaCrisol.boton("VOLVER");
btnVolver.setFont(new Font("Segoe UI", Font.BOLD, 36));
btnVolver.setBounds(780, 1010, 600, 100);
btnVolver.addActionListener(e -> {
    dispose();                                           // cierra esta ventana
});
panel.add(btnVolver);
        // SCROLL GLOBAL (LO MÁS IMPORTANTE)
        JScrollPane scrollGeneral = new JScrollPane(panel);
        scrollGeneral.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollGeneral.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setContentPane(scrollGeneral);

        // TAMAÑO FINAL DE LA VENTANA
        setSize(1450, 900);  // ← Con scroll, 900 alcanza y queda bonito
        setLocationRelativeTo(null);
        setVisible(true);
    }
    private void cargarDnisActivos() {
        try {
            List<String> dnis = socioDAO.listarDnisActivos();
            cmbDniSocio.removeAllItems();
            for (String dni : dnis) cmbDniSocio.addItem(dni);
        } catch (SQLException e) {}
    }

    private void cargarSocio() {
        String dni = (String) cmbDniSocio.getSelectedItem();
        if (dni == null) return;
        try {
            socioSeleccionado = socioDAO.buscarPorDni(dni);
            lblSocioInfo.setText("Socio: " + socioSeleccionado.getNombres() + " " + socioSeleccionado.getApellidos() + " | DNI: " + dni);
        } catch (SQLException e) {}
    }

    private void buscarLibros() {
        modeloLibros.setRowCount(0);
        String cat = (String) cmbCategoria.getSelectedItem();
        String texto = txtBuscarLibro.getText().toLowerCase();
        try {
            for (Libro l : new LibroDAOImpl().listarTodos()) {
                if (!"Todas".equals(cat) && !l.getCategoria().equals(cat)) continue;
                if (!texto.isEmpty() && !l.getTitulo().toLowerCase().contains(texto) && !l.getAutor().toLowerCase().contains(texto)) continue;
                int disp = ejemplarDAO.contarDisponibles(l.getCodigo());
                if (disp > 0) {
                    modeloLibros.addRow(new Object[]{l.getCodigo(), l.getTitulo(), l.getAutor(), l.getCategoria(), disp});
                }
            }
        } catch (SQLException e) {}
    }

private void agregarLibro() {
    int fila = tablaLibros.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this, "Selecciona un libro");
        return;
    }
    if (modeloSeleccionados.getRowCount() >= 3) {
        JOptionPane.showMessageDialog(this, "Máximo 3 libros por préstamo");
        return;
    }

    String codigo = (String) modeloLibros.getValueAt(fila, 0);

    // VERIFICAR SI YA ESTÁ AGREGADO
    for (int i = 0; i < modeloSeleccionados.getRowCount(); i++) {
        if (modeloSeleccionados.getValueAt(i, 0).equals(codigo)) {
            JOptionPane.showMessageDialog(this, "Ya agregaste este libro", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
    }

    String titulo = (String) modeloLibros.getValueAt(fila, 1);
    String autor = (String) modeloLibros.getValueAt(fila, 2);

    modeloSeleccionados.addRow(new Object[]{codigo, titulo, autor});
}
    private void quitarLibro() {
        int fila = tablaSeleccionados.getSelectedRow();
        if (fila != -1) modeloSeleccionados.removeRow(fila);
    }

        // MÉTODO PARA EVITAR DUPLICADOS DE TÍTULO (NO DEJAR PRESTAR EL MISMO LIBRO 2 VECES AL MISMO SOCIO)
    private boolean socioYaTieneEsteTitulo(String dniSocio, String codigoLibro) {
        String sql = """
            SELECT COUNT(*) 
            FROM prestamos p 
            JOIN ejemplares e ON p.id_ejemplar = e.id 
            WHERE p.id_socio = (SELECT id FROM socios WHERE dni = ?) 
              AND e.codigo_libro = ? 
              AND p.estado = 'Activo'
            """;

        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, dniSocio);
            ps.setString(2, codigoLibro);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;  // true = ya tiene ese título prestado
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error verificando duplicados: " + ex.getMessage());
            ex.printStackTrace();
        }
        return false; // si hay error, por seguridad dejamos pasar (o puedes poner true)
    }
    
    
    
    
private void realizarPrestamoFinal() {
    if (socioSeleccionado == null || modeloSeleccionados.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "Faltan datos: socio o libros");
        return;
    }

    LocalDate fechaDev;
    try {
        fechaDev = LocalDate.parse(txtFechaDevolucion.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (fechaDev.isBefore(LocalDate.now()) || fechaDev.isAfter(LocalDate.now().plusDays(30))) {
            JOptionPane.showMessageDialog(this, "Fecha inválida. Máximo 30 días.");
            return;
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Formato fecha: dd/MM/yyyy");
        return;
    }

    try {
        for (int i = 0; i < modeloSeleccionados.getRowCount(); i++) {
            String codigoLibro = (String) modeloSeleccionados.getValueAt(i, 0);

            // VALIDAR DUPLICADO DE TÍTULO
            if (socioYaTieneEsteTitulo(socioSeleccionado.getDni(), codigoLibro)) {
                JOptionPane.showMessageDialog(this,
                    "ERROR: El socio " + socioSeleccionado.getNombres() +
                    " ya tiene prestado un ejemplar del libro:\n" + codigoLibro,
                    "TÍTULO DUPLICADO", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // OBTENER UN EJEMPLAR DISPONIBLE
            Ejemplar ej = ejemplarDAO.listarPorLibro(codigoLibro).stream()
                .filter(e -> "Disponible".equals(e.getEstado()))
                .findFirst()
                .orElse(null);

            if (ej == null) {
                JOptionPane.showMessageDialog(this,
                    "No hay ejemplares disponibles de: " + codigoLibro,
                    "SIN STOCK", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // CREAR EL PRÉSTAMO
            Prestamo p = new Prestamo();
            p.setDniSocio(socioSeleccionado.getDni());           // ← DNI CORRECTO
            p.setIdEjemplar(ej.getId());                         // ← ID REAL DEL EJEMPLAR
            p.setFechaPrestamo(LocalDate.now());
            p.setFechaPrevistaDevolucion(fechaDev);

            // GUARDAR EN BD
            prestamoDAO.realizarPrestamo(p);

            // CAMBIAR ESTADO DEL EJEMPLAR A PRESTADO
            ejemplarDAO.prestar(ej.getId());
        }

        // ÉXITO TOTAL
        JOptionPane.showMessageDialog(this,
            "<html><h2>PRÉSTAMO REALIZADO CON ÉXITO</h2>" +
            "Libros prestados: <b>" + modeloSeleccionados.getRowCount() + "</b><br>" +
            "Fecha devolución: <b>" + fechaDev.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "</b></html>",
            "ÉXITO TOTAL", JOptionPane.INFORMATION_MESSAGE);

        // LIMPIAR
        modeloSeleccionados.setRowCount(0);
        txtFechaDevolucion.setText(LocalDate.now().plusDays(15).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        modeloLibros.setRowCount(0);
        buscarLibros(); // recargar tabla

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, 
            "Error al realizar préstamo:\n" + ex.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PrestamoForm::new);
    }
}