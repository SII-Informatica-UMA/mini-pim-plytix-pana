package es.uma.informatica.sii.plytix.pana.service;

import es.uma.informatica.sii.plytix.pana.dto.PlanDTO;
import es.uma.informatica.sii.plytix.pana.entities.Plan;
import es.uma.informatica.sii.plytix.pana.repositories.CuentaRepository;
import es.uma.informatica.sii.plytix.pana.repositories.PlanRepository;
import es.uma.informatica.sii.plytix.pana.service.PLanNoEncontrado;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.util.*;

import java.util.Optional;

@Service
@Transactional
public class PlanService {
    private final PlanRepository repo;
    private final CuentaRepository Cuentarepo;

    @Autowired
    public PlanService (PlanRepository repo, CuentaRepository cuentarepo){
        this.repo = repo;
        Cuentarepo = cuentarepo;
    }

    //Post
    public Long aniadirPlan (Plan p){
        p.setId(null);
        repo.save(p);
        return p.getId();
    }

    //Put
    public void modificarPlan(PlanDTO plan, Long id){
        Optional<Plan> p = repo.findById(id);
        if(!p.isPresent()){
            throw new PLanNoEncontrado();
        }
        Plan pl = p.get();
        pl.setNombre(plan.getNombre());
    }

    /**
     * Elimina el plan indicado solo si:
     *  - existe,
     *  - no hay ninguna cuenta asociada a él.
     *
     * @param planId ID del plan a eliminar.
     * @throws PLanNoEncontrado si no existe el plan.
     * @throws CuentasAsociadasException si hay cuentas que dependen de este plan.
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminarPlan(Long planId) {
        // 1) Comprobar existencia
        if (!repo.existsById(planId)) {
            throw new PLanNoEncontrado();
        }

        // 2) Comprobar cuentas asociadas
        // opción A: si CuentaRepository tiene método existsByPlanId:
        if (Cuentarepo.existsByPlan_Id(planId)) {
            throw new CuentasAsociadasException("No se puede eliminar el plan, tiene cuentas asociadas.");
        }

        // 3) Si pasa todas las validaciones, eliminar
        repo.deleteById(planId);
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

        // 1) Sin filtros => todos
        if (idPlan == null && nombre == null) {
            return repo.findAll();
        }
        // 2) Solo idPlan => buscado por PK
        if (idPlan != null && nombre == null) {
            return repo.findById(idPlan)
                    .map(List::of)
                    .orElseGet(List::of);
        }
        // 3) Solo nombre => findByNombre
        if (idPlan == null && nombre != null) {
            return repo.findByNombre(nombre);
        }
        // 4) Ambos filtros: por ID y que el nombre coincida
        return repo.findById(idPlan)
                .filter(p -> nombre.equals(p.getNombre()))
                .map(List::of)
                .orElseGet(List::of);
    }


}
