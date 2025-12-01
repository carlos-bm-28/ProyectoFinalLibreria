// service/SocioService.java
package service;

import dao.impl.SocioDAOImpl;
import dao.interfaces.SocioDAO;
import model.Socio;
import java.sql.SQLException;
import java.util.List;

public class SocioService {

    private final SocioDAO dao = new SocioDAOImpl();

    // GUARDAR O ACTUALIZAR (ESTE ES EL QUE TE FALTABA)
    public void guardar(Socio s) throws SQLException {
        Socio existente = dao.buscarPorDni(s.getDni());
        if (existente == null) {
            dao.insertar(s);           // NUEVO SOCIO
        } else {
            dao.actualizar(s);         // EDITAR EXISTENTE
        }
    }

    public List<Socio> listarTodos() throws SQLException {
        return dao.listarTodos();
    }

    public Socio buscarPorDni(String dni) throws SQLException {
        return dao.buscarPorDni(dni);
    }

    public void cambiarEstado(String dni, String estado) throws SQLException {
        dao.cambiarEstadoPorDni(dni, estado);
    }

    public void eliminar(String dni) throws SQLException {
        dao.eliminarPorDni(dni);
    }
}