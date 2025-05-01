package es.uma.informatica.sii.plytix.pana.controller;


import es.uma.informatica.sii.plytix.pana.dto.CuentaDTO;
import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import es.uma.informatica.sii.plytix.pana.service.CuentaService;
import es.uma.informatica.sii.plytix.pana.service.Exceptions.CuentaNoEncontrada;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RequestMapping ("/cuenta")
@RestController
public class CuentaController {

    private CuentaService servicio;

    public CuentaController(CuentaService servicio) {
        this.servicio = servicio;
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> modificarCuenta(@PathVariable(name = "id")Long id, @RequestBody CuentaDTO cuenta) {

        servicio.modificarCuenta(cuenta, id);
        return ResponseEntity.ok(cuenta);
    }


    @ExceptionHandler(CuentaNoEncontrada.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void noEncontrado(){}

}