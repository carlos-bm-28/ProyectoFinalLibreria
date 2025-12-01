// dao/impl/EjemplarDAOImpl.java
package dao.impl;

import dao.interfaces.EjemplarDAO;
import model.Ejemplar;
import model.Libro;
import util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EjemplarDAOImpl implements EjemplarDAO {

    // === TUS MÉTODOS ORIGINALES (ya los tenías) ===
    @Override
    public void agregarEjemplares(String codigoLibro, int cantidad) throws SQLException {
        String sql = "INSERT INTO ejemplares (codigo_libro, numero_ejemplar, estado) VALUES (?, ?, 'Disponible')";
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
            for (int i = 1; i <= cantidad; i++) {
                ps.setString(1, codigoLibro);
                ps.setInt(2, i);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

   @Override
public int contarDisponibles(String codigoLibro) throws SQLException {
    String sql = "SELECT COUNT(*) FROM ejemplares WHERE codigo_libro = ? AND estado = 'Disponible'";
    try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
        ps.setString(1, codigoLibro);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }
}

@Override
public int contarTotales(String codigoLibro) throws SQLException {
    String sql = "SELECT COUNT(*) FROM ejemplares WHERE codigo_libro = ?";
    try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
        ps.setString(1, codigoLibro);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }
}

    @Override
    public List<Ejemplar> listarDisponibles() throws SQLException {
        List<Ejemplar> lista = new ArrayList<>();
        String sql = """
            SELECT e.*, l.titulo
            FROM ejemplares e
            JOIN libros l ON e.codigo_libro = l.codigo
            WHERE e.estado = 'Disponible'
            ORDER BY l.titulo
            """;
        try (Statement st = Conexion.getInstance().getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Ejemplar e = new Ejemplar();
                e.setId(rs.getInt("id"));
                e.setCodigoLibro(rs.getString("codigo_libro"));
                e.setNumeroEjemplar(rs.getInt("numero_ejemplar"));
                e.setEstado(rs.getString("estado"));
                lista.add(e);
            }
        }
        return lista;
    }

    @Override
    public List<Ejemplar> listarPorLibro(String codigoLibro) throws SQLException {
        List<Ejemplar> lista = new ArrayList<>();
        String sql = "SELECT * FROM ejemplares WHERE codigo_libro = ? ORDER BY numero_ejemplar";
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, codigoLibro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ejemplar e = new Ejemplar();
                    e.setId(rs.getInt("id"));
                    e.setCodigoLibro(rs.getString("codigo_libro"));
                    e.setNumeroEjemplar(rs.getInt("numero_ejemplar"));
                    e.setEstado(rs.getString("estado"));
                    lista.add(e);
                }
            }
        }
        return lista;
    }

    @Override
public void prestar(int idEjemplar) throws SQLException {
    String sql = "UPDATE ejemplares SET estado = 'Prestado' WHERE id = ? AND estado = 'Disponible'";
    try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
        ps.setInt(1, idEjemplar);
        int filas = ps.executeUpdate();
        if (filas == 0) {
            throw new SQLException("El ejemplar " + idEjemplar + " ya está prestado o no existe");
        }
    }
}


    @Override
    public void devolver(int idEjemplar) throws SQLException {
        String sql = "UPDATE ejemplares SET estado = 'Disponible' WHERE id = ?";
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, idEjemplar);
            ps.executeUpdate();
        }
    }

    @Override
    public Ejemplar buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM ejemplares WHERE id = ?";
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Ejemplar e = new Ejemplar();
                    e.setId(rs.getInt("id"));
                    e.setCodigoLibro(rs.getString("codigo_libro"));
                    e.setNumeroEjemplar(rs.getInt("numero_ejemplar"));
                    e.setEstado(rs.getString("estado"));
                    return e;
                }
            }
        }
        return null;
    }

    // === MÉTODOS NUEVOS QUE NECESITAMOS PARA EL PRÉSTAMO (ya los agregué) ===

    // Buscar ejemplar por ID (ya lo tenías, lo dejé igual)

    // Listar disponibles con filtro por texto (título, autor o código del libro)
    public List<Ejemplar> listarDisponiblesConFiltro(String texto) throws SQLException {
        List<Ejemplar> lista = new ArrayList<>();
        String sql = """
            SELECT e.*, l.titulo, l.autor 
            FROM ejemplares e
            JOIN libros l ON e.codigo_libro = l.codigo
            WHERE e.estado = 'Disponible'
            AND (l.titulo LIKE ? OR l.autor LIKE ? OR e.codigo_libro LIKE ? OR ? IS NULL)
            ORDER BY l.titulo
            """;

        String param = texto == null || texto.trim().isEmpty() ? null : "%" + texto.trim() + "%";

        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, param);
            ps.setString(2, param);
            ps.setString(3, param);
            ps.setString(4, param);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ejemplar e = new Ejemplar();
                    e.setId(rs.getInt("id"));
                    e.setCodigoLibro(rs.getString("codigo_libro"));
                    e.setNumeroEjemplar(rs.getInt("numero_ejemplar"));
                    e.setEstado(rs.getString("estado"));
                    lista.add(e);
                }
            }
        }
        return lista;
    }
}