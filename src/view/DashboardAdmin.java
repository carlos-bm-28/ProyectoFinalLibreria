// view/DashboardAdmin.java
package view;

import javax.swing.*;
import java.awt.*;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import util.TemaCrisol;
public class DashboardAdmin extends JFrame {

    // COLORES OFICIALES CRISOL
    private static final Color AMARILLO_CRISOL = new Color(255, 193, 7);
    private static final Color AZUL_CRISOL    = new Color(0, 38, 100);
    private static final Color BLANCO         = Color.WHITE;

    public DashboardAdmin() {
        setTitle("ADMINISTRADOR - Librería Crisol");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new GridBagLayout());
        panelPrincipal.setBackground(AMARILLO_CRISOL);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 30, 20, 30);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // LOGO CRISOL GIGANTE
        JLabel lblLogo = new JLabel("CRISOL", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Arial Black", Font.BOLD, 120));
        lblLogo.setForeground(AZUL_CRISOL);

        JLabel lblSub = new JLabel("LIBROS Y MÁS", SwingConstants.CENTER);
        lblSub.setFont(new Font("Arial", Font.BOLD, 50));
        lblSub.setForeground(AZUL_CRISOL);

        JPanel panelLogo = new JPanel(new BorderLayout());
        panelLogo.setOpaque(false);
        panelLogo.add(lblLogo, BorderLayout.CENTER);
        panelLogo.add(lblSub, BorderLayout.SOUTH);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(50, 0, 30, 0);
        panelPrincipal.add(panelLogo, gbc);

        // BOTONES
        JButton btnSocios    = crearBoton("GESTIONAR SOCIOS",          AZUL_CRISOL);
        JButton btnLibros    = crearBoton("GESTIONAR LIBROS",          AZUL_CRISOL);
        JButton btnPrestamos = crearBoton("PRÉSTAMOS Y DEVOLUCIONES",  AZUL_CRISOL);
        JButton btnUsuarios  = crearBoton("GESTIONAR USUARIOS",        AZUL_CRISOL);
        JButton btnReportes  = crearBoton("REPORTES",                  AZUL_CRISOL);
        JButton btnSalir     = crearBoton("CERRAR SESIÓN",             new Color(200, 35, 51));

        // ACCIONES (sin errores)
        btnSocios.addActionListener(e -> new view.mantenimiento.SocioMantenimientoForm().setVisible(true));
        btnLibros.addActionListener(e -> new view.mantenimiento.LibroMantenimientoForm().setVisible(true));
        
        // PRÉSTAMOS: temporal hasta que creemos el form real
        btnPrestamos.addActionListener(e -> {
    new view.proceso.HistorialPrestamosForm().setVisible(true);
});

        btnUsuarios.addActionListener(e -> new view.mantenimiento.UsuarioMantenimientoForm().setVisible(true));
        btnReportes.addActionListener(e -> JOptionPane.showMessageDialog(this, "Reportes próximamente"));
        
        btnSalir.addActionListener(e -> {
            int op = JOptionPane.showConfirmDialog(this, "¿Cerrar sesión?", "Salir", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                dispose();
                new LoginForm().setVisible(true);
            }
        });

        // POSICIONAR
        gbc.gridwidth = 1;
        gbc.insets = new Insets(20, 80, 20, 80);
        gbc.gridx = 0; gbc.gridy = 1; panelPrincipal.add(btnSocios, gbc);
        gbc.gridx = 1;                 panelPrincipal.add(btnLibros, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panelPrincipal.add(btnPrestamos, gbc);
        gbc.gridx = 1;                 panelPrincipal.add(btnUsuarios, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panelPrincipal.add(btnReportes, gbc);
        gbc.gridx = 1;                 panelPrincipal.add(btnSalir, gbc);

        add(panelPrincipal);

        // Look and Feel sin errores
        try {
              UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
    // Si falla el tema del sistema, sigue con el default
        }
    }

    private JButton crearBoton(String texto, Color fondo) {
        JButton btn = new JButton("<html><center><h2>" + texto + "</h2></center></html>");
        btn.setPreferredSize(new Dimension(450, 180));
        btn.setBackground(fondo);
        btn.setForeground(BLANCO);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 24));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(AZUL_CRISOL, 4, true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        TemaCrisol.aplicarTemaGlobal();// ← También aquí por si ejecutan directo
        new DashboardAdmin().setVisible(true);
    });
}}