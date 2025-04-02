package es.uma.informatica.sii.plytix.pana;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.uma.informatica.sii.plytix.pana.repositories.UsuarioRepository;

@Component
public class CommandLineRunner implements org.springframework.boot.CommandLineRunner {
	private UsuarioRepository repository;
	public CommandLineRunner(UsuarioRepository repository) {
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


