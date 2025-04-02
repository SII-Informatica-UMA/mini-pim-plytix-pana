package es.uma.informatica.sii.plytix.pana;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CommandLineRunner implements org.springframework.boot.CommandLineRunner {

	@Override
	@Transactional
	public void run(String... args) throws Exception {

		for (String s: args) {
			System.out.println(s);
		}

		}
	}


