package es.uma.informatica.sii.plytix.pana.dto;

import lombok.Data;

@Data
public class PropietarioDTO {
    private Long id;
    private String email;
    private String nombre;
    private String apellido1;
    private String apellido2;
}