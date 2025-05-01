package es.uma.informatica.sii.plytix.pana.services;

import es.uma.informatica.sii.plytix.pana.entities.Plan;
import es.uma.informatica.sii.plytix.pana.repositories.CuentaRepository;
import es.uma.informatica.sii.plytix.pana.repositories.PlanRepository;
import es.uma.informatica.sii.plytix.pana.services.exceptions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CuentaService {

    private CuentaRepository cuentaRepository;
    private final PlanRepository planRepository;

    @Autowired
    public CuentaService(CuentaRepository cuentaRepository,
                         PlanRepository planRepository) {
        this.cuentaRepository = cuentaRepository;
        this.planRepository = planRepository;
    }

    /**
     * Elimina la cuenta solo si:
     *  - existe,
     *  - y ninguno de sus planes tiene productos, relaciones, activos o categorías.
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminarCuenta(Long cuentaId) {
        // 1) Comprobar existencia de la cuenta
        if (!cuentaRepository.existsById(cuentaId)) {
            throw new CuentaNoEncontradaException(cuentaId);
        }

        // 2) Recuperar todos los planes de esta cuenta
        List<Plan> planes = PlanRepository.findByCuentaId(cuentaId);

        // 3) para cada plan, comprobar que las cuatro colecciones están vacías
        for (Plan plan : planes) {
            boolean tieneContenido =
                    plan.getMaxProductos() == 0
                            && plan.getMaxRelaciones() == 0
                            && plan.getMaxActivos() == 0
                            && plan.getMaxCategoriasActivos() == 0;
            if (tieneContenido) {
                throw new RelacionesAsociadasException();
            }
        }

        // 4) si todo es correcto, eliminar la cuenta
        cuentaRepository.deleteById(cuentaId);
    }
}
