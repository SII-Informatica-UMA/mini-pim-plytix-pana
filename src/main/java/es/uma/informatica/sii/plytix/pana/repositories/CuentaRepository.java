package es.uma.informatica.sii.plytix.pana.repositories;

import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    List<Cuenta> findByNombre(String nombre);
    List<Cuenta> findByIdUsuario(Long id);
    List<Cuenta> findByIdPlan(Long id);
    boolean existsByPlanId(Long planId);
}
