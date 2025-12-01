package model;

public class Socio {
    private int id;
    private String dni;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String direccion;
    private String estado; // Activo / Inactivo

    // Constructor vacío
    public Socio() {}

    // Constructor completo
    public Socio(int id, String dni, String nombres, String apellidos, String telefono, String direccion, String estado) {
        this.id = id;
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.direccion = direccion;
        this.estado = estado;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
}