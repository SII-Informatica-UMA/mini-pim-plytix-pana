package es.uma.informatica.sii.plytix.pana.controllers;

import java.net.URI;

import es.uma.informatica.sii.plytix.pana.services.exceptions.*;
import es.uma.informatica.sii.plytix.pana.services.CuentaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api")
public class CuentaController {

    private final CuentaService cuentaService;

    @Autowired
    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
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
        } catch (CuentaNoEncontradaException e) {
            return ResponseEntity.notFound().build();
        } catch (PermisosInsuficientesException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (CredencialesInvalidasException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (RecursosAsociadosException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
