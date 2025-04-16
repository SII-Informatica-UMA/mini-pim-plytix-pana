package es.uma.informatica.sii.plytix.pana.entities;

import java.util.*;
import jakarta.persistence.*;

/**
 * Entidad que representa una cuenta de usuario en el sistema.
 * Contiene información fiscal y de suscripción del cliente.
 */
@Entity
public class Cuenta {
	@Id  // Clave primaria de la entidad
	private Long id;

	private String nombre;  // Nombre de la cuenta

	@Column(name = "DIR_FISCAL")  // Nombre personalizado en BD
	private String direccionFiscal;  // Dirección fiscal para facturación

	private String NIF;  // Número de identificación fiscal

	@Temporal(TemporalType.DATE)  // Indica que solo se almacena la fecha (sin hora)
	@Column(name = "FECHA_ALTA")
	private Date fechaAlta;  // Fecha de alta en el sistema

	// Relación muchos-a-uno con Plan. Muchas cuentas pueden tener el mismo plan
	@ManyToOne
	@JoinColumn(name = "plan", foreignKey = @ForeignKey(name = "fk_cuenta_plan"))
	private Plan plan;  // Plan asociado a esta cuenta

	// Lista de IDs de usuarios asociados a esta cuenta
	@ElementCollection
	private List<Long> usuarios;

	// Relación propietario de la cuenta
	private Long duenoId;

	// === GETTERS Y SETTERS ===
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

	public long getDuenoId(){
		return duenoId;
	}

	public void setDuenoId(long duenoId){
		this.duenoId=duenoId;
	}

	public List<Long> getUsuarios(){
		return usuarios;
	}

	public void setUsuarios(List<Long> usuarios){
		this.usuarios=usuarios;
	}

	// Representación textual de la cuenta
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

	// Comparación por ID
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

	// HashCode basado en ID
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
