package es.uma.informatica.sii.plytix.pana.controller;

import es.uma.informatica.sii.plytix.pana.Mapper.CuentaMapper;
import es.uma.informatica.sii.plytix.pana.dto.CuentaDTO;
import es.uma.informatica.sii.plytix.pana.dto.UsuarioDTO;
import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import es.uma.informatica.sii.plytix.pana.excepciones.CredencialesInvalidasException;
import es.uma.informatica.sii.plytix.pana.excepciones.CuentaNotFoundException;
import es.uma.informatica.sii.plytix.pana.excepciones.PermisosInsuficientesException;
import es.uma.informatica.sii.plytix.pana.excepciones.RecursosAsociadosException;
import es.uma.informatica.sii.plytix.pana.service.CuentaService;
import es.uma.informatica.sii.plytix.pana.service.UsuarioServiceClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


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

        List<Cuenta> cuentas;

        if (idCuenta != null) {
            // Single account by ID - admin or associated user
            cuentas = cuentaService.obtenerCuentaPorId(idCuenta, userId);
        } else if (nombre != null) {
            // Accounts by name - admin only
            cuentas = cuentaService.obtenerCuentasPorNombre(nombre, userId);
        } else if (idPlan != null) {
            // Accounts by plan - admin only
            cuentas = cuentaService.obtenerCuentasPorPlan(idPlan, userId);
        } else if (idUsuario != null) {
            // Accounts accessible by specific user
            cuentas = cuentaService.obtenerCuentasPorUsuario(idUsuario, userId);
        } else {
            // Default case - accounts accessible by current user
            cuentas = cuentaService.obtenerCuentasPorUsuario(userId, userId);
        }

        return ResponseEntity.ok(cuentas);
    }

    @GetMapping("/{idCuenta}/usuarios")
    @PreAuthorize("hasRole('ADMIN') or #idUsuario == authentication.principal.id")
    public ResponseEntity<Boolean> usuarioTieneAccesoACuenta(
            @PathVariable Long idCuenta,
            @PathVariable Long idUsuario) {
        boolean tieneAcceso = cuentaService.tieneAcceso(idCuenta, idUsuario);
        return ResponseEntity.ok(tieneAcceso);
    }

    @GetMapping("/{idCuenta}/propietario")
    @PreAuthorize("hasRole('ADMIN') or @cuentaService.esPropietarioOUsuario(#idCuenta, authentication.principal.id)")
    public ResponseEntity<UsuarioDTO> obtenerPropietario(
            @PathVariable Long idCuenta) {
        UsuarioDTO propietario = cuentaService.obtenerPropietarioCuenta(idCuenta);
        return ResponseEntity.ok(propietario);
    }
    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> modificarCuenta(@PathVariable(name = "id")Long id, @RequestBody CuentaDTO cuenta) {

        cuentaService.modificarCuenta(cuenta, id);
        return ResponseEntity.ok(cuenta);
    }

    /**
     * DELETE /api/cuenta/{idCuenta}
     *  - 200 OK          → Se elimina la cuenta correctamente.
     *  - 401 UNATHORIZED → Credenciales no válidas o faltantes.
     *  - 403 FORBIDDEN   → Sin permisos o con recursos asociados.
     *  - 404 Not Found   → Cuenta no encontrada.
     */
    @DeleteMapping("/{idCuenta}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarCuenta(
            @PathVariable("idCuenta") Long idCuenta,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            cuentaService.eliminarCuenta(idCuenta);
            return ResponseEntity.ok().build();
        } catch (PermisosInsuficientesException | RecursosAsociadosException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (CredencialesInvalidasException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
