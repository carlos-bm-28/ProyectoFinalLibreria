// dao/interfaces/LibroDAO.java
package dao.interfaces;

import model.Libro;
import java.sql.SQLException;
import java.util.List;

public interface LibroDAO {
    void insertar(Libro l) throws SQLException;
    void actualizar(Libro l) throws SQLException;
    void eliminar(String codigo) throws SQLException;
    List<Libro> listarTodos() throws SQLException;
    List<Libro> buscar(String texto) throws SQLException;
    Libro buscarPorCodigo(String codigo) throws SQLException;
    
}