package es.uma.informatica.sii.plytix.pana.entities;

import java.util.Objects;
import java.util.Set;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Cuenta {
	@Id
	private Long id;
	private String nombre;
	private String direccionFiscal;
	private String NIF;
	@Temporal(TemporalType.DATE)
	private Date fechaAlta;

	//Getters y Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDireccionFiscal() {
		return direccionFiscal;
	}

	public void setDireccionFiscal(String direccionFiscal) {
		this.direccionFiscal = direccionFiscal;
	}

	public String getNIF() {
		return NIF;
	}

	public void setNIF(String NIF) {
		this.NIF = NIF;
	}

	// Métodos adicionales
	@Override
	public String toString() {
		return "Cuenta{" +
				"id=" + id +
				", nombre='" + nombre + '\'' +
				", direccionFiscal='" + direccionFiscal + '\'' +
				", NIF='" + NIF + '\'' +
				", fechaAlta=" + fechaAlta +
				'}';
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cuenta other = (Cuenta) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}


}

