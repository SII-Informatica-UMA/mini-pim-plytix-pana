package es.uma.informatica.sii.plytix.pana;

import es.uma.informatica.sii.plytix.pana.dto.CuentaDTO;
import es.uma.informatica.sii.plytix.pana.dto.PlanDTO;
import es.uma.informatica.sii.plytix.pana.dto.UsuarioDTO;
import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import es.uma.informatica.sii.plytix.pana.entities.Plan;
import es.uma.informatica.sii.plytix.pana.repositories.CuentaRepository;
import es.uma.informatica.sii.plytix.pana.Mapper.CuentaMapper;
import es.uma.informatica.sii.plytix.pana.repositories.PlanRepository;
import es.uma.informatica.sii.plytix.pana.service.UsuarioServiceClient;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;

import java.net.URI;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.RequestEntity.post;


@SpringBootTest(classes = {Application.class, TestSecurityConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApplicationTests {

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
        planRepository.deleteAll();
    }

    private URI uri(String scheme, String host, int port, String ...paths) {
        UriBuilderFactory ubf = new DefaultUriBuilderFactory();
        UriBuilder ub = ubf.builder()
                .scheme(scheme)
                .host(host).port(port);
        for (String path: paths) {
            ub = ub.path(path);
        }
        return ub.build();
    }

    private RequestEntity<Void> get(String scheme, String host, int port, String path) {
        URI uri = uri(scheme, host,port, path);
        return RequestEntity.get(uri)
                .accept(MediaType.APPLICATION_JSON)
                .build();
    }

    private RequestEntity<Void> delete(String scheme, String host, int port, String path) {
        URI uri = uri(scheme, host,port, path);
        return RequestEntity.delete(uri)
                .build();

    }

    private <T> RequestEntity<T> post(String scheme, String host, int port, String path, T object) {
        URI uri = uri(scheme, host,port, path);
        return RequestEntity.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(object);

    }

    private <T> RequestEntity<T> put(String scheme, String host, int port, String path, T object) {
        URI uri = uri(scheme, host,port, path);
        return RequestEntity.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(object);

    }
    private void compruebaCamposCuenta(Cuenta expected, Cuenta actual) {
        assertThat(actual.getFechaAlta()).isEqualTo(expected.getFechaAlta());
        assertThat(actual.getNombre()).isEqualTo(expected.getNombre());
        assertThat(actual.getId()).isEqualTo(expected.getId());
    }

    private void compruebaCamposPlan(PlanDTO expected, Plan actual) {
        assertThat(actual.getNombre()).isEqualTo(expected.getNombre());
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
            ResponseEntity<UsuarioDTO> respuesta = restTemplate.exchange(
                    get("http", "localhost", port, "/api/planificador/cuenta/100/propietario"),
                    UsuarioDTO.class
            );
            assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(respuesta.getBody().getId()).isEqualTo(200L);
        }
        @Test
        @DisplayName("GET /cuenta/{idCuenta}/propietario - Cuenta no existe devuelve 404")
        public void obtenerPropietarioCuentaNoExistente() {
            ResponseEntity<Void> respuesta = restTemplate.exchange(
                    get("http", "localhost", port, "/cuenta/999/propietario"),
                    Void.class
            );

            assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
        @Test
        @DisplayName("GET /cuenta/{idCuenta}/usuarios - Devuelve 404 para cuenta inexistente")
        public void obtenerUsuariosCuentaInexistente() {
            Long idNoExiste = 999999L;
            ResponseEntity<Void> respuesta = restTemplate.exchange(
                    get("http", "localhost", port, "/cuenta/" + idNoExiste + "/usuarios?idUsuario=123"),
                    Void.class
            );
            assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

    }
}
