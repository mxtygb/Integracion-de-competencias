package com.biblioteca.model;

import java.util.ArrayList;
import java.util.List;

public class Usuario {

    public enum TipoUsuario {
        ADMINISTRADOR, ESTUDIANTE, DOCENTE
    }

    private int id;
    private String nombre;
    private String correo;
    private String contrasena;
    private TipoUsuario tipoUsuario;
    private List<Prestamo> prestamos;

    public Usuario() {
        this.prestamos = new ArrayList<>();
    }

    public Usuario(int id, String nombre, String correo, String contrasena, TipoUsuario tipoUsuario) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.tipoUsuario = tipoUsuario;
        this.prestamos = new ArrayList<>();
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TipoUsuario tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public List<Prestamo> getPrestamos() { return prestamos; }
    public void setPrestamos(List<Prestamo> prestamos) { this.prestamos = prestamos; }

    public boolean esAdministrador() {
        return this.tipoUsuario == TipoUsuario.ADMINISTRADOR;
    }

    @Override
    public String toString() {
        return String.format("Usuario{id=%d, nombre='%s', correo='%s', tipo=%s}",
                id, nombre, correo, tipoUsuario);
    }
}
