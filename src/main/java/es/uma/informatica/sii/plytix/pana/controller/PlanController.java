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


    @GetMapping("/{idPlan}")
    public ResponseEntity<List<Plan>> listarPlanes(
            @PathVariable(name = "idPlan", required = false) Long idPlan,
            @PathVariable(name = "nombre", required = false) String nombre) {


        List<Plan> planes = servicio.buscarPlanes(idPlan, nombre);
        if (planes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(planes);
    }


    @DeleteMapping("/{idPlan}")
    public ResponseEntity<String> eliminarPlan(@PathVariable("idPlan") Long idPlan) {
        if (idPlan == null ) {
            return ResponseEntity
                    .badRequest()
                    .body("ID de plan no válido (400).");
        }
        try {
            servicio.eliminarPlan(idPlan);
            return ResponseEntity.ok("Plan eliminado correctamente (200).");
        } catch (CredencialesInvalidasException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales no válidas o faltantes (401).");
        } catch (PermisosInsuficientesException e) {
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
