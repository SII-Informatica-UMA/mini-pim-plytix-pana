package es.uma.informatica.sii.plytix.pana.repositories;

import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    List<Cuenta> findByNombre(String nombre);
    List<Cuenta> findByDuenoId(Long id);
    List<Cuenta> findByPlanId(Long duenoId);
    boolean existsByPlan_Id(Long planId);
}
