// service/UsuarioService.java
package service;

import dao.impl.UsuarioDAOImpl;
import dao.interfaces.UsuariosDAO;
import model.Usuario;
import java.sql.SQLException;
import java.util.List;

public class UsuarioService {
    private final UsuariosDAO dao = new UsuarioDAOImpl();

    public Usuario login(String username, String password) throws SQLException {
        return dao.autenticar(username, password);
    }

    public void guardar(Usuario u) throws SQLException {
        if (dao.buscarPorUsername(u.getUsername()) == null) {
            dao.insertar(u);
        } else {
            dao.actualizar(u);
        }
    }

    public void eliminar(String username) throws SQLException {
        dao.eliminar(username);
    }

    public List<Usuario> listarTodos() throws SQLException {
        return dao.listarTodos();
    }
    
    public Usuario buscarPorUsername(String username) throws SQLException {
    return dao.buscarPorUsername(username);
}
    
}