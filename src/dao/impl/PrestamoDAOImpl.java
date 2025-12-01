package dao.impl;

import dao.interfaces.PrestamoDAO;
import model.Prestamo;
import util.Conexion;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.Types;

public class PrestamoDAOImpl implements PrestamoDAO {

    @Override
public void realizarPrestamo(Prestamo p) throws SQLException {
    // PRIMERO: BUSCAR EL ID DEL SOCIO POR SU DNI
    int idSocio = 0;
    String sqlBuscarSocio = "SELECT id FROM socios WHERE dni = ?";
    try (PreparedStatement psSocio = Conexion.getInstance().getConnection().prepareStatement(sqlBuscarSocio)) {
        psSocio.setString(1, p.getDniSocio());  // ← AQUÍ USAS EL DNI QUE TE PASAN
        ResultSet rs = psSocio.executeQuery();
        if (rs.next()) {
            idSocio = rs.getInt("id");
        } else {
            throw new SQLException("Socio no encontrado con DNI: " + p.getDniSocio());
        }
    }

    // AHORA SÍ: INSERTAR EL PRÉSTAMO CON EL ID REAL DEL SOCIO
    String sql = """
        INSERT INTO prestamos 
        (id_socio, id_ejemplar, fecha_prestamo, fecha_devolucion_prevista, estado)
        VALUES (?, ?, ?, ?, 'Activo')
        """;
    
    try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
        ps.setInt(1, idSocio);                                      // ← ID REAL
        ps.setInt(2, p.getIdEjemplar());                            // ← ID del ejemplar
        ps.setDate(3, Date.valueOf(p.getFechaPrestamo()));
        ps.setDate(4, Date.valueOf(p.getFechaPrevistaDevolucion()));
        ps.executeUpdate();
    }
}

    @Override
    public Prestamo buscarPorId(int idPrestamo) throws SQLException {
        String sql = "SELECT * FROM prestamos WHERE id = ?";
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, idPrestamo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Prestamo p = new Prestamo();
                p.setId(rs.getInt("id"));
                p.setIdSocio(rs.getInt("id_socio"));
                p.setIdEjemplar(rs.getInt("id_ejemplar"));
                p.setFechaPrestamo(rs.getDate("fecha_prestamo").toLocalDate());
                p.setFechaPrevistaDevolucion(rs.getDate("fecha_devolucion_prevista").toLocalDate());
                if (rs.getDate("fecha_devolucion_real") != null) {
                    p.setFechaDevolucionReal(rs.getDate("fecha_devolucion_real").toLocalDate());
                }
                p.setEstado(rs.getString("estado"));
                return p;
            }
        }
        return null;
    }

    @Override
    public List<Prestamo> listarActivosPorDni(String dni) throws SQLException {
        List<Prestamo> lista = new ArrayList<>();
        String sql = """
            SELECT p.* FROM prestamos p
            JOIN socios s ON p.id_socio = s.id
            WHERE s.dni = ? AND p.estado = 'Activo'
            """;
        
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Prestamo p = new Prestamo();
                p.setId(rs.getInt("id"));
                p.setIdSocio(rs.getInt("id_socio"));
                p.setIdEjemplar(rs.getInt("id_ejemplar"));
                p.setFechaPrestamo(rs.getDate("fecha_prestamo").toLocalDate());
                p.setFechaPrevistaDevolucion(rs.getDate("fecha_devolucion_prevista").toLocalDate());
                if (rs.getDate("fecha_devolucion_real") != null) {
                    p.setFechaDevolucionReal(rs.getDate("fecha_devolucion_real").toLocalDate());
                }
                p.setEstado(rs.getString("estado"));
                lista.add(p);
            }
        }
        return lista;
    }

    @Override
public void actualizar(Prestamo p) throws SQLException {
    String sql = """
        UPDATE prestamos SET
        estado = ?, 
        fecha_devolucion_real = ?
        WHERE id = ?
        """;
    
    try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
        ps.setString(1, p.getEstado());
        if (p.getFechaDevolucionReal() != null) {
            ps.setDate(2, Date.valueOf(p.getFechaDevolucionReal()));
        } else {
            ps.setNull(2, Types.DATE);
        }
        ps.setInt(3, p.getId());
        ps.executeUpdate();
    }
}
    // ← ESTE ES EL MÉTODO NUEVO PARA EL COMBOBOX
    @Override
    public List<String> listarDnisConPrestamosActivos() throws SQLException {
        List<String> dnis = new ArrayList<>();
        String sql = """
            SELECT DISTINCT s.dni 
            FROM prestamos p 
            JOIN socios s ON p.id_socio = s.id 
            WHERE p.estado = 'Activo'
            """;
        
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                dnis.add(rs.getString("dni"));
            }
        }
        return dnis;
    }

    // MÉTODOS QUE PUEDES DEJAR (o eliminar si no los usas)
    @Override public List<Prestamo> listarPrestamosActivos() throws SQLException { return new ArrayList<>(); }
    @Override public void devolverPrestamo(int idPrestamo) throws SQLException {}
    @Override public List<Prestamo> listarTodos() throws SQLException { return new ArrayList<>(); }
}