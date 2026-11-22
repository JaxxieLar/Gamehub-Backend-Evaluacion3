package cl.duoc.gamehub.repository;

import cl.duoc.gamehub.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenRepositorio extends JpaRepository<Orden, Long> {
}