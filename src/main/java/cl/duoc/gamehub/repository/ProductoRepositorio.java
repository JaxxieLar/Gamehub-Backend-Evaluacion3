package cl.duoc.gamehub.repository;

import cl.duoc.gamehub.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepositorio extends JpaRepository<Producto, Long> {
}