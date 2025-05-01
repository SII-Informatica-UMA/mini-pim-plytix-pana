package es.uma.informatica.sii.plytix.pana.controller;

import es.uma.informatica.sii.plytix.pana.Mapper.CuentaMapper;
import es.uma.informatica.sii.plytix.pana.dto.CuentaDTO;
import es.uma.informatica.sii.plytix.pana.dto.UsuarioDTO;
import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import es.uma.informatica.sii.plytix.pana.service.LogicaCuenta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;


@RestController
@RequestMapping("/")
public class CuentaController {

    private LogicaCuenta logica;


    @PostMapping
    public ResponseEntity<CuentaDTO> anadirCuenta(@RequestBody CuentaDTO cuentaDTO, UriComponentsBuilder uriBuilder){

        System.out.println("Anyadir cuenta: "+cuentaDTO);

        Cuenta cuentaEntity = CuentaMapper.toEntity(cuentaDTO);
        Long idUsuario = cuentaEntity.getId();
        if (idUsuario == null) {
            throw new RuntimeException("ID de usuario no disponible en el token.");
        }
        cuentaEntity = logica.anadirCuenta(cuentaEntity, idUsuario);
        return ResponseEntity.created(uriBuilder
                        .path("/cuenta/{id}")
                        .buildAndExpand(cuentaEntity.getId())
                        .toUri())
                .body(CuentaMapper.toDTO(cuentaEntity));

    }

    @PostMapping("/{idCuenta}/propietario")
    public ResponseEntity<?> actualizarPropietario( @PathVariable Long idCuenta, @RequestBody UsuarioDTO dto) {

        logica.actualizarPropietario(idCuenta, dto.getId());

        return ResponseEntity.ok().body(dto);
    }

    @PostMapping("/{idCuenta}/usuarios")
    public ResponseEntity<?> actualizarUsuarios( @PathVariable Long idCuenta, @RequestBody List<UsuarioDTO> nuevosUsuarios) {

        Long usuarioActualId = obtenerIdUsuarioActual();

        // Permitir solo si es ADMIN o si es el propietario de la cuenta
        Cuenta cuenta = cuentaService.obtenerCuentaPorId(idCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        boolean esAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean esPropietario = cuenta.getPropietarioId() != null &&
                cuenta.getDuenoId().equals(usuarioActualId);

        if (!esAdmin && !esPropietario) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Long> ids = nuevosUsuarios.stream()
                .map(UsuarioDTO::getId)
                .toList();

        logica.actualizarUsuarios(idCuenta, ids);

        // (opcional) Devolver usuarios enriquecidos si llamas a microservicio de usuarios
        return ResponseEntity.ok(nuevosUsuarios);
    }
}
