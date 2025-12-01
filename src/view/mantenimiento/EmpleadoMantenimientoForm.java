package view.mantenimiento;

import util.TemaCrisol;
import view.proceso.PrestamoForm;
import view.proceso.DevolucionForm;

import javax.swing.*;
import java.awt.*;
import view.LoginForm;

public class EmpleadoMantenimientoForm extends JFrame {

    public EmpleadoMantenimientoForm() {
        TemaCrisol.aplicarTemaGlobal();
        setTitle("EMPLEADO - Librería Crisol");
        setSize(1000,700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(TemaCrisol.AMARILLO);

        // TÍTULO
        JLabel titulo = new JLabel("LIBRERÍA CRISOL", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial Black", Font.BOLD, 60));
        titulo.setForeground(TemaCrisol.AZUL);
        titulo.setBounds(0, 30, 1000, 100);
        add(titulo);

        JLabel subtitulo = new JLabel("MÓDULO DEL EMPLEADO", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        subtitulo.setForeground(TemaCrisol.AZUL);
        subtitulo.setBounds(0, 120, 1000, 50);
        add(subtitulo);

        // BOTÓN PRÉSTAMO
        JButton btnPrestamo = TemaCrisol.boton("REALIZAR PRÉSTAMO");
        btnPrestamo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        btnPrestamo.setBounds(200, 250, 600, 100);
        btnPrestamo.setBackground(new Color(0, 120, 215));
        btnPrestamo.addActionListener(e -> {
            new PrestamoForm().setVisible(true);
        });
        add(btnPrestamo);

        // BOTÓN DEVOLUCIÓN
JButton btnDevolucion = TemaCrisol.boton("REALIZAR DEVOLUCIÓN");
btnDevolucion.setFont(new Font("Segoe UI", Font.BOLD, 32));
btnDevolucion.setBounds(200, 400, 600, 100);
btnDevolucion.setBackground(new Color(0, 150, 0));
btnDevolucion.addActionListener(e -> {
    try {
        Class.forName("view.proceso.DevolucionForm");
        JFrame form = (JFrame) Class.forName("view.proceso.DevolucionForm").newInstance();
        form.setVisible(true);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, 
            "No se pudo abrir Devolución\nError: " + ex.getMessage(), 
            "Error Crítico", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
});
add(btnDevolucion);

        // BOTÓN CERRAR SESIÓN
        JButton btnCerrar = new JButton("CERRAR SESIÓN");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnCerrar.setBounds(750, 600, 200, 60);
        btnCerrar.setBackground(Color.RED);
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.addActionListener(e -> {
            int op = JOptionPane.showConfirmDialog(this, "¿Cerrar sesión?", "Salir", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                dispose();
                new LoginForm().setVisible(true);
            }
        });
        add(btnCerrar);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EmpleadoMantenimientoForm::new);
    }
}