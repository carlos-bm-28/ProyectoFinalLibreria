package model;

import java.time.LocalDate;

public class Prestamo {

    private int id;
    private int idSocio;
    private int idEjemplar;
    private String dniSocio;

    private LocalDate fechaPrestamo;
    private LocalDate fechaPrevistaDevolucion;
    private LocalDate fechaDevolucionReal;  // ← YA INCLUIDO

    private String estado; // "Activo" o "Devuelto"

    // CONSTRUCTOR VACÍO
    public Prestamo() {}

    // GETTERS & SETTERS

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdSocio() {
        return idSocio;
    }

    public void setIdSocio(int idSocio) {
        this.idSocio = idSocio;
    }

    public int getIdEjemplar() {
        return idEjemplar;
    }

    public void setIdEjemplar(int idEjemplar) {
        this.idEjemplar = idEjemplar;
    }

    public String getDniSocio() {
        return dniSocio;
    }

    public void setDniSocio(String dniSocio) {
        this.dniSocio = dniSocio;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaPrevistaDevolucion() {
        return fechaPrevistaDevolucion;
    }

    public void setFechaPrevistaDevolucion(LocalDate fechaPrevistaDevolucion) {
        this.fechaPrevistaDevolucion = fechaPrevistaDevolucion;
    }

    public LocalDate getFechaDevolucionReal() {
        return fechaDevolucionReal;
    }

    public void setFechaDevolucionReal(LocalDate fechaDevolucionReal) {
        this.fechaDevolucionReal = fechaDevolucionReal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
