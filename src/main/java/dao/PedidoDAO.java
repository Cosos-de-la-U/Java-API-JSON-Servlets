package dao;

import model.Pedido;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class PedidoDAO {
    private Connection con;
    public PedidoDAO(Connection con) {
        this.con = con;
    }
    public List<Pedido> listar() throws SQLException {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT id, id_cliente, fecha, total, estado FROM pedidos";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int idCliente = rs.getInt("id_cliente");
                    Date fecha = rs.getDate("fecha");
                    BigDecimal total = rs.getBigDecimal("total");
                    String estado = rs.getString("estado");
                    Pedido p = new Pedido(id, idCliente, fecha, total, estado);
                    pedidos.add(p);
                }
            }
        }
        return pedidos;
    }
    public Pedido buscarPorId(int id) throws SQLException {
        Pedido pedido = null;
        String sql = "SELECT id, id_cliente, fecha, total, estado FROM pedidos WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idCliente = rs.getInt("id_cliente");
                    Date fecha = rs.getDate("fecha");
                    BigDecimal total = rs.getBigDecimal("total");
                    String estado = rs.getString("estado");
                    pedido = new Pedido(id, idCliente, fecha, total, estado);
                }
            }
        }
        return pedido;
    }
    public void insertar(Pedido pedido) throws SQLException {
        String sql = "INSERT INTO pedidos (id_cliente, fecha, total, estado) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pedido.getIdCliente());
            ps.setDate(2, new java.sql.Date(pedido.getFecha().getTime()));
            ps.setBigDecimal(3, pedido.getTotal());
            ps.setString(4, pedido.getEstado());
            ps.executeUpdate();
        }
    }
    public void actualizar(Pedido pedido) throws SQLException {
        String sql = "UPDATE pedidos SET id_cliente = ?, fecha = ?, total = ?, estado = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pedido.getIdCliente());
            ps.setDate(2, new java.sql.Date(pedido.getFecha().getTime()));
            ps.setBigDecimal(3, pedido.getTotal());
            ps.setString(4, pedido.getEstado());
            ps.setInt(5, pedido.getId());
            ps.executeUpdate();
        }
    }
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM pedidos WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}