// dao/interfaces/UsuarioDAO.java
package dao.interfaces;

import model.Usuario;
import java.sql.SQLException;
import java.util.List;

public interface UsuariosDAO {
    
    // Para el login
    Usuario autenticar(String username, String password) throws SQLException;
    
    // CRUD completo
    void insertar(Usuario usuario) throws SQLException;
    void actualizar(Usuario usuario) throws SQLException;
    void eliminar(String username) throws SQLException;
    List<Usuario> listarTodos() throws SQLException;
    Usuario buscarPorUsername(String username) throws SQLException;
}