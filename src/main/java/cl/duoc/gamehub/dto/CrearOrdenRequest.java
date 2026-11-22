package cl.duoc.gamehub.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CrearOrdenRequest {

    @NotEmpty(message = "La orden debe contener al menos un producto")
    @Valid
    private List<ItemOrden> items;

    public List<ItemOrden> getItems() { return items; }
    public void setItems(List<ItemOrden> items) { this.items = items; }

    public static class ItemOrden {

        @NotNull(message = "El ID del producto es obligatorio")
        private Long productoId;

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        private Integer cantidad;

        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }

        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    }
}