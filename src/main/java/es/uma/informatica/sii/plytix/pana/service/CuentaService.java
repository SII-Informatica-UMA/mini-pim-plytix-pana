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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

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


        // Obtener la lista actual
        List<Long> usuariosActuales = cuenta.getUsuarios();

        // Crear una nueva lista combinando valores existentes + nuevo
        List<Long> nuevaLista = new ArrayList<>(usuariosActuales);
        nuevaLista.add(idAdmin); // Añadir el nuevo ID

        // Llamar al setter
        cuenta.setUsuarios(nuevaLista); // Reemplaza toda la lista

        cuenta.setDuenoId(idAdmin);


        return cuentaRepository.save(cuenta);
    }

    // POST /cuenta/{idCuenta}/propietario
    public void actualizarPropietario(Long idCuenta, Long nuevoPropietarioId, String email) {

        if (idCuenta == null || (nuevoPropietarioId == null && (email == null || email.isBlank()))) {
            throw new IllegalArgumentException("Parámetros inválidos");
        }

        System.out.println("Llamada a cuentaService con email: "+email);


        // Buscar cuenta; lanza 404 si no se encuentra
        Cuenta cuenta = cuentaRepository.findById(idCuenta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada con ID: " + idCuenta));

        List<Long> usuarios = cuenta.getUsuarios();

        if (usuarios == null) {
            usuarios = new ArrayList<>();
            cuenta.setUsuarios(usuarios);
        }

        if(nuevoPropietarioId==null){
            UsuarioDTO user;
            do {
                user = new UsuarioDTO(email);
            } while (usuarios.contains(user.getId()));

            nuevoPropietarioId = user.getId();
        }


        if (!usuarios.contains(nuevoPropietarioId)) {
            usuarios.add(nuevoPropietarioId);
        }

        cuenta.setDuenoId(nuevoPropietarioId);

        cuentaRepository.save(cuenta);
    }

    // POST /cuenta/{idCuenta}/usuarios
    public void actualizarUsuariosCuenta(Long idCuenta, List<UsuarioDTO> nuevosUsuariosIds) {

        Cuenta cuenta = cuentaRepository.findById(idCuenta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada con ID: " + idCuenta));

        List<Long> nuevosIds = nuevosUsuariosIds.stream()
                .map(UsuarioDTO::getId)
                .collect(Collectors.toList());

        cuenta.setUsuarios(nuevosIds);

        // Guardar la cuenta actualizada
        cuentaRepository.save(cuenta);
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
        cuentaRepository.save(cu);
    }

    public void eliminarCuenta(Long cuentaId) {


        if (cuentaRepository.existsById(cuentaId)) {
            cuentaRepository.deleteById(cuentaId);
        } else {
            throw new CuentaNotFoundException(cuentaId);
        }

    }


}
