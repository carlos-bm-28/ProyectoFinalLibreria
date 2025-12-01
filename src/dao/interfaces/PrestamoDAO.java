package dao.interfaces;

import model.Prestamo;
import java.sql.SQLException;
import java.util.List;

public interface PrestamoDAO {
    void realizarPrestamo(Prestamo p) throws SQLException;
    List<Prestamo> listarPrestamosActivos() throws SQLException;
    Prestamo buscarPorId(int idPrestamo) throws SQLException;
    List<Prestamo> listarActivosPorDni(String dni) throws SQLException;
    void actualizar(Prestamo p) throws SQLException;
    void devolverPrestamo(int idPrestamo) throws SQLException;
    List<Prestamo> listarTodos() throws SQLException;

    // ← ESTA LÍNEA FALTABA
    List<String> listarDnisConPrestamosActivos() throws SQLException;
}