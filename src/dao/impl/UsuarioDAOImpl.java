// dao/impl/UsuarioDAOImpl.java
package dao.impl;

import dao.interfaces.UsuariosDAO;
import model.Usuario;
import util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOImpl implements UsuariosDAO {

    @Override
    public Usuario autenticar(String username, String password) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setUsername(rs.getString("username"));
                    u.setNombreCompleto(rs.getString("nombre_completo"));
                    u.setPerfil(rs.getString("perfil"));
                    // NO cargamos password por seguridad
                    return u;
                }
            }
        }
        return null; // usuario o contraseña incorrecta
    }

    @Override
    public void insertar(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuarios (username, password, nombre_completo, perfil) VALUES (?,?,?,?)";
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getNombreCompleto());
            ps.setString(4, u.getPerfil());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Usuario u) throws SQLException {
        String sql = "UPDATE usuarios SET password = ?, nombre_completo = ?, perfil = ? WHERE username = ?";
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, u.getPassword());
            ps.setString(2, u.getNombreCompleto());
            ps.setString(3, u.getPerfil());
            ps.setString(4, u.getUsername());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(String username) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE username = ?";
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT username, nombre_completo, perfil FROM usuarios ORDER BY nombre_completo";
        try (Statement st = Conexion.getInstance().getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setUsername(rs.getString("username"));
                u.setNombreCompleto(rs.getString("nombre_completo"));
                u.setPerfil(rs.getString("perfil"));
                lista.add(u);
            }
        }
        return lista;
    }

    @Override
    // dao/impl/UsuarioDAOImpl.java → AGREGÁ ESTE MÉTODO
public Usuario buscarPorUsername(String username) throws SQLException {
    String sql = "SELECT * FROM usuarios WHERE username = ?";
    try (Connection cn = Conexion.getInstance().getConnection();
         PreparedStatement ps = cn.prepareStatement(sql)) {
        
        ps.setString(1, username);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setNombreCompleto(rs.getString("nombre_completo"));
                u.setPerfil(rs.getString("perfil"));
                return u;
            }
        }
    }
    return null;
}
}