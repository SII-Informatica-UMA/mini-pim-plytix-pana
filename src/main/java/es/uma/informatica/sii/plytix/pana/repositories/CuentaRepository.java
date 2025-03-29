package es.uma.informatica.sii.plytix.pana.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.uma.informatica.sii.plytix.pana.entities.Cuenta;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

}
