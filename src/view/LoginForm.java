package view;

import util.Conexion;
import java.sql.*;
import javax.swing.*;
import java.awt.*;
import util.TemaCrisol;
import view.mantenimiento.EmpleadoMantenimientoForm;
import view.proceso.PrestamoForm;

public class LoginForm extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnIngresar;

    // COLORES CRISOL
    private static final Color AMARILLO_CRISOL = new Color(255, 193, 7);
    private static final Color AZUL_CRISOL = new Color(0, 38, 100);

    public LoginForm() {
        configurarVentana();
        crearInterfaz();
        setVisible(true);
    }

    private void configurarVentana() {
        setTitle("Librería Crisol - Iniciar Sesión");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(AMARILLO_CRISOL);
    }

    private void crearInterfaz() {
    JPanel panel = new JPanel(null);
    panel.setBackground(AMARILLO_CRISOL);

    // LOGO
    JLabel lblCrisol = new JLabel("CRISOL", SwingConstants.CENTER);
    lblCrisol.setFont(new Font("Arial Black", Font.BOLD, 110));
    lblCrisol.setForeground(AZUL_CRISOL);
    lblCrisol.setBounds(0, 40, 900, 130);
    panel.add(lblCrisol);

    JLabel lblSub = new JLabel("LIBROS Y MÁS", SwingConstants.CENTER);
    lblSub.setFont(new Font("Arial", Font.BOLD, 40));
    lblSub.setForeground(AZUL_CRISOL);
    lblSub.setBounds(0, 160, 900, 60);
    panel.add(lblSub);

    // CAMPOS
    JLabel lblUser = new JLabel("USUARIO:");
    lblUser.setFont(new Font("Segoe UI", Font.BOLD, 22));
    lblUser.setForeground(AZUL_CRISOL);
    lblUser.setBounds(280, 260, 150, 40);
    panel.add(lblUser);

    txtUser = new JTextField();
    txtUser.setBounds(280, 300, 340, 55);
    txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 20));
    txtUser.setBorder(BorderFactory.createLineBorder(AZUL_CRISOL, 3));
    panel.add(txtUser);

    JLabel lblPass = new JLabel("CONTRASEÑA:");
    lblPass.setFont(new Font("Segoe UI", Font.BOLD, 22));
    lblPass.setForeground(AZUL_CRISOL);
    lblPass.setBounds(280, 370, 180, 40);
    panel.add(lblPass);

    txtPass = new JPasswordField();
    txtPass.setBounds(280, 410, 340, 55);
    txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 20));
    txtPass.setBorder(BorderFactory.createLineBorder(AZUL_CRISOL, 3));
    panel.add(txtPass);

    // BOTÓN INGRESAR - AHORA SÍ SE VE PERFECTO
btnIngresar = new JButton("INGRESAR");
btnIngresar.setBounds(280, 490, 340, 80);
btnIngresar.setBackground(AZUL_CRISOL);
btnIngresar.setForeground(Color.WHITE);           // ← LETRAS BLANCAS
btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 32));
btnIngresar.setFocusPainted(false);
btnIngresar.setOpaque(true);                      // ← IMPORTANTE
btnIngresar.setBorderPainted(false);              // ← QUITA EL BORDE
btnIngresar.setContentAreaFilled(true);
btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
btnIngresar.addActionListener(e -> validarLogin());
panel.add(btnIngresar);

    add(panel);
}

    private void validarLogin() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            PreparedStatement ps = Conexion.getInstance().getConnection()
                .prepareStatement("SELECT perfil, nombre_completo FROM usuarios WHERE username=? AND password=? AND estado='Activo'");
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String perfil = rs.getString("perfil");
                String nombre = rs.getString("nombre_completo");

                JOptionPane.showMessageDialog(this, "¡Bienvenido " + nombre + "!", 
                    "Librería Crisol", JOptionPane.INFORMATION_MESSAGE);

                dispose();

                if ("Administrador".equals(perfil)) {
                    new DashboardAdmin().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null,
        "Bienvenido Empleado: " + nombre + "\n\nAccediendo al módulo de operaciones...",
        "Librería Crisol", JOptionPane.INFORMATION_MESSAGE);

    // ABRIMOS LA VENTANA DEL EMPLEADO CON LOS DOS BOTONES GIGANTES
    SwingUtilities.invokeLater(() -> {
        new EmpleadoMantenimientoForm().setVisible(true);
    });
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Usuario, contraseña incorrecta o cuenta inactiva", 
                    "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error de conexión: " + e.getMessage());
        }
    }

public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        TemaCrisol.aplicarTemaGlobal();// ← ESTA LÍNEA ES LA MAGIA
        new LoginForm();
    });
}}