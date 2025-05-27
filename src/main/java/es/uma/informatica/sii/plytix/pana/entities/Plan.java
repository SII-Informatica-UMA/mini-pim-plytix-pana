package es.uma.informatica.sii.plytix.pana.entities;

import jakarta.persistence.*;
import java.util.List;

/**
 * Entidad que representa un plan de suscripción en el sistema.
 * Contiene información sobre los límites y características del plan.
 */
@Entity
public class Plan {
    @Id  // Indica que este campo es la clave primaria de la entidad
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;  // Nombre del plan

    // Los @Column personalizan el nombre de la columna en la base de datos
    @Column(name = "MAX_PRODUCTOS")
    private Long maxProductos;  // Máximo número de productos permitidos

    @Column(name = "MAX_ACTIVOS")
    private Long maxActivos;  // Máximo número de activos permitidos

    @Column(name = "MAX_ALMACENAMIENTO")
    private Long maxAlmacenamiento;  // Límite de almacenamiento que ofrece el plan para una cuenta

    @Column(name = "MAX_CATEGORIAS_PRODUCTOS")
    private Long maxCategoriasProductos;  // Máximo categorías de productos

    @Column(name = "MAX_CATEGORIAS_ACTIVOS")
    private Long maxCategoriasActivos;  // Máximo categorías de activos

    @Column(name = "MAX_RELACIONES")
    private Long maxRelaciones;  // Máximo relaciones entre entidades

    private Double precio;  // Precio del plan

    // Relación uno-a-muchos con Cuenta. Un plan puede tener múltiples cuentas asociadas
    @OneToMany(mappedBy = "plan")  // El dueño de la relación es Cuenta a través del campo 'plan'
    private List<Cuenta> cuentas;

    public Plan() {}

    public Plan(Long id){
        this.id = id;
    }

    public Plan(Long id, String nombre, Long maxProductos, Long maxActivos, Long maxAlmacenamiento, Long maxCategoriasProductos, Long maxCategoriasActivos, Long maxRelaciones, Double precio, List<Cuenta> cuentas) {
        this.id = id;
        this.nombre = nombre;
        this.maxProductos = maxProductos;
        this.maxActivos = maxActivos;
        this.maxAlmacenamiento = maxAlmacenamiento;
        this.maxCategoriasProductos = maxCategoriasProductos;
        this.maxCategoriasActivos = maxCategoriasActivos;
        this.maxRelaciones = maxRelaciones;
        this.precio = precio;
        this.cuentas = cuentas;
    }

    // === GETTERS Y SETTERS ===

    public void setCuentas(List<Cuenta> cuentas){this.cuentas = cuentas;}

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
        return maxActivos;
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

    // Método equals que compara por ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Plan)) return false;
        Plan plan = (Plan) o;
        return id != null && id.equals(plan.id);
    }

    // Método hashCode basado en el ID
    @Override
    public int hashCode() {
        return (id == null) ? 0 : id.hashCode();
    }

    @Override
    public String toString() {
    return "Plan{" +
            "id=" + id +
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
