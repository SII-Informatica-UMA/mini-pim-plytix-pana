package es.uma.informatica.sii.plytix.pana.controller;

import es.uma.informatica.sii.plytix.pana.dto.CuentaDTO;
import es.uma.informatica.sii.plytix.pana.dto.PropietarioDTO;
import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import es.uma.informatica.sii.plytix.pana.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuenta")
public class CuentaController {
    private final CuentaService cuentaService;

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

    @GetMapping("/{idCuenta}/{idUsuario}")
    @PreAuthorize("hasRole('ADMIN') or #idUsuario == authentication.principal.id")
    public ResponseEntity<Boolean> usuarioTieneAccesoACuenta(
            @PathVariable Long idCuenta,
            @PathVariable Long idUsuario) {
        boolean tieneAcceso = cuentaService.tieneAcceso(idCuenta, idUsuario);
        return ResponseEntity.ok(tieneAcceso);
    }

    @GetMapping("/{idCuenta}/propietario")
    @PreAuthorize("hasRole('ADMIN') or @cuentaService.esPropietarioOUsuario(#idCuenta, authentication.principal.id)")
    public ResponseEntity<PropietarioDTO> obtenerPropietario(
            @PathVariable Long idCuenta) {
        PropietarioDTO propietario = cuentaService.obtenerPropietarioCuenta(idCuenta);
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
    @DeleteMapping("/cuenta/{idCuenta}")
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


    @ExceptionHandler(CuentaNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void noEncontrado(){}

}