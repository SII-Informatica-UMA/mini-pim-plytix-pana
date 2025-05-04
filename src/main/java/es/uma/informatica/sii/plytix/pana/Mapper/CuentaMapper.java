package es.uma.informatica.sii.plytix.pana.Mapper;

import es.uma.informatica.sii.plytix.pana.dto.CuentaDTO;
import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import es.uma.informatica.sii.plytix.pana.entities.Plan;

public class CuentaMapper {

    public static Cuenta toEntity(CuentaDTO dto) {
        Cuenta cuenta = new Cuenta();
        cuenta.setNombre(dto.getNombre());
        cuenta.setDireccionFiscal(dto.getDireccion()); // Asume que en el DTO el campo se llama "direccion"
        cuenta.setNIF(dto.getNif());
        cuenta.setFechaAlta(dto.getFechaAlta());

        // Relación con Plan (solo ID)
        if (dto.getPlanId() != null) {
            Plan plan = new Plan();
            plan.setId(dto.getPlanId());
            cuenta.setPlan(plan);
        }

        return cuenta;
    }

    public static CuentaDTO toDTO(Cuenta entity) {
        CuentaDTO dto = new CuentaDTO();
        dto.setNombre(entity.getNombre());
        dto.setDireccion(entity.getDireccionFiscal());
        dto.setNif(entity.getNIF());
        dto.setFechaAlta(entity.getFechaAlta());

        if (entity.getPlan() != null) {
            dto.setPlanId(entity.getPlan().getId());
        }

        return dto;
    }
}
