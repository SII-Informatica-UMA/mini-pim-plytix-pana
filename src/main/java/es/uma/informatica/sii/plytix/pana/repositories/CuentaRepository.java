package es.uma.informatica.sii.plytix.pana.repositories;

import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    List<Cuenta> findByNombre(String nombre);

    // Para buscar por ID de plan
    List<Cuenta> findByPlanId(Long id);

    // Para buscar cuentas donde el usuario es dueño
    List<Cuenta> findByDuenoId(Long id);

    // Para buscar cuentas que contengan un usuario en su lista
    @Query("SELECT c FROM Cuenta c WHERE :usuarioId MEMBER OF c.usuarios")
    List<Cuenta> findByUsuarioInUsuarios(@Param("usuarioId") Long usuarioId);

}
