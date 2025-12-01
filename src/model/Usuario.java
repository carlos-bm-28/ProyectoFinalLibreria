package model;

public class Usuario {
    
    private String username;
    private String password;        // solo se usa al crear/modificar
    private String nombreCompleto;
    private String perfil;          // "Administrador" o "Empleado"

    // Constructor vacío (obligatorio para DAO)
    public Usuario() {}

    // Constructor con todos los campos (opcional, pero útil)
    public Usuario(String username, String password, String nombreCompleto, String perfil) {
        this.username = username;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.perfil = perfil;
    }

    // ===================== GETTERS Y SETTERS =====================
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    // Método útil para mostrar en combo o label
    @Override
    public String toString() {
        return nombreCompleto + " (" + username + ") - " + perfil;
    }
}