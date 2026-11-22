package cl.duoc.gamehub.controller;

import cl.duoc.gamehub.dto.CrearOrdenRequest;
import cl.duoc.gamehub.model.DetalleOrden;
import cl.duoc.gamehub.model.Orden;
import cl.duoc.gamehub.model.Producto;
import cl.duoc.gamehub.model.Usuario;
import cl.duoc.gamehub.repository.OrdenRepositorio;
import cl.duoc.gamehub.repository.ProductoRepositorio;
import cl.duoc.gamehub.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    private final OrdenRepositorio ordenRepositorio;
    private final ProductoRepositorio productoRepositorio;
    private final UsuarioRepository usuarioRepositorio;

    public OrdenController(
            OrdenRepositorio ordenRepositorio,
            ProductoRepositorio productoRepositorio,
            UsuarioRepository usuarioRepositorio) {
        this.ordenRepositorio = ordenRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    // Obtener órdenes (ADMIN ve todas; USER ve solo las suyas)
    @GetMapping
    public ResponseEntity<?> listarOrdenes(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Debes iniciar sesión"));
        }

        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<Orden> todas = ordenRepositorio.findAll();

        List<Orden> resultado = todas.stream()
                .filter(o -> esAdmin || (o.getUsuario() != null && auth.getName().equalsIgnoreCase(o.getUsuario().getEmail())))
                .sorted(Comparator.comparing(Orden::getFecha, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        return ResponseEntity.ok(resultado);
    }

    // Crear nueva orden de compra y descontar stock
    @PostMapping
    @Transactional
    public ResponseEntity<?> crearOrden(@Valid @RequestBody CrearOrdenRequest request, Authentication auth) {
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Debes iniciar sesión para comprar"));
        }

        Usuario usuario = usuarioRepositorio.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setFecha(LocalDateTime.now());
        orden.setEstado("COMPLETADA");

        long totalCalculado = 0L;

        for (CrearOrdenRequest.ItemOrden item : request.getItems()) {
            Producto producto = productoRepositorio.findById(item.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: ID " + item.getProductoId()));

            if (producto.getStock() < item.getCantidad()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "mensaje", "Stock insuficiente para " + producto.getNombre()
                ));
            }

            // Descontar stock y guardar
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepositorio.save(producto);

            DetalleOrden detalle = new DetalleOrden();
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            orden.agregarDetalle(detalle);

            totalCalculado += producto.getPrecio() * item.getCantidad();
        }

        orden.setTotal(totalCalculado);
        Orden guardada = ordenRepositorio.save(orden);

        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }
}