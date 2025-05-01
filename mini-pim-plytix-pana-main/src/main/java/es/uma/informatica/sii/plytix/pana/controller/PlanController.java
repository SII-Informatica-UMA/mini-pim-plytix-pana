package es.uma.informatica.sii.plytix.pana.controller;

import es.uma.informatica.sii.plytix.pana.dto.PlanDTO;
import es.uma.informatica.sii.plytix.pana.entities.Plan;
import es.uma.informatica.sii.plytix.pana.service.Exceptions.PLanNoEncontrado;
import es.uma.informatica.sii.plytix.pana.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RequestMapping ("/plan")
@RestController
public class PlanController {

    private PlanService servicio;

    public PlanController(PlanService servicio) {
        this.servicio = servicio;
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> anadirProyecto(@RequestBody PlanDTO plan, UriComponentsBuilder builder) {
        Plan pl = new Plan();

        pl.setNombre(plan.getNombre());
        pl.setMaxActivos(plan.getMaxActivos());
        pl.setPrecio(plan.getPrecio());
        pl.setMaxAlmacenamiento(pl.getMaxAlmacenamiento());
        pl.setMaxCategoriasActivos(pl.getMaxCategoriasActivos());
        pl.setMaxCategoriasProductos(plan.getMaxCategoriasProductos());
        pl.setMaxRelaciones(plan.getMaxRelaciones());
        pl.setMaxProductos(plan.getMaxProductos());

        Long id = servicio.aniadirPlan(pl);
        URI uri = builder
                .path("/plan")
                .path(String.format("/%d", pl.getId()))
                .build()
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> modificarPlan(@PathVariable(name = "id")Long id, @RequestBody PlanDTO plan) {

        servicio.modificarPlan(plan, id);
        return ResponseEntity.ok(plan);
    }

    @ExceptionHandler(PLanNoEncontrado.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void noEncontrado(){}


}
