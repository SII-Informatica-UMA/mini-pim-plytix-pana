package es.uma.informatica.sii.plytix.pana.excepciones;

public class CuentaNotFoundException extends RuntimeException {
  public CuentaNotFoundException(Long message) {
    super(String.valueOf(message));
  }
}
