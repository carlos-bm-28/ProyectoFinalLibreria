package view.mantenimiento;

import model.Libro;
import service.LibroService;
import util.TemaCrisol;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import util.Conexion;

public class LibroMantenimientoForm extends JFrame {

    private final LibroService service = new LibroService();
    private DefaultTableModel modelo = new DefaultTableModel();
    private Libro libroSeleccionado = null;

    // CAMPOS
    private JTextField txtCodigo, txtTitulo, txtAutor, txtEditorial, txtAnio, txtCantidadEjemplares;
    private JComboBox<String> cmbCategoria;  // ← AHORA ES COMBOBOX
    private JTable tablaLibros;

    public LibroMantenimientoForm() {
        cargarLibros();
        setTitle("MANTENIMIENTO DE LIBROS - Biblioteca Crisol");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        TemaCrisol.aplicarTemaGlobal();
        initComponents();
        configurarTabla();
    }

    private void configurarTabla() {
        modelo.addColumn("Código");
        modelo.addColumn("Título");
        modelo.addColumn("Autor");
        modelo.addColumn("Editorial");
        modelo.addColumn("Año");
        modelo.addColumn("Categoría");
        modelo.addColumn("Disponibles");
        modelo.addColumn("Total");

        tablaLibros.setModel(modelo);
        TemaCrisol.aplicarEstiloTabla(tablaLibros);
        tablaLibros.setRowHeight(35);
    }

    private void initComponents() {
        setLayout(null);
        getContentPane().setBackground(new Color(245, 245, 245));

        // TÍTULO
        JLabel lblTitulo = new JLabel("<html><h1 style='color:#003366;'>MANTENIMIENTO DE LIBROS</h1></html>");
        lblTitulo.setBounds(20, 10, 800, 60);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        add(lblTitulo);

        // === CAMPOS ===
        int y = 90;

        addLabelAndField("Código:", txtCodigo = new JTextField(), 50, y, 150, 30);
        addLabelAndField("Título:", txtTitulo = new JTextField(), 50, y += 50, 500, 30);
        addLabelAndField("Autor:", txtAutor = new JTextField(), 50, y += 50, 500, 30);
        addLabelAndField("Editorial:", txtEditorial = new JTextField(), 50, y += 50, 400, 30);
        addLabelAndField("Año:", txtAnio = new JTextField(), 50, y += 50, 120, 30);

        // === CATEGORÍA CON COMBOBOX (LO MÁS PRO) ===
        JLabel lblCategoria = new JLabel("Categoría:");
        lblCategoria.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCategoria.setBounds(50, y += 50, 200, 30);
        add(lblCategoria);

        String[] categorias = {
            "Novela", "Ciencia", "Infantil", "Autoayuda", "Historia",
            "Terror", "Romántica", "Fantasía", "Poesía", "Biografía",
            "Cocina", "Tecnología", "Deporte", "Arte", "Religión", "Otros"
        };

        cmbCategoria = new JComboBox<>(categorias);
        cmbCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cmbCategoria.setBounds(220, y, 400, 40);
        cmbCategoria.setBackground(Color.WHITE);
        cmbCategoria.setBorder(BorderFactory.createLineBorder(TemaCrisol.AZUL, 2));
        add(cmbCategoria);

        addLabelAndField("Cantidad de Ejemplares:", txtCantidadEjemplares = new JTextField("1"), 50, y += 60, 120, 30);

        txtCodigo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtCantidadEjemplares.setText("1");

        // === BOTONES ===
        JButton btnNuevo = crearBoton("NUEVO", 650, 80, 140, 50);
        JButton btnGuardar = crearBoton("GUARDAR", 810, 80, 160, 50);
        JButton btnEliminar = crearBoton("ELIMINAR", 980, 80, 160, 50);

        btnNuevo.addActionListener(e -> limpiarCampos());
        btnGuardar.addActionListener(e -> guardarLibro());
        btnEliminar.addActionListener(e -> eliminarLibro());

        // === TABLA ===
        JScrollPane scroll = new JScrollPane();
        tablaLibros = new JTable();
        scroll.setViewportView(tablaLibros);
        scroll.setBounds(20, 450, 1150, 240);
        add(scroll);
        
        JButton btnActualizar = TemaCrisol.boton("ACTUALIZAR LISTA");
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 22));
        btnActualizar.setBounds(750, 350, 300, 60);
        btnActualizar.setBackground(TemaCrisol.AZUL);
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.addActionListener(e -> {
            cargarLibros();  // ← RECARGA TODO CON DATOS REALES DE LA BD
            JOptionPane.showMessageDialog(this, "Lista actualizada correctamente", "Actualizado", JOptionPane.INFORMATION_MESSAGE);
        });
        add(btnActualizar);
        // SELECCIONAR LIBRO DE LA TABLA
        tablaLibros.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int fila = tablaLibros.getSelectedRow();
                if (fila != -1) {
                    String codigo = (String) modelo.getValueAt(fila, 0);
                    try {
                        libroSeleccionado = service.buscarPorCodigo(codigo);
                        if (libroSeleccionado != null) {
                            txtCodigo.setText(libroSeleccionado.getCodigo());
                            txtTitulo.setText(libroSeleccionado.getTitulo());
                            txtAutor.setText(libroSeleccionado.getAutor());
                            txtEditorial.setText(libroSeleccionado.getEditorial());
                            txtAnio.setText(String.valueOf(libroSeleccionado.getAnio()));
                            cmbCategoria.setSelectedItem(libroSeleccionado.getCategoria()); // ← SE MARCA LA CATEGORÍA
                            txtCantidadEjemplares.setText("1");
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Error al seleccionar libro");
                    }
                }
            }
        });
    }

    private void addLabelAndField(String texto, JTextField campo, int x, int y, int w, int h) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setBounds(x, y, 200, h);
        campo.setBounds(x + 170, y, w, h);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        add(lbl);
        add(campo);
    }

    private JButton crearBoton(String texto, int x, int y, int w, int h) {
        JButton btn = TemaCrisol.boton(texto);
        btn.setBounds(x, y, w, h);
        add(btn);
        return btn;
    }

private void cargarLibros() {
    modelo.setRowCount(0);  // ← ESTO LIMPIA LA TABLA ANTES DE RECARGAR

    try {
        for (Libro l : service.listarTodos()) {
            int disponibles = service.contarDisponibles(l.getCodigo());
            int totales = service.contarTotales(l.getCodigo());
            modelo.addRow(new Object[]{
                l.getCodigo(),
                l.getTitulo(),
                l.getAutor(),
                l.getEditorial(),
                l.getAnio(),
                l.getCategoria(),
                disponibles,
                totales
            });
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al cargar libros");
    }
}

private void guardarLibro() {
        try {
            if (txtCodigo.getText().trim().isEmpty() || txtTitulo.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Código y Título son obligatorios");
                return;
            }

            String categoria = (String) cmbCategoria.getSelectedItem();
            if (categoria == null || categoria.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecciona una categoría", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Libro libro = new Libro();
            libro.setCodigo(txtCodigo.getText().trim().toUpperCase());
            libro.setTitulo(txtTitulo.getText().trim());
            libro.setAutor(txtAutor.getText().trim());
            libro.setEditorial(txtEditorial.getText().trim());
            libro.setAnio(txtAnio.getText().isEmpty() ? 2025 : Integer.parseInt(txtAnio.getText()));
            libro.setCategoria(categoria);  // ← AHORA VIENE DEL COMBO

            int cantidad = 1;
            try {
                cantidad = Integer.parseInt(txtCantidadEjemplares.getText());
                if (cantidad < 1) cantidad = 1;
            } catch (Exception e) {
                cantidad = 1;
            }

            service.guardarConEjemplares(libro, cantidad);
            JOptionPane.showMessageDialog(this,
                "Libro guardado con éxito!\n" + cantidad + " ejemplar(es) creado(s)");
            cargarLibros();
            limpiarCampos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        }
    }

private void eliminarLibro() {
    if (libroSeleccionado == null) {
        JOptionPane.showMessageDialog(this, "Selecciona un libro de la tabla");
        return;
    }

    // PRIMERO: VERIFICAR SI HAY PRÉSTAMOS DE ESE LIBRO (ACTIVOS O HISTÓRICOS)
    String sql = """
        SELECT COUNT(*) 
        FROM prestamos p 
        JOIN ejemplares e ON p.id_ejemplar = e.id 
        WHERE e.codigo_libro = ?
        """;

    try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
        ps.setString(1, libroSeleccionado.getCodigo());
        ResultSet rs = ps.executeQuery();
        if (rs.next() && rs.getInt(1) > 0) {
            // HAY PRÉSTAMOS → NO SE PUEDE ELIMINAR
            JOptionPane.showMessageDialog(this,
                "<html><h3>NO SE PUEDE ELIMINAR</h3>" +
                "El libro <b>" + libroSeleccionado.getTitulo() + "</b><br>" +
                "tiene <b>" + rs.getInt(1) + " préstamo(s)</b> registrados.<br><br>" +
                "Solo se pueden eliminar libros que <u>nunca</u> han sido prestados.</html>",
                "PROHIBIDO ELIMINAR", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // SI LLEGA AQUÍ → NO HAY PRÉSTAMOS → SÍ SE PUEDE ELIMINAR
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Eliminar permanentemente el libro:\n" +
            libroSeleccionado.getTitulo() + "\n" +
            "y sus " + service.contarTotales(libroSeleccionado.getCodigo()) + " ejemplar(es)?",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            service.eliminarLibro(libroSeleccionado.getCodigo());
            JOptionPane.showMessageDialog(this, "Libro eliminado correctamente");
            cargarLibros();
            limpiarCampos();
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }
}
    private void limpiarCampos() {
        libroSeleccionado = null;
        txtCodigo.setText("");
        txtTitulo.setText("");
        txtAutor.setText("");
        txtEditorial.setText("");
        txtAnio.setText("");
        cmbCategoria.setSelectedIndex(-1);  // ← QUEDA VACÍO
        txtCantidadEjemplares.setText("1");
        txtCodigo.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibroMantenimientoForm().setVisible(true));
    }
}