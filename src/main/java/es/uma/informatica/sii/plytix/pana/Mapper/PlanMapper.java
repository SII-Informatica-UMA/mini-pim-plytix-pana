package es.uma.informatica.sii.plytix.pana.Mapper;

import es.uma.informatica.sii.plytix.pana.dto.CuentaDTO;
import es.uma.informatica.sii.plytix.pana.dto.PlanDTO;
import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import es.uma.informatica.sii.plytix.pana.entities.Plan;

public class PlanMapper {

    public static PlanDTO toDTO(Plan entity) {
        PlanDTO dto = new PlanDTO(entity.getNombre(), entity.getMaxProductos(),entity.getMaxActivos(),entity.getMaxAlmacenamiento(),entity.getMaxCategoriasProductos(),entity.getMaxCategoriasActivos(),entity.getMaxRelaciones(),entity.getPrecio());
        /*dto.setNombre(entity.getNombre());
        dto.setMaxCategoriasActivos(entity.getMaxCategoriasActivos());
        dto.setMaxCategoriasProductos(entity.getMaxCategoriasProductos());
        dto.setMaxActivos(entity.getMaxActivos());
        dto.setPrecio(entity.getPrecio());
        dto.setMaxAlmacenamiento(entity.getMaxAlmacenamiento());
        dto.setMaxProductos(entity.getMaxProductos());
        dto.setMaxRelaciones(entity.getMaxRelaciones());*/

        return dto;
    }
}
