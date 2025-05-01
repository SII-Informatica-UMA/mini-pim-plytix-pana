package es.uma.informatica.sii.plytix.pana.dto;

import java.util.Date;

public class CuentaDTO {
    private String nombre;
    private String direccion;
    private String nif;
    private Date fechaAlta;
    private Long planId;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public Date getFechaAlta(){
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta){
        this.fechaAlta = fechaAlta;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }
}
