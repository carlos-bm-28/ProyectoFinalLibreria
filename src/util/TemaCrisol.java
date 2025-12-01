package util;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class TemaCrisol {

    // COLORES OFICIALES CRISOL
    public static final Color AMARILLO = new Color(255, 193, 7);   // #FFC107
    public static final Color AZUL     = new Color(0, 38, 100);    // #002664
    public static final Color ROJO     = new Color(200, 35, 51);

    // APLICAR TEMA GLOBAL
    public static void aplicarTemaGlobal() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {}

        UIManager.put("Panel.background", AMARILLO);
        UIManager.put("Button.background", AZUL);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 16));
        UIManager.put("OptionPane.background", AMARILLO);
        UIManager.put("OptionPane.messageForeground", AZUL);
    }

    // CREAR BOTÓN PERFECTO CRISOL
    public static JButton boton(String texto) {
        JButton btn = new JButton("<html><center><h2>" + texto + "</h2></center></html>");
        btn.setBackground(AZUL);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 22));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // BOTÓN ROJO (para eliminar)
    public static JButton botonRojo(String texto) {
        JButton btn = boton(texto);
        btn.setBackground(ROJO);
        return btn;
    }
    
    
        // === MÉTODO MÁGICO: TODAS LAS TABLAS QUEDAN CON CABECERA AZUL CRISOL ===
    public static void aplicarEstiloTabla(JTable tabla) {
        tabla.setRowHeight(40);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        // Forzar cabecera azul aunque el tema la cambie después
        SwingUtilities.invokeLater(() -> {
            JTableHeader header = tabla.getTableHeader();
            header.setBackground(new Color(0, 102, 153));
            header.setForeground(Color.WHITE);
            header.setFont(new Font("Segoe UI", Font.BOLD, 16));
            header.setOpaque(true);
            header.setReorderingAllowed(false);

            // Renderer personalizado que IGNORA cualquier tema
            header.setDefaultRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                    label.setBackground(new Color(0, 102, 153));
                    label.setForeground(Color.WHITE);
                    label.setHorizontalAlignment(JLabel.CENTER);
                    label.setOpaque(true);
                    return label;
                }
            });
        });
    }
}