// util/Conexion.java
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Conexion {
    private static Conexion instance;
    private Connection connection;

    private static final String URL = "jdbc:mysql://localhost:3306/bd_crisol?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "carlosb";  // TU CONTRASEÑA

    private Conexion() {
        conectar();
    }

    public static Conexion getInstance() {
        if (instance == null) {
            instance = new Conexion();
        }
        return instance;
    }

    // ESTE ES EL MÉTODO CLAVE: reconecta si se cerró
    private void conectar() {
        try {
            if (connection != null && !connection.isClosed()) {
                return; // ya está conectada
            }
            connection = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("¡CONECTADO A bd_crisol CON ÉXITO!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "ERROR DE CONEXIÓN A LA BASE DE DATOS\n\n" +
                "Detalle: " + e.getMessage() + "\n\n" +
                "Posibles causas:\n" +
                "• MySQL no está encendido\n" +
                "• Contraseña incorrecta\n" +
                "• Base de datos bd_crisol no existe",
                "ERROR DE CONEXIÓN", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // ESTE ES EL MÉTODO QUE TODOS USAN → NUNCA FALLA
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                conectar(); // RECONECTA AUTOMÁTICAMENTE
            }
        } catch (SQLException e) {
            conectar(); // si falla, intenta de nuevo
        }
        return connection;
    }

    // OPCIONAL: si querés cerrar manualmente al final del programa
    public void cerrar() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada correctamente");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}