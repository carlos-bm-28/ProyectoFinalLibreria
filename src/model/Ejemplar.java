// model/Ejemplar.java
package model;

public class Ejemplar {
    private int id;
    private String codigoLibro;
    private int numeroEjemplar;
    private String estado; // Disponible, Prestado, EnReparacion, Perdido

    public Ejemplar() {}

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCodigoLibro() { return codigoLibro; }
    public void setCodigoLibro(String codigoLibro) { this.codigoLibro = codigoLibro; }
    public int getNumeroEjemplar() { return numeroEjemplar; }
    public void setNumeroEjemplar(int numeroEjemplar) { this.numeroEjemplar = numeroEjemplar; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}