package es.uma.informatica.sii.plytix.pana.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.uma.informatica.sii.plytix.pana.entities.Cuenta;

public interface BookRepository extends JpaRepository<Cuenta, Long> {
	List<Cuenta> findByNombre(String title);
	List<Cuenta> findByIsbnAndNombreOrderByNombreAsc(String isbn, String nombre);
	
	@Query("select b from Book b where b.nombre = :nombre")
	List<Cuenta> miConsultaCompleja(@Param("nombre") String nombre);
}
