package es.uma.informatica.sii.plytix.pana.service;

import es.uma.informatica.sii.plytix.pana.dto.PlanDTO;
import es.uma.informatica.sii.plytix.pana.entities.Plan;
import es.uma.informatica.sii.plytix.pana.repositories.PlanRepository;
import es.uma.informatica.sii.plytix.pana.service.PLanNoEncontrado;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class PlanService {
    private PlanRepository repo;

    @Autowired
    public PlanService (PlanRepository repo){
        this.repo = repo;
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
}
