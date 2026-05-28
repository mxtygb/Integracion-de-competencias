package com.biblioteca.dao;

import com.biblioteca.model.Libro;
import com.biblioteca.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    // ── Crear ──────────────────────────────────────────────────────────────────
    public boolean agregar(Libro libro) {
        String sql = "INSERT INTO libros (titulo, autor, editorial, anio_publicacion, isbn, categoria, disponible, stock) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getEditorial());
            ps.setInt(4, libro.getAnioPublicacion());
            ps.setString(5, libro.getIsbn());
            ps.setString(6, libro.getCategoria().name());
            ps.setBoolean(7, libro.isDisponible());
            ps.setInt(8, libro.getStock());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) libro.setId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al agregar libro: " + e.getMessage());
        }
        return false;
    }

    // ── Leer ───────────────────────────────────────────────────────────────────
    public Libro buscarPorId(int id) {
        String sql = "SELECT * FROM libros WHERE id = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapearLibro(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar libro por ID: " + e.getMessage());
        }
        return null;
    }

    public List<Libro> listarTodos() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros ORDER BY titulo";
        try (Connection conn = ConexionDB.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) libros.add(mapearLibro(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar libros: " + e.getMessage());
        }
        return libros;
    }

    public List<Libro> buscarPorTitulo(String titulo) {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE titulo LIKE ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + titulo + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) libros.add(mapearLibro(rs));
        } catch (SQLException e) {
            System.err.println("Error al buscar por título: " + e.getMessage());
        }
        return libros;
    }

    public List<Libro> buscarPorAutor(String autor) {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE autor LIKE ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + autor + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) libros.add(mapearLibro(rs));
        } catch (SQLException e) {
            System.err.println("Error al buscar por autor: " + e.getMessage());
        }
        return libros;
    }

    public List<Libro> buscarPorCategoria(Libro.Categoria categoria) {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE categoria = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoria.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) libros.add(mapearLibro(rs));
        } catch (SQLException e) {
            System.err.println("Error al buscar por categoría: " + e.getMessage());
        }
        return libros;
    }

    public List<Libro> listarDisponibles() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE disponible = true ORDER BY titulo";
        try (Connection conn = ConexionDB.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) libros.add(mapearLibro(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar disponibles: " + e.getMessage());
        }
        return libros;
    }

    // ── Actualizar ────────────────────────────────────────────────────────────
    public boolean actualizar(Libro libro) {
        String sql = "UPDATE libros SET titulo=?, autor=?, editorial=?, anio_publicacion=?, "
                   + "isbn=?, categoria=?, disponible=?, stock=? WHERE id=?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getEditorial());
            ps.setInt(4, libro.getAnioPublicacion());
            ps.setString(5, libro.getIsbn());
            ps.setString(6, libro.getCategoria().name());
            ps.setBoolean(7, libro.isDisponible());
            ps.setInt(8, libro.getStock());
            ps.setInt(9, libro.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar libro: " + e.getMessage());
        }
        return false;
    }

    public boolean actualizarDisponibilidad(int libroId, boolean disponible, int stock) {
        String sql = "UPDATE libros SET disponible=?, stock=? WHERE id=?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, disponible);
            ps.setInt(2, stock);
            ps.setInt(3, libroId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar disponibilidad: " + e.getMessage());
        }
        return false;
    }

    // ── Eliminar ──────────────────────────────────────────────────────────────
    public boolean eliminar(int id) {
        String sql = "DELETE FROM libros WHERE id=?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar libro: " + e.getMessage());
        }
        return false;
    }

    // ── Mapeo ──────────────────────────────────────────────────────────────────
    private Libro mapearLibro(ResultSet rs) throws SQLException {
        Libro libro = new Libro();
        libro.setId(rs.getInt("id"));
        libro.setTitulo(rs.getString("titulo"));
        libro.setAutor(rs.getString("autor"));
        libro.setEditorial(rs.getString("editorial"));
        libro.setAnioPublicacion(rs.getInt("anio_publicacion"));
        libro.setIsbn(rs.getString("isbn"));
        libro.setCategoria(Libro.Categoria.valueOf(rs.getString("categoria")));
        libro.setDisponible(rs.getBoolean("disponible"));
        libro.setStock(rs.getInt("stock"));
        return libro;
    }
}
