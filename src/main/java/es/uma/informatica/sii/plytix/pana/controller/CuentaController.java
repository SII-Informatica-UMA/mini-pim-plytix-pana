package es.uma.informatica.sii.plytix.pana.controller;

import es.uma.informatica.sii.plytix.pana.dto.PropietarioDTO;
import es.uma.informatica.sii.plytix.pana.service.CuentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cuenta")
@RequiredArgsConstructor
public class CuentaController {
    private final CuentaService cuentaService;

    @GetMapping("/{idCuenta}/propietario")
    @PreAuthorize("hasRole('ADMIN') or @cuentaService.esPropietarioOUsuario(#idCuenta, authentication.principal.id)")
    public ResponseEntity<PropietarioDTO> obtenerPropietario(
            @PathVariable Long idCuenta) {
        // Si el usuario no cumple la condición, Spring devolverá 403 automáticamente
        PropietarioDTO propietario = cuentaService.obtenerPropietarioCuenta(idCuenta);
        return ResponseEntity.ok(propietario);
    }
}