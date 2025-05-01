package es.uma.informatica.sii.plytix.pana.Mapper;

import es.uma.informatica.sii.plytix.pana.dto.CuentaDTO;
import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import es.uma.informatica.sii.plytix.pana.entities.Plan;

public class CuentaMapper {

    public static Cuenta toEntity(CuentaDTO dto) {
        Cuenta cuenta = new Cuenta();
        cuenta.setNombre(dto.getNombre());
        cuenta.setDireccionFiscal(dto.getDireccionFiscal()); // Asume que en el DTO el campo se llama "direccion"
        cuenta.setNIF(dto.getnif());
        cuenta.setFechaAlta(dto.getFechaAlta());

        // Relación con Plan (solo ID)
        if (dto.getPlan().getId() != null) {
            Plan plan = new Plan();
            plan.setId(dto.getPlan().getId());
            cuenta.setPlan(plan);
        }

        return cuenta;
    }

    public static CuentaDTO toDTO(Cuenta entity) {
        CuentaDTO dto = new CuentaDTO();
        dto.setNombre(entity.getNombre());
        dto.setDireccionFiscal(entity.getDireccionFiscal());
        dto.setnif(entity.getNIF());
        dto.setFechaAlta(entity.getFechaAlta());

        if (entity.getPlan() != null) {
            dto.getPlan().setId(entity.getPlan().getId());
        }

        return dto;
    }
}
