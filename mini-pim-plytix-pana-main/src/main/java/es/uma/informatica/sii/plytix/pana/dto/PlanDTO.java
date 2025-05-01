package es.uma.informatica.sii.plytix.pana.dto;

import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import es.uma.informatica.sii.plytix.pana.entities.Plan;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

public class PlanDTO {


        private String nombre;  // Nombre del plan
        private Long maxProductos;  // Máximo número de productos permitidos
        private Long maxActivos;  // Máximo número de activos permitidos
        private Long maxAlmacenamiento;  // Límite de almacenamiento que ofrece el plan para una cuenta
        private Long maxCategoriasProductos;  // Máximo categorías de productos
        private Long maxCategoriasActivos;  // Máximo categorías de activos
        private Long maxRelaciones;  // Máximo relaciones entre entidades
        private Double precio;  // Precio del plan
        private List<Cuenta> cuentas;

        public PlanDTO() {}

        // === GETTERS Y SETTERS ===





        public String getNombre() {
            return this.nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public Long getMaxProductos() {
            return maxProductos;
        }

        public void setMaxProductos(Long maxProductos) {
            this.maxProductos = maxProductos;
        }

        public Long getMaxActivos() {
            return maxProductos;
        }

        public void setMaxActivos(Long maxActivos) {
            this.maxActivos = maxActivos;
        }

        public Long getMaxAlmacenamiento() {
            return maxAlmacenamiento;
        }

        public void setMaxAlmacenamiento(Long maxAlmacenamiento) {
            this.maxAlmacenamiento = maxAlmacenamiento;
        }

        public Long getMaxCategoriasProductos() {
            return maxCategoriasProductos;
        }

        public void setMaxCategoriasProductos(Long maxCategoriasProductos) {
            this.maxCategoriasProductos = maxCategoriasProductos;
        }

        public Long getMaxCategoriasActivos() {
            return maxCategoriasActivos;
        }

        public void setMaxCategoriasActivos(Long maxCategoriasActivos) {
            this.maxCategoriasActivos = maxCategoriasActivos;
        }

        public Long getMaxRelaciones() {
            return maxRelaciones;
        }

        public void setMaxRelaciones(Long maxRelaciones) {
            this.maxRelaciones = maxRelaciones;
        }

        public Double getPrecio() {
            return precio;
        }

        public void setPrecio(Double precio) {
            this.precio = precio;
        }

        @Override
        public String toString() {
            return "Plan{" +

                    ", nombre='" + nombre + '\'' +
                    ", maxProductos=" + maxProductos +
                    ", maxActivos=" + maxActivos +
                    ", maxAlmacenamiento=" + maxAlmacenamiento +
                    ", maxCategoriasProductos=" + maxCategoriasProductos +
                    ", maxCategoriasActivos=" + maxCategoriasActivos +
                    ", maxRelaciones=" + maxRelaciones +
                    ", precio=" + precio +
                    '}';
        }

    }


