package model.viewModel;

import java.math.BigDecimal;
import java.util.Date;

public class PedidoView {
    private int id;
    private int idCliente;
    private String nombre;
    private Date fecha;
    private BigDecimal total;
    private String estado;
    public PedidoView(int id, int idCliente, String nombre, Date fecha, BigDecimal total, String estado) {
        this.id = id;
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
    }

    public PedidoView(int idCliente, String nombre, Date fecha, BigDecimal total, String estado) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
    }
    // Métodos getter y setter
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getIdCliente() {
        return idCliente;
    }
    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }
    public Date getFecha() {
        return fecha;
    }
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    public BigDecimal getTotal() {
        return total;
    }
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}