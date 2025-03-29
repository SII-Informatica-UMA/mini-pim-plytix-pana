package es.uma.informatica.sii.plytix.pana.entities;

import java.util.*;
import jakarta.persistence.*;

@Entity
public class Cuenta {
    @Id
    private Long id;
    private String nombre;
    @Column(name = "DIR_FISCAL")
    private String direccionFiscal;
    private String NIF;
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_ALTA")
    private Date fechaAlta;
    @OneToOne
    @JoinColumn(name = "plan")
    private Plan plan;
    @OneToMany(mappedBy = "cuenta")
    private List<Usuario> usuarios;

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
