package es.uma.informatica.sii.plytix.pana.service;

import es.uma.informatica.sii.plytix.pana.dto.UsuarioDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class UsuarioServiceClient {
    private final RestTemplate restTemplate;

    @Value("${usuario.service.url}")
    private String usuarioServiceUrl;

    public UsuarioServiceClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public boolean isAdmin(Long usuarioId) {
        String url = UriComponentsBuilder.fromHttpUrl(usuarioServiceUrl)
                .path("/usuarios/{id}/isAdmin")
                .buildAndExpand(usuarioId)
                .toUriString();

        ResponseEntity<Boolean> response = restTemplate.getForEntity(
                url,
                Boolean.class
        );

        return response.getStatusCode().is2xxSuccessful() && Boolean.TRUE.equals(response.getBody());
    }
    public UsuarioDTO obtenerUsuarioPorId(Long usuarioId) {
        String url = UriComponentsBuilder.fromHttpUrl(usuarioServiceUrl)
                .path("/usuarios/{id}")
                .buildAndExpand(usuarioId)
                .toUriString();

        ResponseEntity<UsuarioDTO> response = restTemplate.getForEntity(
                url,
                UsuarioDTO.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Error al obtener usuario del servicio");
        }
    }

}
