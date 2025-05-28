package es.uma.informatica.sii.plytix.pana.controller;

import es.uma.informatica.sii.plytix.pana.dto.CuentaDTO;
import es.uma.informatica.sii.plytix.pana.dto.UsuarioDTO;
import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import es.uma.informatica.sii.plytix.pana.excepciones.CuentaNotFoundException;
import es.uma.informatica.sii.plytix.pana.service.CuentaService;
import es.uma.informatica.sii.plytix.pana.UsuarioServiceClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/cuenta")
public class CuentaController {

    private CuentaService cuentaService;
    private UsuarioServiceClient usuarioService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @GetMapping
    public ResponseEntity<List<Cuenta>> obtenerCuentas(
            @RequestParam(required = false) Long idCuenta,
            @RequestParam(required = false) Long idUsuario,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long idPlan,
            @AuthenticationPrincipal Long userId) {

        try {
            List<Cuenta> cuentas;
            if (idCuenta != null) {
                cuentas = cuentaService.obtenerCuentaPorId(idCuenta, userId);
            } else if (nombre != null) {
                cuentas = cuentaService.obtenerCuentasPorNombre(nombre, userId);
            } else if (idPlan != null) {
                cuentas = cuentaService.obtenerCuentasPorPlan(idPlan, userId);
            } else if (idUsuario != null) {
                cuentas = cuentaService.obtenerCuentasPorUsuario(idUsuario, userId);
            } else {
                cuentas = cuentaService.obtenerCuentasPorUsuario(userId, userId);
            }
            return ResponseEntity.ok(cuentas);
        } catch (CuentaNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage()); // Lanza 404
        }
    }

    @GetMapping("/{idCuenta}/usuarios")
    public ResponseEntity<Boolean> usuarioTieneAccesoACuenta(
            @PathVariable Long idCuenta,
            @PathVariable Long idUsuario) {
        boolean tieneAcceso = cuentaService.tieneAcceso(idCuenta, idUsuario);
        return ResponseEntity.ok(tieneAcceso);
    }

    @GetMapping("/{idCuenta}/propietario")
    public ResponseEntity<UsuarioDTO> obtenerPropietario(
            @PathVariable Long idCuenta) {
        UsuarioDTO propietario = cuentaService.obtenerPropietarioCuenta(idCuenta);
        return ResponseEntity.ok(propietario);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> modificarCuenta(@PathVariable(name = "id")Long id, @RequestBody CuentaDTO cuenta) {

        cuentaService.modificarCuenta(cuenta, id);
        return ResponseEntity.ok(cuenta);
    }

    @DeleteMapping("/{idCuenta}")
    public ResponseEntity<Void> eliminarCuenta(@PathVariable("idCuenta") Long idCuenta) {
        try {
            cuentaService.eliminarCuenta(idCuenta);
            return ResponseEntity.ok().build();
        } catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }


   @PostMapping
    public ResponseEntity<?> anadirCuenta(@RequestBody Cuenta cuenta, UriComponentsBuilder builder){


        Long idUsuario = cuenta.getId();
        if (idUsuario == null) {
            throw new RuntimeException("ID de usuario no disponible en el token.");
        }

        try {
            cuenta = cuentaService.anadirCuenta(cuenta, idUsuario);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        URI uri = builder
                .path("/cuenta/{id}")
                .buildAndExpand(cuenta.getId())
                .toUri();

        return ResponseEntity.created(uri).build();

    }

    @PostMapping("/{idCuenta}/propietario")
    public ResponseEntity<?> actualizarPropietario( @PathVariable Long idCuenta,
                                                    @RequestParam(required = false) Long nuevoPropietarioId,
                                                    @RequestParam(required = false) String email,
                                                    UriComponentsBuilder  builder) {

        if ((nuevoPropietarioId == null && email == null) ||
                (nuevoPropietarioId != null && email != null)) {
            return ResponseEntity.badRequest().body("Debe proporcionar el ID o el email del nuevo propietario.");
        }

        cuentaService.actualizarPropietario(idCuenta, nuevoPropietarioId, email);
        URI uri = builder
                .path("/cuenta/{idCuenta}/propietario")
                .buildAndExpand(idCuenta)
                .toUri();
        return ResponseEntity.ok().location(uri).build();


    }

    @PostMapping("/{idCuenta}/usuarios")
    public ResponseEntity<Void> gestionarUsuariosCuenta(@PathVariable Long idCuenta,
                                                        @RequestBody List<UsuarioDTO> nuevosUsuarios,
                                                        UriComponentsBuilder builder) {
        List<UsuarioDTO> usuariosProcesados = new ArrayList<>();

        for (UsuarioDTO user : nuevosUsuarios) {
            if (user.getId() == null) {

                usuariosProcesados.add(new UsuarioDTO(user.getEmail()));
            } else {
                usuariosProcesados.add(user);
            }
        }

        URI uri = builder
                .path("/cuenta/{idCuenta}/usuarios")
                .buildAndExpand(idCuenta)
                .toUri();

        cuentaService.actualizarUsuariosCuenta(idCuenta, usuariosProcesados);
        return ResponseEntity.ok().location(uri).build();
    }

    @ExceptionHandler(CuentaNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void noEncontrado(){}
}
