package es.uma.informatica.sii.plytix.pana.repositories;

import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

}
