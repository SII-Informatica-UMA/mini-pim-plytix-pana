package es.uma.informatica.sii.plytix.pana;

import es.uma.informatica.sii.plytix.pana.Mapper.PlanMapper;
import es.uma.informatica.sii.plytix.pana.dto.CuentaDTO;
import es.uma.informatica.sii.plytix.pana.dto.PlanDTO;
import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import es.uma.informatica.sii.plytix.pana.entities.Plan;
import es.uma.informatica.sii.plytix.pana.repositories.CuentaRepository;
import es.uma.informatica.sii.plytix.pana.Mapper.CuentaMapper;
import es.uma.informatica.sii.plytix.pana.repositories.PlanRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;
import es.uma.informatica.sii.plytix.pana.controller.PlanController;

import java.net.URI;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.RequestEntity.post;


@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
    @DisplayName("cuando hay proyectos (la lista no está vacia)")
    public class ListaConDatos {
        @BeforeEach
        public void introduceDatos() {
            List<Cuenta> cuentas = new ArrayList<>();
            Plan p = new Plan(
                    13L,                      // id
                    "Plan Premium",          // nombre
                    100L,                    // maxProductos
                    50L,                     // maxActivos
                    1024L,                   // maxAlmacenamiento (en MB, por ejemplo)
                    10L,                     // maxCategoriasProductos
                    5L,                      // maxCategoriasActivos
                    20L,                     // maxRelaciones
                    29.99,                   // precio
                    cuentas                  // lista de cuentas
            );
            planRepository.save(p);
            cuentaRepository.save(new Cuenta(
                    124L,                           // id
                    "Cuenta2",              // nombre
                    "Calle 43",     // dirección fiscal
                    "B12342278",                    // NIF
                    new Date(),                     // fechaAlta (fecha actual)
                    p, // plan
                    Arrays.asList(101L, 103L, 103L), // lista de IDs de usuarios
                    10L                           // ID del dueño
            ));
            cuentaRepository.save(new Cuenta(
                    123456789L,                           // id
                    "Cuenta3",              // nombre
                    "Calle 40",     // dirección fiscal
                    "B1678",                    // NIF
                    new Date(),                     // fechaAlta (fecha actual)
                    p, // plan
                    Arrays.asList(101L, 102L, 108L), // lista de IDs de usuarios
                    2001L                           // ID del dueño
            ));
        }

        @Nested
        @DisplayName("al modificar una cuenta")
        public class ModificarCuentas {

            @Test
            @DisplayName("modifica los datos de una cuenta existente")
            public void modificaCuenta() {
                Cuenta original = cuentaRepository.findAll().get(0);
                CuentaDTO cuentaDTO = CuentaMapper.toDTO(original);
                cuentaDTO.setNombre("Cuenta Actualizada");

                var peticion = put("http", "localhost", port, "/cuenta/" + original.getId(),cuentaDTO);
                var respuesta = restTemplate.exchange(peticion, Void.class);

                assertThat(respuesta.getStatusCode().value()).isEqualTo(200);

                Optional<Cuenta> cuentaBD = cuentaRepository.findById(original.getId());
                Cuenta pr = cuentaBD.get();
                assertThat(pr.getNombre()).isEqualTo("Cuenta Actualizada");//cambiar por comprueba campos
            }

            @Test
            @DisplayName("devuelve error al modificar una cuenta que no existe")
            public void modificarCuentaInexistente() {
                Cuenta c = new Cuenta(
                        123L,                           // id
                        "Cuenta1",              // nombre
                        "Calle 45",     // dirección fiscal
                        "B12345678",                    // NIF
                        new Date(),                     // fechaAlta (fecha actual)
                        new Plan(), // plan
                        Arrays.asList(101L, 102L, 103L), // lista de IDs de usuarios
                        1001L                           // ID del dueño
                );
                CuentaDTO cuentaDTO = CuentaMapper.toDTO(c);
                var peticion = put("http", "localhost", port, "/cuenta/",cuentaDTO);
                var respuesta = restTemplate.exchange(peticion, Void.class);

                assertThat(respuesta.getStatusCode().value()).isEqualTo(404);


            }
        }

        @Nested
        @DisplayName("al modificar un plan")
        public class ModificarPlanes {

            @Test
            @DisplayName("modifica los datos de un plan existente")
            public void modificaPlan() {

                Plan p = planRepository.findAll().get(0);
                PlanDTO planDTO = PlanMapper.toDTO(p);
                planDTO.setNombre("Plan Actualizado");


                var peticion = put("http", "localhost", port, "/plan/" + p.getId(),planDTO);
                var respuesta = restTemplate.exchange(peticion, Void.class);

                assertThat(respuesta.getStatusCode().value()).isEqualTo(200);

                Optional<Plan> planBD = planRepository.findById(p.getId());
                Plan pr = planBD.get();
                assertThat(pr.getNombre()).isEqualTo("Plan Actualizado");
            }

            @Test
            @DisplayName("devuelve error al modificar un plan que no existe")
            public void modificarPlanInexistente() {

                List<Cuenta> cuentas = new ArrayList<>();

                Plan p = new Plan(
                        14L,                      // id
                        "Plan Básico",           // nombre
                        10L,                     // maxProductos
                        5L,                      // maxActivos
                        256L,                    // maxAlmacenamiento (por ejemplo en MB)
                        2L,                      // maxCategoriasProductos
                        1L,                      // maxCategoriasActivos
                        3L,                      // maxRelaciones
                        4.99,                    // precio
                        cuentas                  // lista de cuentas asociadas
                );

                PlanDTO planDTO = PlanMapper.toDTO(p);

                var peticion = put("http", "localhost", port, "/plan/",planDTO);
                var respuesta = restTemplate.exchange(peticion, Void.class);

                assertThat(respuesta.getStatusCode().value()).isEqualTo(404);

            }
        }
    }

    @Nested
    @DisplayName("cuando no hay planes (la lista está vacía)")
    public class ListaVacia {

        @Test
        @DisplayName("inserta un plan en una lista vacía")
        public void insertaPlan() {

            List<Cuenta> cuentas = new ArrayList<>();

            var p = new Plan(
                    14L,                      // id
                    "Plan Básico",           // nombre
                    10L,                     // maxProductos
                    5L,                      // maxActivos
                    256L,                    // maxAlmacenamiento (por ejemplo en MB)
                    2L,                      // maxCategoriasProductos
                    1L,                      // maxCategoriasActivos
                    3L,                      // maxRelaciones
                    4.99,                    // precio
                    cuentas                  // lista de cuentas asociadas
            );

            var planDTO = PlanMapper.toDTO(p);


            var peticion = post("http", "localhost", port, "/plan/", planDTO);
            var respuesta = restTemplate.exchange(peticion, Void.class);
            assertThat(respuesta.getStatusCode().value()).isEqualTo(201);
            List<Plan> planes = planRepository.findAll();
            assertThat(planes).hasSize(1);
            assertThat(respuesta.getHeaders().getLocation().toString())
                    .isEqualTo("http://localhost:" + port + "/plan/" + planes.get(0).getId());
            compruebaCamposPlan(planDTO, planes.get(0));
        }
    }

}
