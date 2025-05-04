package es.uma.informatica.sii.plytix.pana.repositories;

import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    List<Cuenta> findByNombre(String nombre);
    List<Cuenta> findByPlanId(Long planId);
    List<Cuenta> findByUsuariosContaining(Long usuarioId);
    List<Cuenta> findByDuenoId(Long duenoId);

    boolean existsByPlan_Id(Long planId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Cuenta c WHERE c.id = :cuentaId AND " +
            "(c.duenoId = :usuarioId OR :usuarioId MEMBER OF c.usuarios)")
    boolean existsByIdAndDuenoIdOrUsuariosContaining(
            @Param("cuentaId") Long cuentaId,
            @Param("usuarioId") Long usuarioId);



}
