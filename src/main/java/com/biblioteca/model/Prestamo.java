package com.biblioteca.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Prestamo {

    public enum EstadoPrestamo {
        ACTIVO, DEVUELTO, VENCIDO
    }

    private int id;
    private Usuario usuario;
    private Libro libro;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEsperada;
    private LocalDate fechaDevolucionReal;
    private EstadoPrestamo estado;

    private static final int DIAS_PRESTAMO_DEFAULT = 14;

    public Prestamo() {}

    public Prestamo(int id, Usuario usuario, Libro libro) {
        this.id = id;
        this.usuario = usuario;
        this.libro = libro;
        this.fechaPrestamo = LocalDate.now();
        this.fechaDevolucionEsperada = fechaPrestamo.plusDays(DIAS_PRESTAMO_DEFAULT);
        this.estado = EstadoPrestamo.ACTIVO;
    }

    public Prestamo(int id, Usuario usuario, Libro libro, int diasPrestamo) {
        this(id, usuario, libro);
        this.fechaDevolucionEsperada = fechaPrestamo.plusDays(diasPrestamo);
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Libro getLibro() { return libro; }
    public void setLibro(Libro libro) { this.libro = libro; }

    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(LocalDate fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }

    public LocalDate getFechaDevolucionEsperada() { return fechaDevolucionEsperada; }
    public void setFechaDevolucionEsperada(LocalDate fechaDevolucionEsperada) {
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
    }

    public LocalDate getFechaDevolucionReal() { return fechaDevolucionReal; }
    public void setFechaDevolucionReal(LocalDate fechaDevolucionReal) {
        this.fechaDevolucionReal = fechaDevolucionReal;
    }

    public EstadoPrestamo getEstado() { return estado; }
    public void setEstado(EstadoPrestamo estado) { this.estado = estado; }

    public boolean estaVencido() {
        if (estado == EstadoPrestamo.ACTIVO) {
            return LocalDate.now().isAfter(fechaDevolucionEsperada);
        }
        return false;
    }

    public long getDiasRetraso() {
        if (estaVencido()) {
            return ChronoUnit.DAYS.between(fechaDevolucionEsperada, LocalDate.now());
        }
        return 0;
    }

    public void registrarDevolucion() {
        this.fechaDevolucionReal = LocalDate.now();
        this.estado = EstadoPrestamo.DEVUELTO;
    }

    public void actualizarEstado() {
        if (estado == EstadoPrestamo.ACTIVO && estaVencido()) {
            this.estado = EstadoPrestamo.VENCIDO;
        }
    }

    @Override
    public String toString() {
        return String.format(
                "Prestamo{id=%d, usuario='%s', libro='%s', fechaPrestamo=%s, devolucionEsperada=%s, estado=%s}",
                id, usuario.getNombre(), libro.getTitulo(),
                fechaPrestamo, fechaDevolucionEsperada, estado);
    }
}
