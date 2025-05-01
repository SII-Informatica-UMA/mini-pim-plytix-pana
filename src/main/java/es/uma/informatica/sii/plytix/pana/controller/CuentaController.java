package es.uma.informatica.sii.plytix.pana.controller;

import es.uma.informatica.sii.plytix.pana.dto.PropietarioDTO;
import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import es.uma.informatica.sii.plytix.pana.service.CuentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuenta")
@RequiredArgsConstructor
public class CuentaController {
    private final CuentaService cuentaService;

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
}