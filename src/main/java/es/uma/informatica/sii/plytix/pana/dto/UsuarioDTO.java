package es.uma.informatica.sii.plytix.pana.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String email;

    public UsuarioDTO(Long id){
        this.id=id;
    }
    public UsuarioDTO(String email){
        Random rand = new Random();
        id = ThreadLocalRandom.current().nextLong(100);
        this.email=email;
    }


}
