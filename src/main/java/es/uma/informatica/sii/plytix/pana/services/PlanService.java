package es.uma.informatica.sii.plytix.pana.services;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import es.uma.informatica.sii.plytix.pana.repositories.CuentaRepository;
import es.uma.informatica.sii.plytix.pana.services.exceptions.CuentasAsociadasException;
import es.uma.informatica.sii.plytix.pana.services.exceptions.PlanNoEncontradoException;
import es.uma.informatica.sii.plytix.pana.repositories.PlanRepository;
import es.uma.informatica.sii.plytix.pana.entities.Plan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final CuentaRepository cuentaRepository;

    @Autowired
    public PlanService(PlanRepository planRepository,
                       CuentaRepository cuentaRepository) {
        this.planRepository = planRepository;
        this.cuentaRepository = cuentaRepository;
    }

    /**
     * Elimina el plan indicado solo si:
     *  - existe,
     *  - no hay ninguna cuenta asociada a él.
     *
     * @param planId ID del plan a eliminar.
     * @throws PlanNoEncontradoException si no existe el plan.
     * @throws CuentasAsociadasException si hay cuentas que dependen de este plan.
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminarPlan(Long planId) {
        // 1) Comprobar existencia
        if (!planRepository.existsById(planId)) {
            throw new PlanNoEncontradoException(planId);
        }

        // 2) Comprobar cuentas asociadas
        // opción A: si CuentaRepository tiene método existsByPlanId:
        if (cuentaRepository.existsByPlanId(planId)) {
            throw new CuentasAsociadasException("No se puede eliminar el plan, tiene cuentas asociadas.");
        }

        // opción B: si Plan entity tiene un set de cuentas:
        // Plan plan = planRepository.findById(planId).get();
        // if (!plan.getCuentas().isEmpty()) {
        //     throw new CuentasAsociadasException("…");
        // }

        // 3) Si pasa todas las validaciones, eliminar
        planRepository.deleteById(planId);
    }


    /**
     * Busca planes según idPlan y/o nombre.
     */
    public List<Plan> buscarPlanes(Long idPlan, String nombre) {
        // Ejemplo simplificado:
        // - Si ambos parámetros son nulos, devuelve todos los planes.
        // - Si sólo uno está presente, busca por ese campo.
        // - Si ambos están presentes, filtra por ambos.
        // Podrías usar métodos de JPA, Criteria, QueryDSL, etc.

        if (idPlan == null && nombre == null) {
            // No se ha especificado nada, devuelve todos
            return planRepository.findAll();
        } else if (idPlan != null) {
            // Buscar por idPlan
            return planRepository.findByIdPlan(idPlan);
        } else {
            // Buscar por nombre
            return planRepository.findByNombre(nombre);
        }
    }
}


