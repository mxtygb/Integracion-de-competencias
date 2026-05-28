package com.biblioteca.dao;

import com.biblioteca.model.Prestamo;
import com.biblioteca.model.Usuario;
import com.biblioteca.model.Libro;
import com.biblioteca.util.ConexionDB;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final LibroDAO libroDAO = new LibroDAO();

    public boolean agregar(Prestamo prestamo) {
        String sql = "INSERT INTO prestamos (usuario_id, libro_id, fecha_prestamo, fecha_devolucion_esperada, estado) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, prestamo.getUsuario().getId());
            ps.setInt(2, prestamo.getLibro().getId());
            ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            ps.setDate(4, Date.valueOf(prestamo.getFechaDevolucionEsperada()));
            ps.setString(5, prestamo.getEstado().name());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) prestamo.setId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar préstamo: " + e.getMessage());
        }
        return false;
    }

    public Prestamo buscarPorId(int id) {
        String sql = "SELECT * FROM prestamos WHERE id = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapearPrestamo(rs);
        } catch (SQLException e) {
            System.err.println("Error al buscar préstamo: " + e.getMessage());
        }
        return null;
    }

    public List<Prestamo> listarTodos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamos ORDER BY fecha_prestamo DESC";
        try (Connection conn = ConexionDB.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) prestamos.add(mapearPrestamo(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar préstamos: " + e.getMessage());
        }
        return prestamos;
    }

    public List<Prestamo> listarPorUsuario(int usuarioId) {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamos WHERE usuario_id = ? ORDER BY fecha_prestamo DESC";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) prestamos.add(mapearPrestamo(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar préstamos por usuario: " + e.getMessage());
        }
        return prestamos;
    }

    public List<Prestamo> listarActivos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamos WHERE estado = 'ACTIVO' ORDER BY fecha_devolucion_esperada";
        try (Connection conn = ConexionDB.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) prestamos.add(mapearPrestamo(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar préstamos activos: " + e.getMessage());
        }
        return prestamos;
    }

    public List<Prestamo> listarVencidos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamos WHERE estado = 'ACTIVO' AND fecha_devolucion_esperada < CURDATE()";
        try (Connection conn = ConexionDB.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) prestamos.add(mapearPrestamo(rs));
        } catch (SQLException e) {
            System.err.println("Error al listar préstamos vencidos: " + e.getMessage());
        }
        return prestamos;
    }

    public boolean registrarDevolucion(int prestamoId) {
        String sql = "UPDATE prestamos SET estado='DEVUELTO', fecha_devolucion_real=? WHERE id=?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setInt(2, prestamoId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar devolución: " + e.getMessage());
        }
        return false;
    }

    public int contarPrestamosActivos() {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE estado = 'ACTIVO'";
        try (Connection conn = ConexionDB.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error al contar préstamos: " + e.getMessage());
        }
        return 0;
    }

    private Prestamo mapearPrestamo(ResultSet rs) throws SQLException {
        Prestamo p = new Prestamo();
        p.setId(rs.getInt("id"));
        p.setUsuario(usuarioDAO.buscarPorId(rs.getInt("usuario_id")));
        p.setLibro(libroDAO.buscarPorId(rs.getInt("libro_id")));
        p.setFechaPrestamo(rs.getDate("fecha_prestamo").toLocalDate());
        p.setFechaDevolucionEsperada(rs.getDate("fecha_devolucion_esperada").toLocalDate());
        Date devReal = rs.getDate("fecha_devolucion_real");
        if (devReal != null) p.setFechaDevolucionReal(devReal.toLocalDate());
        p.setEstado(Prestamo.EstadoPrestamo.valueOf(rs.getString("estado")));
        return p;
    }
}
