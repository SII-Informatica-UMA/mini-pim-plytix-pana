package es.uma.informatica.sii.plytix.pana;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EntityScan("es.uma.informatica.sii.plytix.pana.entities")
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
