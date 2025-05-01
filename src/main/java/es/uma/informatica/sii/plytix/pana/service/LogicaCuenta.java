package es.uma.informatica.sii.plytix.pana.service;

import es.uma.informatica.sii.plytix.pana.dto.CuentaDTO;
import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import es.uma.informatica.sii.plytix.pana.entities.Plan;
import es.uma.informatica.sii.plytix.pana.excepciones.PlanNoExisteException;
import es.uma.informatica.sii.plytix.pana.repositories.CuentaRepository;
import es.uma.informatica.sii.plytix.pana.repositories.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@Transactional
public class LogicaCuenta {

    @Autowired
    private CuentaRepository cuentaRepository;
    @Autowired
    private PlanRepository planRepository;


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


}
