package es.uma.informatica.sii.plytix.pana.dto;

import es.uma.informatica.sii.plytix.pana.entities.Plan;

import java.util.Date;
import java.util.List;

public class CuentaDTO {



        private String nombre;  // Nombre de la cuenta

        private String direccionFiscal;  // Dirección fiscal para facturación

        private String nif;  // Número de identificación fiscal


        private Date fechaAlta;  // Fecha de alta en el sistema

        private Plan plan;  // Plan asociado a esta cuenta

        // Lista de IDs de usuarios asociados a esta cuenta
        private List<Long> usuarios;

        // Relación propietario de la cuenta
        private Long duenoId;

        // === GETTERS Y SETTERS ===


        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }
        public Date getFechaAlta(){return fechaAlta;}
        public void setFechaAlta(Date fechAlta){this.fechaAlta = fechAlta;}
        public Plan getPlan(){return plan;}
        public void setPlan (Plan p){this.plan = p;}

        public String getDireccionFiscal() {
            return direccionFiscal;
        }

        public void setDireccionFiscal(String direccionFiscal) {
            this.direccionFiscal = direccionFiscal;
        }

        public String getnif() {
            return nif;
        }

        public void setnif(String nif) {
            this.nif = nif;
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
            return "CuentaDTO{" +

                    ", nombre='" + nombre + '\'' +
                    ", direccionFiscal='" + direccionFiscal + '\'' +
                    ", NIF='" + nif + '\'' +
                    ", fechaAlta=" + fechaAlta +
                    '}';
        }
    }

