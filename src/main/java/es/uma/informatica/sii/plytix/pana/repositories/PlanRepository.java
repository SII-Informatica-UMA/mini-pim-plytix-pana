package es.uma.informatica.sii.plytix.pana.repositories;

import java.util.List;

import es.uma.informatica.sii.plytix.pana.entities.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findByNombre(String nombre);
}
