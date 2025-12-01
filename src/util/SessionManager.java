package util;

import model.Usuario;

public class SessionManager {
    private static SessionManager instance;
    private Usuario usuarioActual;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void logout() {
        this.usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public boolean esAdministrador() {
       return usuarioActual != null && "Administrador".equals(usuarioActual.getPerfil());
    }

    public boolean esEmpleado() {
       return usuarioActual != null && "Empleado".equals(usuarioActual.getPerfil());
    }

    public String getNombreUsuario() {
        return usuarioActual != null ? usuarioActual.getNombreCompleto() : "Invitado";
    }

    public String getUsername() {
        return usuarioActual != null ? usuarioActual.getUsername() : null;
    }
}
