package es.uma.informatica.sii.plytix.pana.service;

import es.uma.informatica.sii.plytix.pana.dto.PlanDTO;
import es.uma.informatica.sii.plytix.pana.entities.Plan;
import es.uma.informatica.sii.plytix.pana.excepciones.CuentasAsociadasException;
import es.uma.informatica.sii.plytix.pana.excepciones.PlanNoEncontrado;
import es.uma.informatica.sii.plytix.pana.excepciones.PlanNoExisteException;
import es.uma.informatica.sii.plytix.pana.repositories.CuentaRepository;
import es.uma.informatica.sii.plytix.pana.repositories.PlanRepository;
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
    public Plan aniadirPlan (Plan p){
        p.setId(null);
        repo.save(p);
        return p;
    }

    //Put
    public void modificarPlan(PlanDTO plan, Long id){
        Optional<Plan> p = repo.findById(id);
        if(!p.isPresent()){
            throw new PlanNoEncontrado("Plan no encontrado");
        }
        Plan pl = p.get();
        pl.setNombre(plan.getNombre());

        repo.save(pl);
    }

    public void eliminarPlan(Long planId) {
        if (!repo.existsById(planId)) {
            throw new PlanNoExisteException();
        }

        if (Cuentarepo.existsByPlan_Id(planId)) {
            throw new CuentasAsociadasException("No se puede eliminar el plan, tiene cuentas asociadas.");
        }

        repo.deleteById(planId);
    }


    public List<Plan> buscarPlanes(Long idPlan, String nombre) {

        if (idPlan == null && nombre == null) {
            List<Plan> planes = repo.findAll();
            return planes;
        } else if (idPlan != null && nombre == null) {
            List<Plan> planes = repo.findById(idPlan)
                    .map(List::of)
                    .orElseGet(List::of);
            return planes;
        } else if (idPlan == null && nombre != null) {
            List<Plan> planes = repo.findByNombre(nombre);
            return planes;
        } else {
            List<Plan> planes = repo.findById(idPlan)
                    .filter(p -> nombre.equals(p.getNombre()))
                    .map(List::of)
                    .orElseGet(List::of);
            return planes;
        }
    }


}
