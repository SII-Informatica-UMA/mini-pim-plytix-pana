package es.uma.informatica.sii.plytix.pana.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CuentaDTO {
    private String nombre;
    private String direccion;
    private String nif;
    private Date fechaAlta;
    private Long planId;

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public void setFechaAlta(Date fechaAlta){
        this.fechaAlta = fechaAlta;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }


}
