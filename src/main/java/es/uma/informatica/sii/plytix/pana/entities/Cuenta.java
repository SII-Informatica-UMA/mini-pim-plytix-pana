package es.uma.informatica.sii.plytix.pana.entities;

import java.util.Objects;
import java.util.Set;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Cuenta {
	@Id @GeneratedValue
	private Long id;
	private String nombre;
	private String isbn;
	@ElementCollection
	private Set<String> etiquetas;
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
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	public Set<String> getEtiquetas() {
		return etiquetas;
	}
	public void setEtiquetas(Set<String> etiquetas) {
		this.etiquetas = etiquetas;
	}
	@Override
	public int hashCode() {
		return Objects.hash(id);
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
	public String toString() {
		return "Book [id=" + id + ", nombre=" + nombre + ", isbn=" + isbn + "]";
	}
	
	
	

}
