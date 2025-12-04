package view.reportes.Reporte6;

import util.TemaCrisol;
import javax.swing.*;
import java.awt.*;

public class Reporte6Form extends JFrame {

    public Reporte6Form() {
        TemaCrisol.aplicarTemaGlobal();
        setTitle("REPORTE #6 - GENERAL DEL SISTEMA");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel titulo = new JLabel("REPORTE #6 - GENERAL DEL SISTEMA", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial Black", Font.BOLD, 42));
        titulo.setForeground(TemaCrisol.AZUL);
        titulo.setBounds(0, 30, 1100, 80);
        add(titulo);

        // BOTÓN 1 - LIBROS EN PDF
        JButton btnLibros = TemaCrisol.boton("1. GENERAR PDF - LIBROS REGISTRADOS");
        btnLibros.setFont(new Font("Segoe UI", Font.BOLD, 28));
        btnLibros.setBounds(100, 150, 900, 100);
        btnLibros.setBackground(TemaCrisol.AZUL);
        btnLibros.addActionListener(e -> GeneradorPDFReporte6.generarPDFLibros());
        add(btnLibros);

        // BOTÓN 2 - TRABAJADORES EN PDF
        JButton btnTrabajadores = TemaCrisol.boton("2. GENERAR PDF - TRABAJADORES");
        btnTrabajadores.setFont(new Font("Segoe UI", Font.BOLD, 28));
        btnTrabajadores.setBounds(100, 270, 900, 100);
        btnTrabajadores.setBackground(TemaCrisol.AZUL);
        btnTrabajadores.addActionListener(e -> GeneradorPDFReporte6.generarPDFTrabajadores());
        add(btnTrabajadores);

        // BOTÓN 3 - SOCIOS EN PDF
        JButton btnSocios = TemaCrisol.boton("3. GENERAR PDF - SOCIOS REGISTRADOS");
        btnSocios.setFont(new Font("Segoe UI", Font.BOLD, 28));
        btnSocios.setBounds(100, 390, 900, 100);
        btnSocios.setBackground(TemaCrisol.AZUL);
        btnSocios.addActionListener(e -> GeneradorPDFReporte6.generarPDFSocios());
        add(btnSocios);

        JLabel info = new JLabel("→ Todos los reportes se generan directamente en PDF ←", SwingConstants.CENTER);
        info.setFont(new Font("Segoe UI", Font.ITALIC, 22));
        info.setForeground(Color.DARK_GRAY);
        info.setBounds(0, 550, 1100, 50);
        add(info);

        setVisible(true);
    }
}