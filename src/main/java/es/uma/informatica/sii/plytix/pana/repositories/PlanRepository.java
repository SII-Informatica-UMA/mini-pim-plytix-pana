package es.uma.informatica.sii.plytix.pana.repositories;

import java.util.List;

import es.uma.informatica.sii.plytix.pana.entities.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findByIdPlan(Long id);
    List<Plan> findByNombre(String nombre);
}
