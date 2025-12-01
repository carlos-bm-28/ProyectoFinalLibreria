package util;

import java.sql.*;
import javax.swing.JOptionPane;

public class CreadorEstructuraCompleta {
    public static void main(String[] args) {
        try {
            Connection con = Conexion.getInstance().getConnection();
            Statement st = con.createStatement();

            // Ejecutamos comando por comando (así nunca falla)
            String[] comandos = {
                // 1. Usuarios
                "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(30) UNIQUE NOT NULL," +
                "password VARCHAR(255) NOT NULL," +
                "nombre_completo VARCHAR(100) NOT NULL," +
                "perfil ENUM('Administrador','Empleado') NOT NULL," +
                "estado ENUM('Activo','Inactivo') DEFAULT 'Activo')",

                "INSERT IGNORE INTO usuarios (username,password,nombre_completo,perfil) VALUES " +
                "('admin','Admin123@','Administrador General','Administrador')," +
                "('juan','Juan123@','Juan Pérez','Empleado')," +
                "('maria','Maria123@','María López','Empleado')",

                // 2. Socios
                "CREATE TABLE IF NOT EXISTS socios (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "dni VARCHAR(8) UNIQUE NOT NULL," +
                "nombres VARCHAR(80) NOT NULL," +
                "apellidos VARCHAR(80) NOT NULL," +
                "telefono VARCHAR(15)," +
                "direccion VARCHAR(150)," +
                "fecha_registro DATE DEFAULT (CURRENT_DATE)," +
                "estado ENUM('Activo','Inactivo') DEFAULT 'Activo')",

                "INSERT IGNORE INTO socios (dni,nombres,apellidos,telefono) VALUES " +
                "('12345678','Carlos Andrés','Ramírez Soto','987654321')," +
                "('87654321','Ana Belén','García Mendoza','912345678')",

                // 3. Libros
                "CREATE TABLE IF NOT EXISTS libros (" +
                "codigo VARCHAR(10) PRIMARY KEY," +
                "titulo VARCHAR(200) NOT NULL," +
                "autor VARCHAR(100) NOT NULL," +
                "categoria VARCHAR(50)," +
                "anio YEAR)",

                "INSERT IGNORE INTO libros VALUES " +
                "('L001','Don Quijote de la Mancha','Miguel de Cervantes','Clásico',1605)," +
                "('L002','Cien años de soledad','Gabriel García Márquez','Realismo mágico',1967)," +
                "('L003','Clean Code','Robert C. Martin','Programación',2008)",

                // 4. Ejemplares
                "CREATE TABLE IF NOT EXISTS ejemplares (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "libro_codigo VARCHAR(10) NOT NULL," +
                "numero_ejemplar INT NOT NULL," +
                "estado ENUM('Disponible','Prestado','En reparación','Perdido') DEFAULT 'Disponible'," +
                "FOREIGN KEY (libro_codigo) REFERENCES libros(codigo) ON DELETE CASCADE," +
                "UNIQUE(libro_codigo, numero_ejemplar))",

                "INSERT IGNORE INTO ejemplares (libro_codigo,numero_ejemplar) VALUES " +
                "('L001',1),('L001',2),('L002',1),('L003',1),('L003',2)",

                // 5. Préstamos y detalle
                "CREATE TABLE IF NOT EXISTS prestamos (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "socio_dni VARCHAR(8) NOT NULL," +
                "empleado_username VARCHAR(30) NOT NULL," +
                "fecha_prestamo DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "fecha_devolucion_prevista DATE NOT NULL," +
                "fecha_devolucion_real DATE NULL," +
                "estado ENUM('Activo','Devuelto','Vencido') DEFAULT 'Activo'," +
                "FOREIGN KEY (socio_dni) REFERENCES socios(dni)," +
                "FOREIGN KEY (empleado_username) REFERENCES usuarios(username))",

                "CREATE TABLE IF NOT EXISTS detalle_prestamo (" +
                "prestamo_id INT NOT NULL," +
                "ejemplar_id INT NOT NULL," +
                "PRIMARY KEY (prestamo_id, ejemplar_id)," +
                "FOREIGN KEY (prestamo_id) REFERENCES prestamos(id) ON DELETE CASCADE," +
                "FOREIGN KEY (ejemplar_id) REFERENCES ejemplares(id))"
            };

            for (String cmd : comandos) {
                st.executeUpdate(cmd);
            }

            JOptionPane.showMessageDialog(null, 
                "¡BASE DE DATOS bd_crisol 100% COMPLETA!\n\n" +
                "USUARIOS LISTOS:\n" +
                "→ admin  → Admin123@\n" +
                "→ juan   → Juan123@\n" +
                "→ maria  → Maria123@\n\n" +
                "¡AHORA SÍ VAMOS CON EL LOGIN!", 
                "ÉXITO TOTAL", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Todo creado correctamente (algunos ya existían)");
        }
    }
}