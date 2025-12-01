// view/mantenimiento/SocioMantenimientoForm.java
package view.mantenimiento;

import model.Socio;
import service.SocioService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import util.TemaCrisol;

public class SocioMantenimientoForm extends JFrame {

    private final SocioService service = new SocioService();
    private final DefaultTableModel modelo = new DefaultTableModel();

    private JTextField txtDni, txtNombres, txtApellidos, txtTelefono, txtDireccion;
    private JComboBox<String> cmbEstado;
    private Socio socioSeleccionado = null;

    public SocioMantenimientoForm() {
        // === TEMA CRISOL EN ESTE FORMULARIO ===
    getContentPane().setBackground(new Color(255, 193, 7)); // Amarillo Crisol
    setTitle("MANTENIMIENTO DE SOCIOS - Biblioteca Crisol");
    setSize(1300, 900);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    JPanel panel = new JPanel(null);
    panel.setBackground(new Color(245, 250, 255));
    panel.setPreferredSize(new Dimension(1260, 1400)); // ← AUMENTÉ LA ALTURA A 1400

    construirInterfaz(panel);
    cargarSocios();

    JScrollPane scroll = new JScrollPane(panel);
    scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    setContentPane(scroll); 
    }

    private void construirInterfaz(JPanel panel) {
    // TÍTULO
    JLabel titulo = new JLabel("MANTENIMIENTO DE SOCIOS", SwingConstants.CENTER);
    titulo.setFont(new Font("Segoe UI", Font.BOLD, 38));
    titulo.setForeground(new Color(0, 51, 102));
    titulo.setBounds(0, 30, 1260, 60);
    panel.add(titulo);

    // TABLA
    modelo.addColumn("DNI");
    modelo.addColumn("Nombres");
    modelo.addColumn("Apellidos");
    modelo.addColumn("Teléfono");
    modelo.addColumn("Dirección");
    modelo.addColumn("Estado");

   


    JTable tabla = new JTable(modelo);
TemaCrisol.aplicarEstiloTabla(tabla);
JScrollPane scrollTabla = new JScrollPane(tabla);
scrollTabla.setBounds(40, 120, 1180, 350);
panel.add(scrollTabla);

    int y = 500;
    agregarCampo(panel, "DNI:", txtDni = new JTextField(), 80, y, 200, 45);
    agregarCampo(panel, "Nombres:", txtNombres = new JTextField(), 80, y += 80, 500, 45);
    agregarCampo(panel, "Apellidos:", txtApellidos = new JTextField(), 80, y += 80, 500, 45);
    agregarCampo(panel, "Teléfono:", txtTelefono = new JTextField(), 80, y += 80, 300, 45);
    agregarCampo(panel, "Dirección:", txtDireccion = new JTextField(), 80, y += 80, 800, 45);

    JLabel lblEstado = new JLabel("Estado:");
    lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 18));
    lblEstado.setBounds(80, y += 80, 150, 45);
    panel.add(lblEstado);

    cmbEstado = new JComboBox<>(new String[]{"Activo", "Inactivo"});
    cmbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 18));
    cmbEstado.setBounds(230, y, 250, 45);
    panel.add(cmbEstado);

    // === BOTONES PRINCIPALES ===
    y += 100;
    JButton btnNuevo    = crearBoton(panel, "NUEVO",        100, y, 200, 80);
    JButton btnGuardar  = crearBoton(panel, "GUARDAR",      320, y, 200, 80);
    JButton btnBaja     = crearBoton(panel, "DAR DE BAJA",  540, y, 230, 80);
    JButton btnAlta     = crearBoton(panel, "DAR DE ALTA",  780, y, 230, 80);

    // === BOTÓN ELIMINAR DEFINITIVO - ROJO Y GIGANTE (NUNCA MÁS SE ESCONDE) ===
        y += 100;
    JButton btnEliminar = TemaCrisol.botonRojo("ELIMINAR DEFINITIVO");
    btnEliminar.setBounds(100, y, 1060, 90);
    panel.add(btnEliminar);
    btnEliminar.addActionListener(e -> eliminarDefinitivo());
    btnEliminar.setForeground(Color.WHITE);
    btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 32));
    btnEliminar.setFocusPainted(false);
    btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
    panel.add(btnEliminar);

    // ACCIONES
    btnNuevo.addActionListener(e -> limpiar());
    btnGuardar.addActionListener(e -> guardar());
    btnBaja.addActionListener(e -> cambiarEstado("Inactivo"));
    btnAlta.addActionListener(e -> cambiarEstado("Activo"));
    btnEliminar.addActionListener(e -> eliminarDefinitivo());

    // CLICK EN TABLA
    tabla.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
            int fila = tabla.getSelectedRow();
            if (fila >= 0) {
                String dni = (String) modelo.getValueAt(fila, 0);
                try {
                    socioSeleccionado = service.buscarPorDni(dni);
                    if (socioSeleccionado != null) {
                        txtDni.setText(socioSeleccionado.getDni());
                        txtNombres.setText(socioSeleccionado.getNombres());
                        txtApellidos.setText(socioSeleccionado.getApellidos());
                        txtTelefono.setText(socioSeleccionado.getTelefono());
                        txtDireccion.setText(socioSeleccionado.getDireccion());
                        cmbEstado.setSelectedItem(socioSeleccionado.getEstado());
                        txtDni.setEnabled(false);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error al cargar socio");
                }
            }
        }
    });
}

    private void guardar() {
        try {
            String dni = txtDni.getText().trim();
            String nombres = txtNombres.getText().trim();
            String direccion = txtDireccion.getText().trim();

            if (dni.isEmpty() || nombres.isEmpty() || direccion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "DNI, Nombres y Dirección son obligatorios", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Socio s = new Socio();
            s.setDni(dni);
            s.setNombres(nombres);
            s.setApellidos(txtApellidos.getText().trim());
            s.setTelefono(txtTelefono.getText().trim());
            s.setDireccion(direccion);
            s.setEstado((String) cmbEstado.getSelectedItem());

            service.guardar(s);
            JOptionPane.showMessageDialog(this, "Socio guardado con éxito");
            cargarSocios();
            limpiar();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage());
        }
    }

    private void cambiarEstado(String estado) {
        if (socioSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un socio");
            return;
        }
        try {
            service.cambiarEstado(socioSeleccionado.getDni(), estado);
            JOptionPane.showMessageDialog(this, "Estado cambiado a: " + estado);
            cargarSocios();
            limpiar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cambiar estado");
        }
    }

    private void eliminarDefinitivo() {
        if (socioSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un socio");
            return;
        }
        int op = JOptionPane.showConfirmDialog(this,
            "¿ELIMINAR DEFINITIVAMENTE al socio " + socioSeleccionado.getDni() + "?\n\nNO SE PUEDE DESHACER",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (op == JOptionPane.YES_OPTION) {
            try {
                service.eliminar(socioSeleccionado.getDni());
                JOptionPane.showMessageDialog(this, "Socio eliminado para siempre");
                cargarSocios();
                limpiar();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void cargarSocios() {
        modelo.setRowCount(0);
        try {
            List<Socio> lista = service.listarTodos();
            for (Socio s : lista) {
                modelo.addRow(new Object[]{
                    s.getDni(),
                    s.getNombres(),
                    s.getApellidos(),
                    s.getTelefono(),
                    s.getDireccion(),
                    s.getEstado()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar socios");
        }
    }

    private void limpiar() {
        socioSeleccionado = null;
        txtDni.setText(""); txtNombres.setText(""); txtApellidos.setText("");
        txtTelefono.setText(""); txtDireccion.setText("");
        cmbEstado.setSelectedIndex(0);
        txtDni.setEnabled(true);
        txtDni.requestFocus();
    }

    private void agregarCampo(JPanel p, String texto, JComponent c, int x, int y, int w, int h) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 18));
        l.setBounds(x, y, 180, h);
        p.add(l);
        c.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        c.setBounds(x + 190, y, w, h);
        p.add(c);
    }

    private JButton crearBoton(JPanel p, String texto, int x, int y, int w, int h) {
    JButton b = TemaCrisol.boton(texto);
    b.setBounds(x, y, w, h);
    p.add(b);
    return b;
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SocioMantenimientoForm().setVisible(true));
    }
}