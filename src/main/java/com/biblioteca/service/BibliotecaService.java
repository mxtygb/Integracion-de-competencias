package com.biblioteca.service;

import com.biblioteca.dao.LibroDAO;
import com.biblioteca.dao.PrestamoDAO;
import com.biblioteca.dao.UsuarioDAO;
import com.biblioteca.model.Libro;
import com.biblioteca.model.Prestamo;
import com.biblioteca.model.Usuario;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Capa de servicio: contiene la lógica de negocio del sistema de biblioteca.
 */
public class BibliotecaService {

    private final LibroDAO   libroDAO   = new LibroDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    // ══════════════════════════════════════════════════════════════════
    //  AUTENTICACIÓN
    // ══════════════════════════════════════════════════════════════════

    public Usuario iniciarSesion(String correo, String contrasena) {
        if (correo == null || correo.isBlank() || contrasena == null || contrasena.isBlank()) {
            System.err.println("Correo o contraseña vacíos.");
            return null;
        }
        Usuario usuario = usuarioDAO.autenticar(correo, contrasena);
        if (usuario == null) {
            System.err.println("Credenciales incorrectas.");
        }
        return usuario;
    }

    // ══════════════════════════════════════════════════════════════════
    //  GESTIÓN DE LIBROS
    // ══════════════════════════════════════════════════════════════════

    public boolean registrarLibro(Libro libro) {
        if (libro.getTitulo() == null || libro.getTitulo().isBlank()) {
            System.err.println("El título del libro no puede estar vacío.");
            return false;
        }
        return libroDAO.agregar(libro);
    }

    public boolean actualizarLibro(Libro libro) {
        if (libroDAO.buscarPorId(libro.getId()) == null) {
            System.err.println("El libro con ID " + libro.getId() + " no existe.");
            return false;
        }
        return libroDAO.actualizar(libro);
    }

    public boolean eliminarLibro(int libroId) {
        List<Prestamo> activos = prestamoDAO.listarActivos();
        boolean tienePrestamo = activos.stream()
                .anyMatch(p -> p.getLibro().getId() == libroId);
        if (tienePrestamo) {
            System.err.println("No se puede eliminar: el libro tiene préstamos activos.");
            return false;
        }
        return libroDAO.eliminar(libroId);
    }

    public List<Libro> buscarLibros(String criterio, String valor) {
        return switch (criterio.toLowerCase()) {
            case "titulo"    -> libroDAO.buscarPorTitulo(valor);
            case "autor"     -> libroDAO.buscarPorAutor(valor);
            case "categoria" -> libroDAO.buscarPorCategoria(Libro.Categoria.valueOf(valor.toUpperCase()));
            default          -> libroDAO.listarTodos();
        };
    }

    public List<Libro> listarCatalogo() {
        return libroDAO.listarTodos();
    }

    public Libro buscarLibroPorId(int id) {
        return libroDAO.buscarPorId(id);
    }

    public List<Libro> listarDisponibles() {
        return libroDAO.listarDisponibles();
    }

    // ══════════════════════════════════════════════════════════════════
    //  GESTIÓN DE USUARIOS
    // ══════════════════════════════════════════════════════════════════

    public boolean registrarUsuario(Usuario usuario) {
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            System.err.println("El nombre del usuario no puede estar vacío.");
            return false;
        }
        if (usuarioDAO.buscarPorCorreo(usuario.getCorreo()) != null) {
            System.err.println("Ya existe un usuario con ese correo.");
            return false;
        }
        return usuarioDAO.agregar(usuario);
    }

    public boolean actualizarUsuario(Usuario usuario) {
        return usuarioDAO.actualizar(usuario);
    }

    public boolean eliminarUsuario(int usuarioId) {
        List<Prestamo> activos = prestamoDAO.listarPorUsuario(usuarioId).stream()
                .filter(p -> p.getEstado() == Prestamo.EstadoPrestamo.ACTIVO)
                .collect(Collectors.toList());
        if (!activos.isEmpty()) {
            System.err.println("No se puede eliminar: el usuario tiene préstamos activos.");
            return false;
        }
        return usuarioDAO.eliminar(usuarioId);
    }

    public Usuario buscarUsuarioPorId(int id) {
        return usuarioDAO.buscarPorId(id);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioDAO.listarTodos();
    }

    // ══════════════════════════════════════════════════════════════════
    //  GESTIÓN DE PRÉSTAMOS
    // ══════════════════════════════════════════════════════════════════

    public Prestamo registrarPrestamo(int usuarioId, int libroId) {
        Usuario usuario = usuarioDAO.buscarPorId(usuarioId);
        if (usuario == null) {
            System.err.println("Usuario no encontrado.");
            return null;
        }
        Libro libro = libroDAO.buscarPorId(libroId);
        if (libro == null) {
            System.err.println("Libro no encontrado.");
            return null;
        }
        if (!libro.isDisponible()) {
            System.err.println("El libro '" + libro.getTitulo() + "' no está disponible.");
            return null;
        }

        Prestamo prestamo = new Prestamo(0, usuario, libro);
        if (prestamoDAO.agregar(prestamo)) {
            libro.reducirStock();
            libroDAO.actualizarDisponibilidad(libroId, libro.isDisponible(), libro.getStock());
            System.out.println("Préstamo registrado: " + prestamo);
            return prestamo;
        }
        return null;
    }

    public boolean registrarDevolucion(int prestamoId) {
        Prestamo prestamo = prestamoDAO.buscarPorId(prestamoId);
        if (prestamo == null) {
            System.err.println("Préstamo no encontrado.");
            return false;
        }
        if (prestamo.getEstado() == Prestamo.EstadoPrestamo.DEVUELTO) {
            System.err.println("El préstamo ya fue devuelto.");
            return false;
        }

        if (prestamoDAO.registrarDevolucion(prestamoId)) {
            Libro libro = prestamo.getLibro();
            libro.aumentarStock();
            libroDAO.actualizarDisponibilidad(libro.getId(), true, libro.getStock());
            System.out.println("Devolución registrada para préstamo ID " + prestamoId);
            return true;
        }
        return false;
    }

    public List<Prestamo> listarHistorialPrestamos() {
        return prestamoDAO.listarTodos();
    }

    public List<Prestamo> listarPrestamosActivos() {
        return prestamoDAO.listarActivos();
    }

    public List<Prestamo> listarPrestamosVencidos() {
        return prestamoDAO.listarVencidos();
    }

    public List<Prestamo> listarPrestamosPorUsuario(int usuarioId) {
        return prestamoDAO.listarPorUsuario(usuarioId);
    }

    // ══════════════════════════════════════════════════════════════════
    //  REPORTES
    // ══════════════════════════════════════════════════════════════════

    public Map<String, Object> generarReporte() {
        Map<String, Object> reporte = new HashMap<>();

        List<Libro> todosLosLibros = libroDAO.listarTodos();
        long librosDisponibles     = todosLosLibros.stream().filter(Libro::isDisponible).count();
        long librosPrestados       = todosLosLibros.stream().filter(l -> !l.isDisponible()).count();

        reporte.put("totalLibros",        todosLosLibros.size());
        reporte.put("librosDisponibles",  librosDisponibles);
        reporte.put("librosPrestados",    librosPrestados);
        reporte.put("totalUsuarios",      usuarioDAO.listarTodos().size());
        reporte.put("prestamosActivos",   prestamoDAO.contarPrestamosActivos());
        reporte.put("prestamosVencidos",  prestamoDAO.listarVencidos().size());

        // Distribución por categoría
        Map<String, Long> porCategoria = todosLosLibros.stream()
                .collect(Collectors.groupingBy(l -> l.getCategoria().name(), Collectors.counting()));
        reporte.put("librosPorCategoria", porCategoria);

        return reporte;
    }

    public void imprimirReporte() {
        Map<String, Object> r = generarReporte();
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║        REPORTE SISTEMA BIBLIOTECA    ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.printf("  Total de libros      : %s%n", r.get("totalLibros"));
        System.out.printf("  Libros disponibles   : %s%n", r.get("librosDisponibles"));
        System.out.printf("  Libros prestados     : %s%n", r.get("librosPrestados"));
        System.out.printf("  Total de usuarios    : %s%n", r.get("totalUsuarios"));
        System.out.printf("  Préstamos activos    : %s%n", r.get("prestamosActivos"));
        System.out.printf("  Préstamos vencidos   : %s%n", r.get("prestamosVencidos"));
        System.out.println("\n  Libros por categoría:");
        @SuppressWarnings("unchecked")
        Map<String, Long> categorias = (Map<String, Long>) r.get("librosPorCategoria");
        categorias.forEach((k, v) -> System.out.printf("    %-15s: %d%n", k, v));
        System.out.println("════════════════════════════════════════\n");
    }
}
