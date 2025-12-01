// dao/interfaces/SocioDAO.java
package dao.interfaces;

import model.Socio;
import java.sql.SQLException;
import java.util.List;

public interface SocioDAO {
    void insertar(Socio s) throws SQLException;
    void actualizar(Socio s) throws SQLException;
    void eliminarLogico(int id) throws SQLException;
    void eliminarFisico(int id) throws SQLException;
    List<Socio> listarTodos() throws SQLException;
    List<Socio> buscar(String texto) throws SQLException;

    // MÉTODOS NUEVOS
    Socio buscarPorDni(String dni) throws SQLException;
    void cambiarEstadoPorDni(String dni, String nuevoEstado) throws SQLException;
    void eliminarPorDni(String dni) throws SQLException;
    List<String> listarDnisActivos() throws SQLException;
}