package es.uma.informatica.sii.plytix.pana.controller;

import es.uma.informatica.sii.plytix.pana.dto.PlanDTO;
import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import es.uma.informatica.sii.plytix.pana.excepciones.PermisosInsuficientesException;
import es.uma.informatica.sii.plytix.pana.excepciones.CredencialesInvalidasException;
import es.uma.informatica.sii.plytix.pana.excepciones.PlanNoEncontrado;
import es.uma.informatica.sii.plytix.pana.excepciones.CuentasAsociadasException;
import es.uma.informatica.sii.plytix.pana.entities.Plan;
import es.uma.informatica.sii.plytix.pana.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.*;

import java.net.URI;

@RequestMapping ("/plan")
@RestController
public class PlanController {

    private PlanService servicio;

    public PlanController(PlanService servicio) {
        this.servicio = servicio;
    }

    /**
     * Lista todos los planes disponibles.
     * - Si no se indica idPlan ni nombre, devuelve todos los planes.
     * - Si se indica idPlan, devuelve los planes correspondientes a ese id.
     * - Si se indica nombre, devuelve los planes con ese nombre.
     * - Se pueden combinar ambos parámetros.
     *
     * @param idPlan  (opcional) ID del plan.
     * @param nombre  (opcional) Nombre del plan.
     * @return Lista de planes que cumplen con los criterios de búsqueda.
     */
    @GetMapping
    public ResponseEntity<List<Plan>> listarPlanes(
            @RequestParam(name = "idPlan", required = false) Long idPlan,
            @RequestParam(name = "nombre", required = false) String nombre) {

        // Llama a un servicio que gestione la lógica de búsqueda.
        // Por ejemplo, podríamos tener un método: planService.buscarPlanes(idPlan)
        // que devuelva la lista de planes que cumplan con los criterios.

        List<Plan> planes = servicio.buscarPlanes(idPlan, nombre);

        // Devuelve un 200 (OK) con el listado en formato JSON.
        return ResponseEntity.ok(planes);
    }

    /**
     * Elimina un plan por su ID.
     * Solo un administrador puede hacerlo. Para poder eliminar un plan no puede
     * haber ninguna cuenta asociada a ese plan (lógica que controlará el servicio).
     *
     * @param idPlan Identificador del plan (vía PathVariable).
     * @return Mensaje de éxito o de error según el caso.
     */
    @DeleteMapping("/{idPlan}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> eliminarPlan(@PathVariable("idPlan") Long idPlan) {
        // Ejemplo de validación básica de idPlan
        if (idPlan == null || idPlan < 1) {
            return ResponseEntity
                    .badRequest()
                    .body("ID de plan no válido (400).");
        }
        try {
            // Lógica de eliminación en el servicio
            servicio.eliminarPlan(idPlan);
            // 200 OK: Plan eliminado correctamente
            return ResponseEntity.ok("Plan eliminado correctamente (200).");
        } catch (CredencialesInvalidasException e) {
            // 401 Unauthorized (por ejemplo, si no hay token o es inválido)
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales no válidas o faltantes (401).");
        } catch (PermisosInsuficientesException e) {
            // 403 Forbidden (usuario sin permisos suficientes)
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Sin permisos suficientes (403).");
        } catch (CuentasAsociadasException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Sin permisos suficientes (403).");
        }
    }

    @PostMapping
    public ResponseEntity<?> anadirProyecto(@RequestBody PlanDTO plan, UriComponentsBuilder builder) {
        Plan pl = new Plan();

        pl.setNombre(plan.getNombre());
        pl.setMaxActivos(plan.getMaxActivos());
        pl.setPrecio(plan.getPrecio());
        pl.setMaxAlmacenamiento(plan.getMaxAlmacenamiento());
        pl.setMaxCategoriasActivos(plan.getMaxCategoriasActivos());
        pl.setMaxCategoriasProductos(plan.getMaxCategoriasProductos());
        pl.setMaxRelaciones(plan.getMaxRelaciones());
        pl.setMaxProductos(plan.getMaxProductos());
        List<Cuenta> c = new ArrayList<>();
        pl.setCuentas(c);

        Plan p = servicio.aniadirPlan(pl);
        URI uri = builder
                .path("/plan")
                .path(String.format("/%d", p.getId()))
                .build()
                .toUri();
        return ResponseEntity.created(uri).build();
    }


    @PutMapping("/{idPlan}")
    public ResponseEntity<?> modificarPlan(@PathVariable(name = "idPlan")Long id, @RequestBody PlanDTO plan) {

        servicio.modificarPlan(plan, id);
        return ResponseEntity.ok(plan);
    }

    @ExceptionHandler(PlanNoEncontrado.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void noEncontrado(){}


}
