package com.biblioteca.model;

public class Libro {

    public enum Categoria {
        CIENCIA, HISTORIA, LITERATURA, TECNOLOGIA, MATEMATICAS, ARTE, OTRO
    }

    private int id;
    private String titulo;
    private String autor;
    private String editorial;
    private int anioPublicacion;
    private String isbn;
    private Categoria categoria;
    private boolean disponible;
    private int stock;

    public Libro() {
        this.disponible = true;
        this.stock = 1;
    }

    public Libro(int id, String titulo, String autor, String editorial,
                 int anioPublicacion, String isbn, Categoria categoria, int stock) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.anioPublicacion = anioPublicacion;
        this.isbn = isbn;
        this.categoria = categoria;
        this.stock = stock;
        this.disponible = stock > 0;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }

    public int getAnioPublicacion() { return anioPublicacion; }
    public void setAnioPublicacion(int anioPublicacion) { this.anioPublicacion = anioPublicacion; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public int getStock() { return stock; }
    public void setStock(int stock) {
        this.stock = stock;
        this.disponible = stock > 0;
    }

    public void reducirStock() {
        if (this.stock > 0) {
            this.stock--;
            this.disponible = this.stock > 0;
        }
    }

    public void aumentarStock() {
        this.stock++;
        this.disponible = true;
    }

    @Override
    public String toString() {
        return String.format("Libro{id=%d, titulo='%s', autor='%s', editorial='%s', disponible=%s, stock=%d}",
                id, titulo, autor, editorial, disponible, stock);
    }
}
