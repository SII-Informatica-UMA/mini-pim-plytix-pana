package es.uma.informatica.sii.plytix.pana.service;

import es.uma.informatica.sii.plytix.pana.dto.PropietarioDTO;
import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import es.uma.informatica.sii.plytix.pana.service.AccesoDenegadoException;
import es.uma.informatica.sii.plytix.pana.service.CuentaNotFoundException;
import es.uma.informatica.sii.plytix.pana.repositories.CuentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CuentaService {
    private final CuentaRepository cuentaRepository;
    private final UsuarioServiceClient usuarioServiceClient;

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or @cuentaService.tieneAcceso(#cuentaId, authentication.principal.id)")
    public PropietarioDTO obtenerPropietarioCuenta(Long cuentaId) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaNotFoundException(cuentaId));

        return usuarioServiceClient.obtenerUsuarioPorId(cuenta.getDuenoId());
    }

    public boolean tieneAcceso(Long cuentaId, Long usuarioId) {
        return cuentaRepository.existsByIdAndDuenoIdOrUsuariosContaining(cuentaId, usuarioId);
    }
}