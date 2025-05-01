package es.uma.informatica.sii.plytix.pana.controller;

import es.uma.informatica.sii.plytix.pana.dto.CuentaDTO;
import es.uma.informatica.sii.plytix.pana.dto.PropietarioDTO;
import es.uma.informatica.sii.plytix.pana.dto.UsuarioDTO;
import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import es.uma.informatica.sii.plytix.pana.Mapper.CuentaMapper;
import es.uma.informatica.sii.plytix.pana.service.CuentaNotFoundException;
import es.uma.informatica.sii.plytix.pana.service.CuentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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

    @PostMapping
    public ResponseEntity<CuentaDTO> anadirCuenta(@RequestBody CuentaDTO cuentaDTO, UriComponentsBuilder uriBuilder){

        System.out.println("Anyadir cuenta: "+cuentaDTO);

        Cuenta cuentaEntity = CuentaMapper.toEntity(cuentaDTO);
        Long idUsuario = cuentaEntity.getId();
        if (idUsuario == null) {
            throw new RuntimeException("ID de usuario no disponible en el token.");
        }
        cuentaEntity = cuentaService.anadirCuenta(cuentaEntity, idUsuario);
        return ResponseEntity.created(uriBuilder
                        .path("/cuenta/{id}")
                        .buildAndExpand(cuentaEntity.getId())
                        .toUri())
                .body(CuentaMapper.toDTO(cuentaEntity));

    }

    @PostMapping("/{idCuenta}/propietario")
    public ResponseEntity<?> actualizarPropietario( @PathVariable Long idCuenta, @RequestBody UsuarioDTO dto) {

        cuentaService.actualizarPropietario(idCuenta, dto.getId());

        return ResponseEntity.ok().body(dto);
    }
//--------------------------------------------------------------------------------------------------
    @PostMapping("/{idCuenta}/usuarios")
    public ResponseEntity<?> actualizarUsuarios( @PathVariable Long idCuenta, @RequestBody List<UsuarioDTO> nuevosUsuarios) {

        Long usuarioActualId = obtenerIdUsuarioActual();

        // Permitir solo si es ADMIN o si es el propietario de la cuenta
        Cuenta cuenta = cuentaService.obtenerCuentaPorId(idCuenta) //falta atributo idusuario (son 2 atributos)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        boolean esAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean esPropietario = cuenta.getId() != null &&
                cuenta.getDuenoId().equals(usuarioActualId);

        if (!esAdmin && !esPropietario) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Long> ids = nuevosUsuarios.stream()
                .map(UsuarioDTO::getId)
                .toList();

        cuentaService.actualizarUsuarios(idCuenta, ids); //ids tiene que ser de tipo string, no Long (en el service aparece como string)

        // (opcional) Devolver usuarios enriquecidos si llamas a microservicio de usuarios
        return ResponseEntity.ok(nuevosUsuarios);
    }
//-------------------------------------------------------------------------------------------------------------------


    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> modificarCuenta(@PathVariable(name = "id")Long id, @RequestBody CuentaDTO cuenta) {

        cuentaService.modificarCuenta(cuenta, id);
        return ResponseEntity.ok(cuenta);
    }


    @ExceptionHandler(CuentaNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void noEncontrado(){}

}