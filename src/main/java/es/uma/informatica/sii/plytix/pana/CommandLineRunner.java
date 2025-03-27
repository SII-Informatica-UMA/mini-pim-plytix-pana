package es.uma.informatica.sii.plytix.pana;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.uma.informatica.sii.plytix.pana.entities.Cuenta;
import es.uma.informatica.sii.plytix.pana.repositories.BookRepository;

@Component
public class CommandLineRunner implements org.springframework.boot.CommandLineRunner {
	private BookRepository repository;
	public CommandLineRunner(BookRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional
	public void run(String... args) throws Exception {

		for (String s: args) {
			System.out.println(s);
		}
	}

}
