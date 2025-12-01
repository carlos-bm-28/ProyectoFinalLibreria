// service/LibroService.java  ← VERSIÓN FINAL (SIN EjemplarService)
package service;

import dao.impl.LibroDAOImpl;
import dao.impl.EjemplarDAOImpl;
import model.Libro;
import model.Ejemplar;
import java.sql.SQLException;
import java.util.List;

public class LibroService {
    private final LibroDAOImpl libroDAO = new LibroDAOImpl();
    private final EjemplarDAOImpl ejemplarDAO = new EjemplarDAOImpl();

    // Guardar libro + sus ejemplares
    public void guardarConEjemplares(Libro libro, int cantidadEjemplares) throws SQLException {
        if (libroDAO.buscarPorCodigo(libro.getCodigo()) == null) {
            libroDAO.insertar(libro);
        } else {
            libroDAO.actualizar(libro);
        }
        ejemplarDAO.agregarEjemplares(libro.getCodigo(), cantidadEjemplares);
    }

    public List<Libro> listarTodos() throws SQLException {
        return libroDAO.listarTodos();
    }

    public List<Libro> buscar(String texto) throws SQLException {
        return libroDAO.buscar(texto);
    }

    public int contarDisponibles(String codigoLibro) throws SQLException {
        return ejemplarDAO.contarDisponibles(codigoLibro);
    }

    public int contarTotales(String codigoLibro) throws SQLException {
        return ejemplarDAO.contarTotales(codigoLibro);
    }

    public List<Ejemplar> listarEjemplaresDisponibles() throws SQLException {
        return ejemplarDAO.listarDisponibles();
    }

    public void eliminarLibro(String codigo) throws SQLException {
        libroDAO.eliminar(codigo);
        // Los ejemplares se borran en cascada si configuraste ON DELETE CASCADE en la BD
    }
    
    // AGREGÁ ESTE MÉTODO EN TU LibroService.java
public Libro buscarPorCodigo(String codigo) throws SQLException {
    for (Libro l : listarTodos()) {
        if (l.getCodigo().equalsIgnoreCase(codigo)) {
            return l;
        }
    }
    return null;
}
    
    
}