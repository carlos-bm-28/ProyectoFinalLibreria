// model/Libro.java
package model;

public class Libro {
    private String codigo;
    private String titulo;
    private String autor;
    private String editorial;
    private int anio;
    private String categoria;

    public Libro() {}

    // Getters y Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }
    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    @Override
    public String toString() {
        return titulo + " - " + autor;
    }
}