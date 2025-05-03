package es.uma.informatica.sii.plytix.pana.repositories;

import java.util.List;
import java.util.Optional;

import es.uma.informatica.sii.plytix.pana.entities.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findById(Long id);

    // Si necesitas lista (aunque findById ya devuelve Optional)
    List<Plan> findAllById(Long id);

    List<Plan> findByNombre(String nombre);
}

