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

import java.util.List;

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

    public boolean esPropietarioOUsuario(Long cuentaId, Long usuarioId) {
        return tieneAcceso(cuentaId, usuarioId);
    }

    @Transactional(readOnly = true)
    public List<Cuenta> obtenerCuentaPorId(Long idCuenta, Long userId) {
        if (!cuentaRepository.existsByIdAndDuenoIdOrUsuariosContaining(idCuenta, userId)
                && !usuarioServiceClient.isAdmin(userId)) {
            throw new AccesoDenegadoException("No tienes acceso a esta cuenta");
        }
        return cuentaRepository.findById(idCuenta)
                .map(List::of)
                .orElseThrow(() -> new CuentaNotFoundException(idCuenta));
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<Cuenta> obtenerCuentasPorNombre(String nombre, Long userId) {
        return cuentaRepository.findByNombre(nombre);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<Cuenta> obtenerCuentasPorPlan(Long idPlan, Long userId) {
        return cuentaRepository.findByPlanId(idPlan);
    }

    @Transactional(readOnly = true)
    public List<Cuenta> obtenerCuentasPorUsuario(Long idUsuario, Long requestingUserId) {
        if (!idUsuario.equals(requestingUserId) && !usuarioServiceClient.isAdmin(requestingUserId)) {
            throw new AccesoDenegadoException("Solo puedes consultar tus propias cuentas");
        }
        List<Cuenta> cuentas = new ArrayList<>();
        cuentas.addAll(cuentaRepository.findByDuenoId(idUsuario));
        cuentas.addAll(cuentaRepository.findByUsuariosContaining(idUsuario));
        return cuentas;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<Cuenta> obtenerTodasLasCuentas() {
        return cuentaRepository.findAll();
    }
}