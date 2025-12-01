// dao/impl/LibroDAOImpl.java
package dao.impl;

import dao.interfaces.LibroDAO;
import model.Libro;
import util.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAOImpl implements LibroDAO {

    @Override
    public void insertar(Libro l) throws SQLException {
        String sql = "INSERT INTO libros (codigo, titulo, autor, editorial, anio, categoria) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, l.getCodigo());
            ps.setString(2, l.getTitulo());
            ps.setString(3, l.getAutor());
            ps.setString(4, l.getEditorial());
            ps.setInt(5, l.getAnio());
            ps.setString(6, l.getCategoria());
            ps.executeUpdate();
        }
    }

    @Override
    public void actualizar(Libro l) throws SQLException {
        String sql = "UPDATE libros SET titulo=?, autor=?, editorial=?, anio=?, categoria=? WHERE codigo=?";
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, l.getTitulo());
            ps.setString(2, l.getAutor());
            ps.setString(3, l.getEditorial());
            ps.setInt(4, l.getAnio());
            ps.setString(5, l.getCategoria());
            ps.setString(6, l.getCodigo());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(String codigo) throws SQLException {
        try (PreparedStatement ps = Conexion.getInstance().getConnection()
                .prepareStatement("DELETE FROM libros WHERE codigo=?")) {
            ps.setString(1, codigo);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Libro> listarTodos() throws SQLException {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libros ORDER BY titulo";
        try (Statement st = Conexion.getInstance().getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Libro l = new Libro();
                l.setCodigo(rs.getString("codigo"));
                l.setTitulo(rs.getString("titulo"));
                l.setAutor(rs.getString("autor"));
                l.setEditorial(rs.getString("editorial"));
                l.setAnio(rs.getInt("anio"));
                l.setCategoria(rs.getString("categoria"));
                lista.add(l);
            }
        }
        return lista;
    }

    @Override
    public List<Libro> buscar(String texto) throws SQLException {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE titulo LIKE ? OR autor LIKE ? OR codigo LIKE ?";
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
            String like = "%" + texto + "%";
            ps.setString(1, like); ps.setString(2, like); ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Libro l = new Libro();
                    l.setCodigo(rs.getString("codigo"));
                    l.setTitulo(rs.getString("titulo"));
                    l.setAutor(rs.getString("autor"));
                    l.setEditorial(rs.getString("editorial"));
                    l.setAnio(rs.getInt("anio"));
                    l.setCategoria(rs.getString("categoria"));
                    lista.add(l);
                }
            }
        }
        return lista;
    }

    @Override
    public Libro buscarPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT * FROM libros WHERE codigo = ?";
        try (PreparedStatement ps = Conexion.getInstance().getConnection().prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Libro l = new Libro();
                    l.setCodigo(rs.getString("codigo"));
                    l.setTitulo(rs.getString("titulo"));
                    l.setAutor(rs.getString("autor"));
                    l.setEditorial(rs.getString("editorial"));
                    l.setAnio(rs.getInt("anio"));
                    l.setCategoria(rs.getString("categoria"));
                    return l;
                }
            }
        }
        return null;
    }
}