package es.uma.informatica.sii.plytix.pana.entities;

import java.util.Objects;
import java.util.Set;
import jakarta.persistence.*;

@Entity
public class Usuario {
	@Id
	private Long id;
	private String email;
	private String nombre;

	private String apellido1;

	private String apellido2;
	@Enumerated(EnumType.STRING)
	private Role role;
	@ManyToOne
	@JoinColumn(name = "cuenta")
	private Cuenta cuenta;


	public Long getID(){
		return id;
	}

	public String getEmail(){
		return email;
	}

	public String getApellido1(){
		return apellido1;
	}

	public String getApellido2(){
		return apellido2;
	}

	public void setId(Long id){
		this.id=id;
	}

	public Role getRole(){
		return role;
	}

	public void setNombre(String nombre){
		this.nombre=nombre;
	}

	public void setEmail(String email){
		this.email=email;
	}

	public void setApellido1(String apellido1){
		this.apellido1=apellido1;
	}

	public void setApellido2(String apellido2){
		this.apellido2=apellido2;
	}

	public void setRole(Role role){
		this.role=role;
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
		Usuario other = (Usuario) obj;
		return Objects.equals(id, other.id);
	}
	@Override
	public String toString() {
		return "Usuario [id=" + id + ", email= " + email + ", nombre=" + nombre + ", apellido1= " + apellido1+ ", apellido2= " +apellido2+ ", role= "+role+ "]";
	}
	
	
	

}
