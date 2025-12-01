// dao/impl/SocioDAOImpl.java
package dao.impl;

import dao.interfaces.SocioDAO;
import model.Socio;
import util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SocioDAOImpl implements SocioDAO {

    // INSERTAR NUEVO SOCIO
    @Override
    public void insertar(Socio s) throws SQLException {
        String sql = "INSERT INTO socios (dni, nombres, apellidos, telefono, direccion, estado) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, s.getDni());
            ps.setString(2, s.getNombres());
            ps.setString(3, s.getApellidos());
            ps.setString(4, s.getTelefono());
            ps.setString(5, s.getDireccion());
            ps.setString(6, "Activo"); // nuevo socio siempre activo
            ps.executeUpdate();
        }
    }

    // ACTUALIZAR SOCIO EXISTENTE
    @Override
    public void actualizar(Socio s) throws SQLException {
        String sql = "UPDATE socios SET nombres=?, apellidos=?, telefono=?, direccion=?, estado=? WHERE dni=?";
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, s.getNombres());
            ps.setString(2, s.getApellidos());
            ps.setString(3, s.getTelefono());
            ps.setString(4, s.getDireccion());
            ps.setString(5, s.getEstado());
            ps.setString(6, s.getDni());
            ps.executeUpdate();
        }
    }

    // DAR DE BAJA (lógico)
    @Override
    public void eliminarLogico(int id) throws SQLException {
        String sql = "UPDATE socios SET estado='Inactivo' WHERE id=?";
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ELIMINAR DEFINITIVO (físico)
    @Override
    public void eliminarFisico(int id) throws SQLException {
        String sql = "DELETE FROM socios WHERE id=?";
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // LISTAR TODOS LOS SOCIOS
    @Override
    public List<Socio> listarTodos() throws SQLException {
        List<Socio> lista = new ArrayList<>();
        String sql = "SELECT * FROM socios ORDER BY apellidos, nombres";
        try (Statement st = Conexion.getInstance().getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Socio s = new Socio();
                s.setId(rs.getInt("id"));
                s.setDni(rs.getString("dni"));
                s.setNombres(rs.getString("nombres"));
                s.setApellidos(rs.getString("apellidos"));
                s.setTelefono(rs.getString("telefono"));
                s.setDireccion(rs.getString("direccion"));
                s.setEstado(rs.getString("estado") != null ? rs.getString("estado") : "Activo");
                lista.add(s);
            }
        }
        return lista;
    }

    // BUSCAR POR TEXTO (DNI, APELLIDOS O NOMBRES)
    @Override
    public List<Socio> buscar(String texto) throws SQLException {
        List<Socio> lista = new ArrayList<>();
        String sql = "SELECT * FROM socios WHERE dni LIKE ? OR apellidos LIKE ? OR nombres LIKE ? ORDER BY apellidos";
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
            String like = "%" + texto + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Socio s = new Socio();
                    s.setId(rs.getInt("id"));
                    s.setDni(rs.getString("dni"));
                    s.setNombres(rs.getString("nombres"));
                    s.setApellidos(rs.getString("apellidos"));
                    s.setTelefono(rs.getString("telefono"));
                    s.setDireccion(rs.getString("direccion"));
                    s.setEstado(rs.getString("estado") != null ? rs.getString("estado") : "Activo");
                    lista.add(s);
                }
            }
        }
        return lista;
    }

    // === MÉTODOS NUEVOS QUE NECESITÁS PARA EL FORMULARIO ===

    // BUSCAR SOCIO POR DNI (para hacer clic en la tabla)
    public Socio buscarPorDni(String dni) throws SQLException {
        String sql = "SELECT * FROM socios WHERE dni = ?";
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Socio s = new Socio();
                    s.setId(rs.getInt("id"));
                    s.setDni(rs.getString("dni"));
                    s.setNombres(rs.getString("nombres"));
                    s.setApellidos(rs.getString("apellidos"));
                    s.setTelefono(rs.getString("telefono"));
                    s.setDireccion(rs.getString("direccion"));
                    s.setEstado(rs.getString("estado") != null ? rs.getString("estado") : "Activo");
                    return s;
                }
            }
        }
        return null;
    }

    // CAMBIAR ESTADO POR DNI (Dar de baja / alta)
    public void cambiarEstadoPorDni(String dni, String nuevoEstado) throws SQLException {
        String sql = "UPDATE socios SET estado = ? WHERE dni = ?";
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setString(2, dni);
            ps.executeUpdate();
        }
    }

    // ELIMINAR DEFINITIVO POR DNI (solo si no tiene préstamos)
   // === ELIMINAR DEFINITIVO CON CONTROL DE PRÉSTAMOS (100% FUNCIONAL) ===

    // === ELIMINAR DEFINITIVO - VERSIÓN CORRECTA PARA TU BD (usa id_socio) ===
@Override
public void eliminarPorDni(String dni) throws SQLException {
    Connection cn = Conexion.getInstance().getConnection();

    // 1. Obtener el ID del socio a partir del DNI
    int idSocio = -1;
    String sqlId = "SELECT id FROM socios WHERE dni = ?";
    try (PreparedStatement ps = cn.prepareStatement(sqlId)) {
        ps.setString(1, dni);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                idSocio = rs.getInt("id");
            } else {
                throw new SQLException("No se encontró el socio con DNI: " + dni);
            }
        }
    }

    // 2. Verificar si tiene préstamos activos usando id_socio
    String check = "SELECT COUNT(*) FROM prestamos WHERE id_socio = ? AND fecha_devolucion_real IS NULL";
    try (PreparedStatement ps = cn.prepareStatement(check)) {
        ps.setInt(1, idSocio);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("No se puede eliminar: el socio tiene préstamos activos o no devueltos");
            }
        }
    }

    // 3. Si no tiene préstamos → eliminar físicamente
    String deleteSql = "DELETE FROM socios WHERE dni = ?";
    try (PreparedStatement ps = cn.prepareStatement(deleteSql)) {
        ps.setString(1, dni);
        int filas = ps.executeUpdate();
        if (filas == 0) {
            throw new SQLException("Error al eliminar el socio");
        }
    }
}

        public List<String> listarDnisActivos() throws SQLException {
        List<String> dnis = new ArrayList<>();
        String sql = "SELECT dni FROM socios WHERE estado = 'Activo' ORDER BY apellidos, nombres";
        
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                dnis.add(rs.getString("dni"));
            }
        }
        return dnis;
    }
        
        public int obtenerIdPorDni(String dni) throws SQLException {
    String sql = "SELECT id FROM socios WHERE dni = ?";
    try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
        ps.setString(1, dni);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("id");
        }
        throw new SQLException("Socio no encontrado con DNI: " + dni);
    }
}
}