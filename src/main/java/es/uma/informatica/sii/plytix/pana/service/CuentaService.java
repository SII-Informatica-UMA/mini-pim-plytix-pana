package es.uma.informatica.sii.plytix.pana.service;

import es.uma.informatica.sii.plytix.pana.dto.CuentaDTO;
import es.uma.informatica.sii.plytix.pana.dto.UsuarioDTO;
import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import es.uma.informatica.sii.plytix.pana.entities.Plan;
import es.uma.informatica.sii.plytix.pana.excepciones.AccesoDenegadoException;
import es.uma.informatica.sii.plytix.pana.excepciones.CuentaNotFoundException;
import es.uma.informatica.sii.plytix.pana.excepciones.PlanNoExisteException;
import es.uma.informatica.sii.plytix.pana.excepciones.RelacionesAsociadasException;
import es.uma.informatica.sii.plytix.pana.repositories.CuentaRepository;
import es.uma.informatica.sii.plytix.pana.repositories.PlanRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@Transactional
public class CuentaService {

    @Autowired
    private CuentaRepository cuentaRepository;
    @Autowired
    private PlanRepository planRepository;

    private final UsuarioServiceClient usuarioServiceClient;


    //POST /cuenta
    public Cuenta anadirCuenta(Cuenta cuenta, Long idAdmin){

        // Validación básica del NIF (ejemplo)
        if (cuenta.getNIF() == null || cuenta.getNIF().isBlank()) {
            throw new IllegalArgumentException("NIF es requerido");
        }

        // Obtener la lista actual
        List<Long> usuariosActuales = cuenta.getUsuarios();

        // Crear una nueva lista combinando valores existentes + nuevo
        List<Long> nuevaLista = new ArrayList<>(usuariosActuales);
        nuevaLista.add(idAdmin); // Añadir el nuevo ID

        // Llamar al setter
        cuenta.setUsuarios(nuevaLista); // Reemplaza toda la lista

        cuenta.setDuenoId(idAdmin);

        // Validamos solo la existencia del plan
        if (cuenta.getPlan() != null && cuenta.getPlan().getId() != null) {
            Plan plan = planRepository.findById(cuenta.getPlan().getId())
                    .orElseThrow(() -> new PlanNoExisteException("Plan no encontrado"));
            cuenta.setPlan(plan); // Asocia el plan completo
        }

        return cuentaRepository.save(cuenta);
    }

    // POST /cuenta/{idCuenta}/propietario
    public void actualizarPropietario(Long idCuenta, Long nuevoPropietarioId) {
        // Buscar la cuenta por su ID
        Optional<Cuenta> cuentaOpt = cuentaRepository.findById(idCuenta);
        if (cuentaOpt.isEmpty()) {
            throw new IllegalArgumentException("No se encontró ninguna cuenta con ID: " + idCuenta);
        }

        Cuenta cuenta = cuentaOpt.get();

        // Actualizar el propietario
        cuenta.setDuenoId(nuevoPropietarioId); // Suponiendo que el campo propietario se llama "duenoId"

        // Verificar si el nuevo propietario ya es usuario de la cuenta
        List<Long> usuarios = cuenta.getUsuarios();
        if (usuarios == null) {
            usuarios = new ArrayList<>();
            cuenta.setUsuarios(usuarios);
        }

        if (!usuarios.contains(nuevoPropietarioId)) {
            usuarios.add(nuevoPropietarioId);
        }

        // Guardar los cambios
        cuentaRepository.save(cuenta);
    }

    // POST /cuenta/{idCuenta}/usuarios
    public List<Cuenta> esPropietarioCuenta(Long idCuenta, Long idUsuario) {
        return cuentaRepository.findByDuenoId(idUsuario);
    }


    @Autowired
    public CuentaService (CuentaRepository cuentaRepository, UsuarioServiceClient usuarioServiceClient){
        this.cuentaRepository = cuentaRepository;
        this.usuarioServiceClient = usuarioServiceClient;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or @cuentaService.tieneAcceso(#cuentaId, authentication.principal.id)")
    public UsuarioDTO obtenerPropietarioCuenta(Long cuentaId) {
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

    //put
    public void modificarCuenta (CuentaDTO cuenta, Long id){

        Optional<Cuenta> c = cuentaRepository.findById(id);
        if(!c.isPresent()){
            throw new CuentaNotFoundException(id);
        }
        Cuenta cu = c.get();
        cu.setNombre(cuenta.getNombre());
        cu.setDireccionFiscal(cuenta.getDireccion());
        cu.setNIF(cuenta.getNif());
        cu.setFechaAlta(cuenta.getFechaAlta());

        Optional<Plan> pl = planRepository.findById(cuenta.getPlanId());
        if(!pl.isPresent()){
            throw new PlanNoExisteException();
        }
        cu.setPlan(pl.get());
    }

    /**
     * Elimina la cuenta solo si:
     *  - existe,
     *  - y ninguno de sus planes tiene productos, relaciones, activos o categorías.
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminarCuenta(Long cuentaId) {

        Optional<Cuenta> cuenta = cuentaRepository.findById(cuentaId);
        // 1) Comprobar existencia de la cuenta
        if (!cuentaRepository.existsById(cuentaId)) {
            throw new CuentaNotFoundException(cuentaId);
        }

        // 2) Recuperar todos los planes de esta cuenta
        Plan plan = cuenta.get().getPlan();
        if (plan == null
                || plan.getMaxProductos()    > 0
                || plan.getMaxRelaciones()   > 0
                || plan.getMaxActivos()      > 0
                || plan.getMaxCategoriasActivos() > 0) {
            throw new RelacionesAsociadasException("error en las relaciones asociadas");
        }

        // 4) si todo es correcto, eliminar la cuenta
        cuentaRepository.deleteById(cuentaId);
    }

    @Transactional
    public void actualizarUsuariosCuenta(Long idCuenta, List<Long> nuevosUsuariosIds) {
        Cuenta cuenta = cuentaRepository.findById(idCuenta)
                .orElseThrow(() -> new EntityNotFoundException("Cuenta no encontrada"));

        // Reemplazamos la lista completa de usuarios
        cuenta.setUsuarios(nuevosUsuariosIds);
        cuentaRepository.save(cuenta);
    }



}
