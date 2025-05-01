package es.uma.informatica.sii.plytix.pana.service;

import es.uma.informatica.sii.plytix.pana.dto.CuentaDTO;
import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import es.uma.informatica.sii.plytix.pana.entities.Plan;
import es.uma.informatica.sii.plytix.pana.repositories.CuentaRepository;
import es.uma.informatica.sii.plytix.pana.repositories.PlanRepository;
import es.uma.informatica.sii.plytix.pana.service.Exceptions.CuentaNoEncontrada;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class CuentaService {

    private CuentaRepository repo;

    @Autowired
    public CuentaService (CuentaRepository repo){
        this.repo = repo;
    }

    //put
    public void modificarCuenta (CuentaDTO cuenta, Long id){

        Optional<Cuenta> c = repo.findById(id);
        if(!c.isPresent()){
            throw new CuentaNoEncontrada();
        }
        Cuenta cu = c.get();
        cu.setNombre(cuenta.getNombre());
        cu.setDireccionFiscal(cuenta.getDireccionFiscal());
        cu.setnif(cuenta.getnif());
        cu.setFechaAlta(cuenta.getFechaAlta());
        cu.setPlan(cuenta.getPlan());
    }

}
