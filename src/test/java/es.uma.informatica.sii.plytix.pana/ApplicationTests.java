package es.uma.informatica.sii.plytix.pana;

import es.uma.informatica.sii.plytix.pana.dto.CuentaDTO;
import es.uma.informatica.sii.plytix.pana.dto.UsuarioDTO;
import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import es.uma.informatica.sii.plytix.pana.entities.Plan;
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
import es.uma.informatica.sii.plytix.pana.repositories.PlanRepository;
import es.uma.informatica.sii.plytix.pana.service.UsuarioServiceClient;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.RequestEntity.*;

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

    @Autowired
    private PlanRepository planRepository;
    @MockBean
    private UsuarioServiceClient usuarioServiceClient;

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
        @Test
        @DisplayName("modifica la lista de usuarios de una cuenta dado su id")
        public void modificarUsuarios() {
            Long cuentaId = 1L;
            List<UsuarioDTO> listaUsuarios = new ArrayList<>();

            listaUsuarios.add(new UsuarioDTO(1L, null));
            listaUsuarios.add(new UsuarioDTO(null, "ana@example.com"));
            listaUsuarios.add(new UsuarioDTO(2L, "carlos@example.com"));
            listaUsuarios.add(new UsuarioDTO(null, null));

            URI uri = URI.create("http://localhost:" + port + "/cuenta/" + cuentaId + "/usuarios");

            RequestEntity<List<UsuarioDTO>> peticion = RequestEntity
                    .post(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(listaUsuarios);

            ResponseEntity<Void> respuesta = restTemplate.exchange(peticion, Void.class);

            assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
            assertEquals(HttpStatus.OK, respuesta.getStatusCode());

            // Verificar en base de datos
            Optional<Cuenta> cuentaActualizada = cuentaRepository.findById(cuentaId);
            assertTrue(cuentaActualizada.isPresent());

            // Obtener los IDs que deberían haberse guardado (ignorando los usuarios sin ID)
            List<Long> idsEsperados = listaUsuarios.stream()
                    .map(UsuarioDTO::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // Verificar que los IDs están en la cuenta
            List<Long> idsEnCuenta = cuentaActualizada.get().getUsuarios();
            assertNotNull(idsEnCuenta);
            assertTrue(idsEnCuenta.containsAll(idsEsperados));
        }

        @Test
        @DisplayName("falla al modificar la lista de usuarios de una cuenta inexistente")
        public void modificarUsuarios_CuentaNoExiste() {
            Long cuentaId = 99L;
            List<UsuarioDTO> listaUsuarios = new ArrayList<>();

            listaUsuarios.add(new UsuarioDTO(1L, null));
            listaUsuarios.add(new UsuarioDTO(null, "ana@example.com"));
            listaUsuarios.add(new UsuarioDTO(2L, "carlos@example.com"));
            listaUsuarios.add(new UsuarioDTO(null, null));

            URI uri = URI.create("http://localhost:" + port + "/cuenta/" + cuentaId + "/usuarios");

            RequestEntity<List<UsuarioDTO>> peticion = RequestEntity
                    .post(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(listaUsuarios);

            ResponseEntity<Void> respuesta = restTemplate.exchange(peticion, Void.class);

            assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
            assertFalse(respuesta.hasBody());

            List<Long> idsEsperados = listaUsuarios.stream()
                    .map(UsuarioDTO::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            Optional<Cuenta> cuentaOpt = cuentaRepository.findById(cuentaId);
            assertTrue(cuentaOpt.isEmpty());

        }

    }

    @Nested
    @DisplayName("Pruebas de GET")
    public class CuentaGetTests {

        private Plan plan;
        private Cuenta cuenta1;
        private Cuenta cuenta2;

        @BeforeEach
        public void setup() {
            when(usuarioServiceClient.isAdmin(200L)).thenReturn(true); // Admin user
            when(usuarioServiceClient.isAdmin(anyLong())).thenAnswer(inv -> {
                Long userId = inv.getArgument(0);
                return userId.equals(200L); // Solo el usuario 200 es admin
            });

            // Configurar mock para obtenerUsuarioPorId
            UsuarioDTO usuarioMock = new UsuarioDTO();
            usuarioMock.setId(200L);
            when(usuarioServiceClient.obtenerUsuarioPorId(200L)).thenReturn(usuarioMock);

            // Crear datos de prueba
            plan = new Plan(1L);
            plan.setNombre("Plan Premium");
            planRepository.save(plan);

            cuenta1 = new Cuenta(
                    100L,
                    "Cuenta Ejemplo 1",
                    "Calle Falsa 123",
                    "B12345678",
                    new Date(),
                    plan,
                    Arrays.asList(101L, 102L),
                    200L // dueñoId
            );

            cuenta2 = new Cuenta(
                    200L,
                    "Cuenta Ejemplo 2",
                    "Avenida Real 45",
                    "B87654321",
                    new Date(),
                    plan,
                    Arrays.asList(201L, 202L),
                    300L // dueñoId
            );

            cuentaRepository.saveAll(Arrays.asList(cuenta1, cuenta2));
        }
        @Test
        @DisplayName("GET /cuenta/{idCuenta}/propietario - Obtener propietario de cuenta existente")
        public void obtenerPropietarioCuentaExistente() {

            URI uri = URI.create("http://localhost:" + port + "/cuenta/100/propietario");

            ResponseEntity<UsuarioDTO> respuesta = restTemplate.exchange(
                    get(uri).build(),
                    UsuarioDTO.class
            );
            assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(respuesta.getBody().getId()).isEqualTo(200L);
        }
        @Test
        @DisplayName("GET /cuenta/{idCuenta}/propietario - Cuenta no existe devuelve 404")
        public void obtenerPropietarioCuentaNoExistente() {

            URI uri = URI.create("http://localhost:" + port + "/cuenta/999/propietario");
            ResponseEntity<Void> respuesta = restTemplate.exchange(
                    get(uri).build(),
                    Void.class
            );

            assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(respuesta.hasBody()).isEqualTo(false);
        }

    }
}
