package es.uma.informatica.sii.plytix.pana.service;

import es.uma.informatica.sii.plytix.pana.dto.CuentaDTO;
import es.uma.informatica.sii.plytix.pana.dto.PropietarioDTO;
import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import es.uma.informatica.sii.plytix.pana.entities.Plan;
import es.uma.informatica.sii.plytix.pana.repositories.CuentaRepository;
import es.uma.informatica.sii.plytix.pana.repositories.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CuentaService {
    private final CuentaRepository cuentaRepository;
    private final UsuarioServiceClient usuarioServiceClient;
    private final PlanRepository planRepository;

    @Autowired
    public CuentaService (CuentaRepository cuentaRepository, UsuarioServiceClient usuarioServiceClient, PlanRepository planRepository){
        this.cuentaRepository = cuentaRepository;
        this.usuarioServiceClient = usuarioServiceClient;
        this.planRepository = planRepository;
    }

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
                    .orElseThrow(() -> new PLanNoEncontrado());
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
    public void actualizarUsuarios(Long idCuenta, List<String> identificadoresUsuarios) {
        Cuenta cuenta = cuentaRepository.findById(idCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        Set<Long> nuevosUsuarios = new HashSet<>();

        for (String identificador : identificadoresUsuarios) {
            try {
                Long id = Long.parseLong(identificador);
                nuevosUsuarios.add(id);
            } catch (NumberFormatException e) {
                // Si es un email, lo ignoramos o lanzamos excepción según tus reglas
                throw new RuntimeException("Identificador no válido: " + identificador);
            }
        }

        Long idPropietario = cuenta.getDuenoId();

        // Aseguramos que el propietario permanezca en la lista
        if (idPropietario != null && !nuevosUsuarios.contains(idPropietario)) {
            nuevosUsuarios.add(idPropietario);
        }

        cuenta.setUsuarios(new ArrayList<>(nuevosUsuarios));
        cuentaRepository.save(cuenta);
    }





    //put
    public void modificarCuenta (CuentaDTO cuenta, Long id){

        Optional<Cuenta> c = cuentaRepository.findById(id);
        if(!c.isPresent()){
            throw new CuentaNotFoundException(id);
        }
        Cuenta cu = c.get();
        cu.setNombre(cuenta.getNombre());
        cu.setDireccionFiscal(cuenta.getDireccionFiscal());
        cu.setNIF(cuenta.getnif());
        cu.setFechaAlta(cuenta.getFechaAlta());
        cu.setPlan(cuenta.getPlan());
    }


}