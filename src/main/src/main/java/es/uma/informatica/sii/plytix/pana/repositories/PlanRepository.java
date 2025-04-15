package es.uma.informatica.sii.plytix.pana.repositories;

import java.util.List;

import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import es.uma.informatica.sii.plytix.pana.entities.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Cuenta> FindByNombre (String nombre);
}
