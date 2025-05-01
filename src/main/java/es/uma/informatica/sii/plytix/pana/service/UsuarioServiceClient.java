package es.uma.informatica.sii.plytix.pana.service;

import es.uma.informatica.sii.plytix.pana.dto.PropietarioDTO;
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

    public PropietarioDTO obtenerUsuarioPorId(Long usuarioId) {
        String url = UriComponentsBuilder.fromHttpUrl(usuarioServiceUrl)
                .path("/usuarios/{id}")
                .buildAndExpand(usuarioId)
                .toUriString();

        ResponseEntity<PropietarioDTO> response = restTemplate.getForEntity(
                url,
                PropietarioDTO.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Error al obtener usuario del servicio");
        }
    }
}