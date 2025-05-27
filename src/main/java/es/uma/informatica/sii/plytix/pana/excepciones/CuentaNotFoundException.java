package es.uma.informatica.sii.plytix.pana.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // Añade esta anotación
public class CuentaNotFoundException extends RuntimeException {
  public CuentaNotFoundException(Long id) {
    super("Cuenta no encontrada con ID: " + id);
  }
}