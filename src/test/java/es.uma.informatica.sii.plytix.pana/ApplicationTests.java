package es.uma.informatica.sii.plytix.pana;

import es.uma.informatica.sii.plytix.pana.dto.CuentaDTO;
import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import es.uma.informatica.sii.plytix.pana.repositories.CuentaRepository;
import es.uma.informatica.sii.plytix.pana.Mapper.CuentaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.RequestEntity.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Test Microservicio Plan y Cuenta")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ApplicationTests {//headers.setBearerAuth("token");

    @Autowired
    private TestRestTemplate restTemplate;

    @Value(value="${local.server.port}")
    private int port;

    @Autowired
    private CuentaRepository cuentaRepository;

    @BeforeEach
    public void initializeDatabase() {
        cuentaRepository.deleteAll();
    }

    @Nested
    @DisplayName("cuando no hay cuentas (la lista está vacía)")
    public class ListaVacia {

        @Test
        @DisplayName("inserta una cuenta cuando la lista esta vacía")
        public void insertaCuenta() {
            Cuenta cuenta = new Cuenta(1L, "Plytix", null,
                                    "123456789A", null, null, new ArrayList<>(), null);

            RequestEntity<Cuenta> peticion = RequestEntity
                    .post(URI.create("http://localhost:" + port + "/cuenta"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(cuenta);

            var respuesta = restTemplate.exchange(peticion, Void.class);

            compruebaRespuesta(cuenta, respuesta);
        }

        private void compruebaRespuesta(Cuenta cuenta, ResponseEntity<Void> respuesta) {
            assertThat(respuesta.getStatusCode().value()).isEqualTo(201);

            List<Cuenta> cuentas = cuentaRepository.findAll();
            assertThat(cuentas).hasSize(1);
            assertThat(respuesta.getHeaders().get("Location").get(0))
                    .isEqualTo("http://localhost:"+port+"/cuenta/" + cuentas.get(0).getId());
        }
    }
    @Nested
    @DisplayName("cuando hay cuentas (la lista no está vacia)")
    public class ListaConDatos {
        @BeforeEach
        public void introduceDatos() {

            cuentaRepository.save(new Cuenta(1L, "Plytix", null,
                    "123456789A", null, null, new ArrayList<>(), 1L));

            cuentaRepository.save(new Cuenta(2L, "UMA", null,
                    "999999999B", null, null, new ArrayList<>(), 2L));

        }

        @Test
        @DisplayName("modifica el propietario de una cuenta dado su id")
        public void modificarPropietarioCuentaconId() {
            Long cuentaId = 1L;
            Long nuevoPropietarioId = 5L;

            URI uri = URI.create("http://localhost:" + port + "/cuenta/" + cuentaId + "/propietario?nuevoPropietarioId=" + nuevoPropietarioId);

            RequestEntity<Void> peticion = RequestEntity
                    .post(uri)
                    .build();

            ResponseEntity<Void> respuesta = restTemplate.exchange(peticion, Void.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);

            //  Verificar respuesta
            assertEquals(HttpStatus.OK, respuesta.getStatusCode());

            //  Verificar en base de datos
            Optional<Cuenta> cuentaActualizada = cuentaRepository.findById(cuentaId);
            assertTrue(cuentaActualizada.isPresent());
            assertEquals(nuevoPropietarioId, cuentaActualizada.get().getDuenoId());

        }
        @Test
        @DisplayName("modifica el propietario de una cuenta dado su email")
        public void modificarPropietarioCuentaconEmail() {
            Long cuentaId = 1L;
            String nuevoPropietarioEmail = "jose@uma.es";

            URI uri = URI.create("http://localhost:" + port + "/cuenta/" + cuentaId + "/propietario?email=" + nuevoPropietarioEmail);

            RequestEntity<Void> peticion = RequestEntity
                    .post(uri)
                    .build();

            ResponseEntity<Void> respuesta = restTemplate.exchange(peticion, Void.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);

            //  Verificar respuesta
            assertEquals(HttpStatus.OK, respuesta.getStatusCode());

            //  Verificar en base de datos
            Optional<Cuenta> cuentaActualizada = cuentaRepository.findById(cuentaId);
            assertTrue(cuentaActualizada.isPresent());


        }
        @Test
        @DisplayName("falla al modificar propietario de cuenta inexistente")
        public void modificarPropietarioCuenta_cuentaNoExiste() {

            Long cuentaId = 99L;
            Long nuevoPropietarioId = 1L;

            URI uri = URI.create("http://localhost:" + port + "/cuenta/" + cuentaId + "/propietario?nuevoPropietarioId=" + nuevoPropietarioId);

            RequestEntity<Void> peticion = RequestEntity
                    .post(uri)
                    .build();

            ResponseEntity<Void> respuesta = restTemplate.exchange(peticion, Void.class);

            assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
            assertThat(respuesta.hasBody()).isEqualTo(false);
        }

    }
}
