package cl.duoc.gamehub.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "detalle_ordenes")
public class DetalleOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    @JsonBackReference
    private Orden orden;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Long precioUnitario;

    public DetalleOrden() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Orden getOrden() { return orden; }
    public void setOrden(Orden orden) { this.orden = orden; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Long getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Long precioUnitario) { this.precioUnitario = precioUnitario; }
}