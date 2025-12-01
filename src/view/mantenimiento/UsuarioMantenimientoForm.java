// view/mantenimiento/UsuarioMantenimientoForm.java
package view.mantenimiento;

import model.Usuario;
import service.UsuarioService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import util.TemaCrisol;

public class UsuarioMantenimientoForm extends JFrame {

    private UsuarioService service = new UsuarioService();
    private DefaultTableModel modelo = new DefaultTableModel();

    private JTextField txtUsername, txtNombre;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbPerfil;
    private Usuario usuarioSeleccionado = null;

    public UsuarioMantenimientoForm() {
        setTitle("MANTENIMIENTO DE USUARIOS - Biblioteca Crisol");
        setSize(1200, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);

        // Panel principal con scroll
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(245, 250, 255));
        panel.setPreferredSize(new Dimension(1160, 1000));

        construirInterfaz(panel);
        cargarUsuarios();

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setContentPane(scrollPane);
    }

    private void construirInterfaz(JPanel panel) {
        
// === TEMA CRISOL EN ESTE FORMULARIO ===
getContentPane().setBackground(new Color(255, 193, 7)); // Amarillo Crisol
               // TÍTULO
        JLabel titulo = new JLabel("MANTENIMIENTO DE USUARIOS", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 38));
        titulo.setForeground(new Color(0, 0, 51, 102));   // Azul oscuro Crisol
        titulo.setBounds(0, 30, 1160, 60);
        panel.add(titulo);

        // === TABLA DE USUARIOS - PERFECTA Y UNIFORME ===
        modelo.addColumn("Username");
        modelo.addColumn("Nombre Completo");
        modelo.addColumn("Perfil");

        JTable tabla = new JTable(modelo);
        TemaCrisol.aplicarEstiloTabla(tabla);   // ← AZUL OSCURO, NUNCA MÁS BLANCA

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBounds(40, 120, 1080, 320);
        panel.add(scrollTabla);

        // CAMPOS
        int y = 480;

        agregarCampo(panel, "Username:", txtUsername = new JTextField(), 100, y, 350, 45);
        agregarCampo(panel, "Password:", txtPassword = new JPasswordField(), 100, y += 80, 350, 45);
        agregarCampo(panel, "Nombre Completo:", txtNombre = new JTextField(), 100, y += 80, 700, 45);

        JLabel lblPerfil = new JLabel("Perfil:");
        lblPerfil.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPerfil.setBounds(100, y += 80, 150, 45);
        panel.add(lblPerfil);

        cmbPerfil = new JComboBox<>(new String[]{"Administrador", "Empleado"});
        cmbPerfil.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        cmbPerfil.setBounds(260, y, 300, 45);
        panel.add(cmbPerfil);

        // BOTONES GRANDES Y VISIBLES
        JButton btnNuevo = crearBoton(panel, "NUEVO", 180, y += 100, 200, 80);
        JButton btnGuardar = crearBoton(panel, "GUARDAR", 410, y, 220, 80);
        JButton btnEliminar = crearBoton(panel, "ELIMINAR", 660, y, 220, 80);

        btnNuevo.addActionListener(e -> limpiar());
        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());

        // CLICK EN LA TABLA
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int fila = tabla.getSelectedRow();
                if (fila >= 0) {
                    String username = (String) modelo.getValueAt(fila, 0);
                    try {
                        usuarioSeleccionado = service.buscarPorUsername(username);
                        if (usuarioSeleccionado != null) {
                            txtUsername.setText(usuarioSeleccionado.getUsername());
                            txtNombre.setText(usuarioSeleccionado.getNombreCompleto());
                            cmbPerfil.setSelectedItem(usuarioSeleccionado.getPerfil());
                            txtPassword.setText("");
                            txtUsername.setEnabled(false);
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(UsuarioMantenimientoForm.this, "Error al cargar datos del usuario");
                    }
                }
            }
        });
    }

    private void agregarCampo(JPanel panel, String texto, JComponent campo, int x, int y, int w, int h) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setBounds(x, y, 200, h);
        panel.add(label);

        campo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        campo.setBounds(x + 210, y, w, h);
        panel.add(campo);
    }

    private JButton crearBoton(JPanel panel, String texto, int x, int y, int w, int h) {
    JButton btn = TemaCrisol.boton(texto);
    btn.setBounds(x, y, w, h);
    panel.add(btn);
    return btn;
}

    private void cargarUsuarios() {
        modelo.setRowCount(0);
        try {
            List<Usuario> lista = service.listarTodos();
            for (Usuario u : lista) {
                modelo.addRow(new Object[]{
                    u.getUsername(),
                    u.getNombreCompleto(),
                    u.getPerfil()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios: " + e.getMessage());
        }
    }

    private void guardar() {
    try {
        String username = txtUsername.getText().trim();
        String nombre = txtNombre.getText().trim();
        String password = new String(txtPassword.getPassword());
        String perfil = (String) cmbPerfil.getSelectedItem();

        // Validaciones básicas
        if (username.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username y Nombre son obligatorios");
            return;
        }

        // SI ES NUEVO USUARIO → contraseña obligatoria y fuerte
        if (usuarioSeleccionado == null && password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La contraseña es obligatoria para nuevo usuario");
            return;
        }

        // SI ES NUEVO O CAMBIÓ LA CONTRASEÑA → validar que sea fuerte
        if (usuarioSeleccionado == null || !password.isEmpty()) {
            if (!util.Validaciones.esPasswordFuerte(password)) {
                return; // ya muestra el mensaje bonito
            }
        }

        Usuario u = new Usuario();
        u.setUsername(username);
        u.setNombreCompleto(nombre);
        u.setPerfil(perfil);
        if (!password.isEmpty()) {
            u.setPassword(password); // sin encriptar por ahora (después te paso con BCrypt si querés)
        }

        service.guardar(u);
        JOptionPane.showMessageDialog(this, "Usuario guardado con éxito");
        cargarUsuarios();
        limpiar();

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage());
    }
}

    private void eliminar() {
        if (usuarioSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario de la tabla");
            return;
        }
        if (usuarioSeleccionado.getUsername().equals("admin")) {
            JOptionPane.showMessageDialog(this, "No podés borrar al administrador principal");
            return;
        }

        int confirma = JOptionPane.showConfirmDialog(this,
                "¿Eliminar usuario " + usuarioSeleccionado.getUsername() + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirma == JOptionPane.YES_OPTION) {
            try {
                service.eliminar(usuarioSeleccionado.getUsername());
                JOptionPane.showMessageDialog(this, "Usuario eliminado");
                cargarUsuarios();
                limpiar();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar");
            }
        }
    }

    private void limpiar() {
        usuarioSeleccionado = null;
        txtUsername.setText("");
        txtNombre.setText("");
        txtPassword.setText("");
        cmbPerfil.setSelectedIndex(1); // Empleado por defecto
        txtUsername.setEnabled(true);
        txtUsername.requestFocus();
    }

    // Para probar rápido
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UsuarioMantenimientoForm().setVisible(true));
    }
}