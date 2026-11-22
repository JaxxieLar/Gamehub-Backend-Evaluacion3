package cl.duoc.gamehub.controller;

import cl.duoc.gamehub.model.Producto;
import cl.duoc.gamehub.repository.ProductoRepositorio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoRepositorio productoRepositorio;

    public ProductoController(ProductoRepositorio productoRepositorio) {
        this.productoRepositorio = productoRepositorio;
    }

    // Público: listar todos los productos
    @GetMapping
    public List<Producto> listarTodos() {
        return productoRepositorio.findAll();
    }

    // Público: obtener un producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return productoRepositorio.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("mensaje", "Producto no encontrado")));
    }

    // Solo ADMIN: crear nuevo producto en Supabase
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
        producto.setId(null);
        Producto guardado = productoRepositorio.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // Solo ADMIN: actualizar producto existente
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Producto datos) {
        return productoRepositorio.findById(id)
                .<ResponseEntity<?>>map(existente -> {
                    existente.setNombre(datos.getNombre());
                    existente.setPrecio(datos.getPrecio());
                    existente.setStock(datos.getStock());
                    existente.setCategoria(datos.getCategoria());
                    existente.setMarca(datos.getMarca());
                    existente.setImagen(datos.getImagen());
                    return ResponseEntity.ok(productoRepositorio.save(existente));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("mensaje", "Producto no encontrado")));
    }

    // Solo ADMIN: eliminar producto
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (!productoRepositorio.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Producto no encontrado"));
        }
        productoRepositorio.deleteById(id);
        return ResponseEntity.ok(Map.of("mensaje", "Producto eliminado correctamente"));
    }
}