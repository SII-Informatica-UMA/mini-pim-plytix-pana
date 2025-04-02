package es.uma.informatica.sii.plytix.pana.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Plan {
    @Id
    private Long id;
    private String nombre;

    @Column(name = "MAX_PRODUCTOS")
    private Long maxProductos;

    @Column(name = "MAX_ACTIVOS")
    private Long maxActivos;

    @Column(name = "MAX_ALMACENAMIENTO")
    private Long maxAlmacenamiento;

    @Column(name = "MAX_CATEGORIAS_PRODUCTOS")
    private Long maxCategoriasProductos;

    @Column(name = "MAX_CATEGORIAS_ACTIVOS")
    private Long maxCategoriasActivos;

    @Column(name = "MAX_RELACIONES")
    private Long maxRelaciones;

    private Double precio;

    @OneToMany (mappedBy = "plan")
    private List<Cuenta> cuentas;


    // Constructores
    public Plan() {}

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Plan)) return false;
        Plan plan = (Plan) o;
        return id != null && id.equals(plan.id);
    }

    @Override
    public int hashCode() {
        return (id == null) ? 0 : id.hashCode();
    }
}
