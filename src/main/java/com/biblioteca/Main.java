package com.biblioteca;

import com.biblioteca.model.Libro;
import com.biblioteca.model.Prestamo;
import com.biblioteca.model.Usuario;
import com.biblioteca.service.BibliotecaService;

import java.util.List;
import java.util.Scanner;

/**
 * Punto de entrada del Sistema de Gestión de Biblioteca.
 * Menú de consola interactivo para administradores.
 */
public class Main {

    private static final BibliotecaService servicio = new BibliotecaService();
    private static final Scanner scanner = new Scanner(System.in);
    private static Usuario sesionActual = null;

    public static void main(String[] args) {
        System.out.println("══════════════════════════════════════════");
        System.out.println("   SISTEMA DE GESTIÓN DE BIBLIOTECA v1.0  ");
        System.out.println("══════════════════════════════════════════");

        iniciarSesion();

        if (sesionActual != null) {
            menuPrincipal();
        }

        System.out.println("¡Hasta luego!");
    }

    // ══════════════════════════════════════════════════════════════════
    //  AUTENTICACIÓN
    // ══════════════════════════════════════════════════════════════════

    private static void iniciarSesion() {
        int intentos = 0;
        while (intentos < 3) {
            System.out.print("\nCorreo   : ");
            String correo = scanner.nextLine().trim();
            System.out.print("Contraseña: ");
            String clave = scanner.nextLine().trim();

            sesionActual = servicio.iniciarSesion(correo, clave);
            if (sesionActual != null) {
                System.out.println("\n✓ Bienvenido/a, " + sesionActual.getNombre()
                        + " [" + sesionActual.getTipoUsuario() + "]");
                return;
            }
            System.out.println("✗ Credenciales incorrectas. Intento " + (++intentos) + "/3");
        }
        System.out.println("Demasiados intentos fallidos. Cerrando.");
    }

    // ══════════════════════════════════════════════════════════════════
    //  MENÚ PRINCIPAL
    // ══════════════════════════════════════════════════════════════════

    private static void menuPrincipal() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n──── MENÚ PRINCIPAL ────────────────────");
            System.out.println("  1. Gestión de Libros");
            System.out.println("  2. Gestión de Usuarios");
            System.out.println("  3. Gestión de Préstamos");
            System.out.println("  4. Ver Reportes");
            System.out.println("  0. Salir");
            System.out.print("Opción: ");

            switch (leerOpcion()) {
                case 1  -> menuLibros();
                case 2  -> menuUsuarios();
                case 3  -> menuPrestamos();
                case 4  -> servicio.imprimirReporte();
                case 0  -> continuar = false;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  MENÚ LIBROS
    // ══════════════════════════════════════════════════════════════════

    private static void menuLibros() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n──── LIBROS ────────────────────────────");
            System.out.println("  1. Agregar libro");
            System.out.println("  2. Editar libro");
            System.out.println("  3. Ver catálogo completo");
            System.out.println("  4. Ver libros disponibles");
            System.out.println("  5. Buscar libro");
            System.out.println("  6. Eliminar libro");
            System.out.println("  0. Volver");
            System.out.print("Opción: ");

            switch (leerOpcion()) {
                case 1 -> agregarLibro();
                case 2 -> editarLibro();
                case 3 -> mostrarLibros(servicio.listarCatalogo());
                case 4 -> mostrarLibros(servicio.listarDisponibles());
                case 5 -> buscarLibro();
                case 6 -> eliminarLibro();
                case 0 -> continuar = false;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private static void agregarLibro() {
        System.out.println("\n── Agregar Libro ──");
        Libro libro = new Libro();
        System.out.print("Título     : "); libro.setTitulo(scanner.nextLine());
        System.out.print("Autor      : "); libro.setAutor(scanner.nextLine());
        System.out.print("Editorial  : "); libro.setEditorial(scanner.nextLine());
        System.out.print("Año        : "); libro.setAnioPublicacion(leerOpcion());
        System.out.print("ISBN       : "); libro.setIsbn(scanner.nextLine());
        System.out.print("Stock      : "); libro.setStock(leerOpcion());
        libro.setCategoria(leerCategoria());

        System.out.println(servicio.registrarLibro(libro)
                ? "✓ Libro agregado correctamente." : "✗ Error al agregar el libro.");
    }

    private static void editarLibro() {
        System.out.print("\nID del libro a editar: ");
        int id = leerOpcion();
        Libro libro = servicio.buscarLibroPorId(id);
        if (libro == null) {
            System.out.println("✗ Libro no encontrado.");
            return;
        }
        System.out.println("Deje en blanco para mantener el valor actual.");
        System.out.print("Título     [" + libro.getTitulo() + "]: ");
        String titulo = scanner.nextLine();
        if (!titulo.isBlank()) libro.setTitulo(titulo);

        System.out.print("Autor      [" + libro.getAutor() + "]: ");
        String autor = scanner.nextLine();
        if (!autor.isBlank()) libro.setAutor(autor);

        System.out.print("Editorial  [" + libro.getEditorial() + "]: ");
        String editorial = scanner.nextLine();
        if (!editorial.isBlank()) libro.setEditorial(editorial);

        System.out.print("Año        [" + libro.getAnioPublicacion() + "]: ");
        String anio = scanner.nextLine();
        if (!anio.isBlank()) {
            try { libro.setAnioPublicacion(Integer.parseInt(anio.trim())); }
            catch (NumberFormatException ignored) {}
        }

        System.out.print("ISBN       [" + libro.getIsbn() + "]: ");
        String isbn = scanner.nextLine();
        if (!isbn.isBlank()) libro.setIsbn(isbn);

        System.out.print("Stock      [" + libro.getStock() + "]: ");
        String stock = scanner.nextLine();
        if (!stock.isBlank()) {
            try { libro.setStock(Integer.parseInt(stock.trim())); }
            catch (NumberFormatException ignored) {}
        }

        System.out.print("Categoría  [" + libro.getCategoria() + "] (Enter para mantener): ");
        String cat = scanner.nextLine().trim();
        if (!cat.isBlank()) {
            try { libro.setCategoria(Libro.Categoria.valueOf(cat.toUpperCase())); }
            catch (IllegalArgumentException e) { System.out.println("Categoría inválida, se mantiene la actual."); }
        }

        System.out.println(servicio.actualizarLibro(libro)
                ? "✓ Libro actualizado." : "✗ Error al actualizar el libro.");
    }

    private static void buscarLibro() {
        System.out.println("\nBuscar por: 1) Título  2) Autor  3) Categoría");
        System.out.print("Opción: ");
        int op = leerOpcion();
        String criterio = switch (op) { case 1 -> "titulo"; case 2 -> "autor"; default -> "categoria"; };
        System.out.print("Valor: ");
        String valor = scanner.nextLine();
        mostrarLibros(servicio.buscarLibros(criterio, valor));
    }

    private static void eliminarLibro() {
        System.out.print("\nID del libro a eliminar: ");
        int id = leerOpcion();
        System.out.println(servicio.eliminarLibro(id)
                ? "✓ Libro eliminado." : "✗ No se pudo eliminar.");
    }

    private static Libro.Categoria leerCategoria() {
        System.out.println("Categoría (CIENCIA, HISTORIA, LITERATURA, TECNOLOGIA, MATEMATICAS, ARTE, OTRO):");
        System.out.print("→ ");
        try {
            return Libro.Categoria.valueOf(scanner.nextLine().toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            System.out.println("Categoría no reconocida, se usará OTRO.");
            return Libro.Categoria.OTRO;
        }
    }

    private static void mostrarLibros(List<Libro> libros) {
        if (libros.isEmpty()) { System.out.println("No se encontraron libros."); return; }
        System.out.printf("%n%-4s %-35s %-20s %-12s %-5s %-10s%n",
                "ID", "TÍTULO", "AUTOR", "CATEGORÍA", "STOCK", "DISPONIBLE");
        System.out.println("─".repeat(90));
        libros.forEach(l -> System.out.printf("%-4d %-35s %-20s %-12s %-5d %-10s%n",
                l.getId(), truncar(l.getTitulo(), 34), truncar(l.getAutor(), 19),
                l.getCategoria(), l.getStock(), l.isDisponible() ? "SÍ" : "NO"));
    }

    // ══════════════════════════════════════════════════════════════════
    //  MENÚ USUARIOS
    // ══════════════════════════════════════════════════════════════════

    private static void menuUsuarios() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n──── USUARIOS ──────────────────────────");
            System.out.println("  1. Registrar usuario");
            System.out.println("  2. Editar usuario");
            System.out.println("  3. Listar usuarios");
            System.out.println("  4. Eliminar usuario");
            System.out.println("  0. Volver");
            System.out.print("Opción: ");

            switch (leerOpcion()) {
                case 1 -> registrarUsuario();
                case 2 -> editarUsuario();
                case 3 -> mostrarUsuarios(servicio.listarUsuarios());
                case 4 -> eliminarUsuario();
                case 0 -> continuar = false;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private static void registrarUsuario() {
        System.out.println("\n── Registrar Usuario ──");
        Usuario u = new Usuario();
        System.out.print("Nombre    : "); u.setNombre(scanner.nextLine());
        System.out.print("Correo    : "); u.setCorreo(scanner.nextLine());
        System.out.print("Contraseña: "); u.setContrasena(scanner.nextLine());
        System.out.println("Tipo (ADMINISTRADOR, ESTUDIANTE, DOCENTE):");
        System.out.print("→ ");
        try {
            u.setTipoUsuario(Usuario.TipoUsuario.valueOf(scanner.nextLine().toUpperCase().trim()));
        } catch (IllegalArgumentException e) {
            u.setTipoUsuario(Usuario.TipoUsuario.ESTUDIANTE);
            System.out.println("Tipo no reconocido, se asignará ESTUDIANTE.");
        }
        System.out.println(servicio.registrarUsuario(u)
                ? "✓ Usuario registrado." : "✗ Error al registrar usuario.");
    }

    private static void editarUsuario() {
        System.out.print("\nID del usuario a editar: ");
        int id = leerOpcion();
        Usuario u = servicio.buscarUsuarioPorId(id);
        if (u == null) {
            System.out.println("✗ Usuario no encontrado.");
            return;
        }
        System.out.println("Deje en blanco para mantener el valor actual.");
        System.out.print("Nombre    [" + u.getNombre() + "]: ");
        String nombre = scanner.nextLine();
        if (!nombre.isBlank()) u.setNombre(nombre);

        System.out.print("Correo    [" + u.getCorreo() + "]: ");
        String correo = scanner.nextLine();
        if (!correo.isBlank()) u.setCorreo(correo);

        System.out.print("Contraseña (Enter para mantener): ");
        String clave = scanner.nextLine();
        if (!clave.isBlank()) u.setContrasena(clave);

        System.out.print("Tipo      [" + u.getTipoUsuario() + "] (ADMINISTRADOR/ESTUDIANTE/DOCENTE, Enter para mantener): ");
        String tipo = scanner.nextLine().trim();
        if (!tipo.isBlank()) {
            try { u.setTipoUsuario(Usuario.TipoUsuario.valueOf(tipo.toUpperCase())); }
            catch (IllegalArgumentException e) { System.out.println("Tipo inválido, se mantiene el actual."); }
        }

        System.out.println(servicio.actualizarUsuario(u)
                ? "✓ Usuario actualizado." : "✗ Error al actualizar usuario.");
    }

    private static void eliminarUsuario() {
        System.out.print("\nID del usuario a eliminar: ");
        int id = leerOpcion();
        System.out.println(servicio.eliminarUsuario(id)
                ? "✓ Usuario eliminado." : "✗ No se pudo eliminar.");
    }

    private static void mostrarUsuarios(List<Usuario> usuarios) {
        if (usuarios.isEmpty()) { System.out.println("No hay usuarios registrados."); return; }
        System.out.printf("%n%-4s %-30s %-30s %-15s%n", "ID", "NOMBRE", "CORREO", "TIPO");
        System.out.println("─".repeat(82));
        usuarios.forEach(u -> System.out.printf("%-4d %-30s %-30s %-15s%n",
                u.getId(), truncar(u.getNombre(), 29), truncar(u.getCorreo(), 29), u.getTipoUsuario()));
    }

    // ══════════════════════════════════════════════════════════════════
    //  MENÚ PRÉSTAMOS
    // ══════════════════════════════════════════════════════════════════

    private static void menuPrestamos() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n──── PRÉSTAMOS ─────────────────────────");
            System.out.println("  1. Registrar préstamo");
            System.out.println("  2. Registrar devolución");
            System.out.println("  3. Ver préstamos activos");
            System.out.println("  4. Ver historial completo");
            System.out.println("  5. Ver préstamos vencidos");
            System.out.println("  0. Volver");
            System.out.print("Opción: ");

            switch (leerOpcion()) {
                case 1 -> registrarPrestamo();
                case 2 -> registrarDevolucion();
                case 3 -> mostrarPrestamos(servicio.listarPrestamosActivos());
                case 4 -> mostrarPrestamos(servicio.listarHistorialPrestamos());
                case 5 -> mostrarPrestamos(servicio.listarPrestamosVencidos());
                case 0 -> continuar = false;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private static void registrarPrestamo() {
        System.out.println("\n── Registrar Préstamo ──");
        System.out.print("ID de usuario : "); int uid = leerOpcion();
        System.out.print("ID de libro   : "); int lid = leerOpcion();
        Prestamo p = servicio.registrarPrestamo(uid, lid);
        System.out.println(p != null
                ? "✓ Préstamo registrado (ID " + p.getId() + ")" : "✗ No se pudo registrar el préstamo.");
    }

    private static void registrarDevolucion() {
        System.out.print("\nID del préstamo a devolver: ");
        int id = leerOpcion();
        System.out.println(servicio.registrarDevolucion(id)
                ? "✓ Devolución registrada." : "✗ No se pudo registrar la devolución.");
    }

    private static void mostrarPrestamos(List<Prestamo> prestamos) {
        if (prestamos.isEmpty()) { System.out.println("No hay préstamos para mostrar."); return; }
        System.out.printf("%n%-4s %-20s %-25s %-12s %-12s %-10s%n",
                "ID", "USUARIO", "LIBRO", "PRESTADO", "VENCE", "ESTADO");
        System.out.println("─".repeat(87));
        prestamos.forEach(p -> System.out.printf("%-4d %-20s %-25s %-12s %-12s %-10s%n",
                p.getId(), truncar(p.getUsuario().getNombre(), 19),
                truncar(p.getLibro().getTitulo(), 24),
                p.getFechaPrestamo(), p.getFechaDevolucionEsperada(), p.getEstado()));
    }

    // ══════════════════════════════════════════════════════════════════
    //  UTILIDADES
    // ══════════════════════════════════════════════════════════════════

    private static int leerOpcion() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String truncar(String texto, int max) {
        if (texto == null) return "";
        return texto.length() > max ? texto.substring(0, max - 1) + "…" : texto;
    }
}
