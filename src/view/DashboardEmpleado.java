// view/DashboardEmpleado.java
package view;

import javax.swing.*;
import java.awt.*;

public class DashboardEmpleado extends JFrame {

    public DashboardEmpleado() {
        initComponents();
        setTitle("EMPLEADO - Biblioteca Crisol");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));

        JLabel lbl = new JLabel("<html><h1 style='color:#006600;'>EMPLEADO</h1><h2>Biblioteca Crisol</h2></html>", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 32));

        JButton btnPrestamo = new JButton("<html><h2>REALIZAR PRÉSTAMO<br>O DEVOLUCIÓN</h2></html>");
        btnPrestamo.setPreferredSize(new Dimension(450, 220));
        btnPrestamo.setBackground(new Color(0, 153, 0));
        btnPrestamo.setForeground(Color.WHITE);
        btnPrestamo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnPrestamo.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // VERSIÓN SEGURA: NO DA ERROR (el formulario no existe todavía)
        btnPrestamo.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Módulo de Préstamos y Devoluciones\n\nPróximamente disponible",
                "En desarrollo", 
                JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnSalir = new JButton("Cerrar Sesión");
        btnSalir.setPreferredSize(new Dimension(300, 60));
        btnSalir.setBackground(new Color(220, 53, 69));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSalir.addActionListener(e -> {
            this.dispose();
            new view.LoginForm().setVisible(true);
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(40, 40, 40, 40);
        gbc.gridx = 0;

        gbc.gridy = 0;
        panel.add(lbl, gbc);

        gbc.gridy = 1;
        panel.add(btnPrestamo, gbc);

        gbc.gridy = 2;
        panel.add(btnSalir, gbc);

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardEmpleado().setVisible(true));
    }
}