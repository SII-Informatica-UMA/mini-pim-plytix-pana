package es.uma.informatica.sii.plytix.pana.dto;

import lombok.Data;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Data
public class UsuarioDTO {
    private Long id;
    private String email;

    public UsuarioDTO(){
        Random rand = new Random();
        id = ThreadLocalRandom.current().nextLong(100);
    }
    public UsuarioDTO(Long id){
        this.id=id;
    }
    public UsuarioDTO(String email){
        Random rand = new Random();
        id = ThreadLocalRandom.current().nextLong(100);
        this.email=email;
    }

    public UsuarioDTO(Long id, String email){
        this.id = id;
        this.email=email;
    }

    public String getEmail() { return this.email; }

    public Long getId() { return this.id; }

    public void setId(long id) { this.id = id; }
}
