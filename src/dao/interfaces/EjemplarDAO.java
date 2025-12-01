// dao/interfaces/EjemplarDAO.java
package dao.interfaces;

import model.Ejemplar;
import java.sql.SQLException;
import java.util.List;

public interface EjemplarDAO {
    void agregarEjemplares(String codigoLibro, int cantidad) throws SQLException;
    int contarDisponibles(String codigoLibro) throws SQLException;
    int contarTotales(String codigoLibro) throws SQLException;
    List<Ejemplar> listarDisponibles() throws SQLException;
    List<Ejemplar> listarPorLibro(String codigoLibro) throws SQLException;
    void prestar(int idEjemplar) throws SQLException;
    void devolver(int idEjemplar) throws SQLException;
    Ejemplar buscarPorId(int id) throws SQLException;
}