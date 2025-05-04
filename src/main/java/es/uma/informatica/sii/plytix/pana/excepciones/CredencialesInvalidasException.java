package es.uma.informatica.sii.plytix.pana.excepciones;

public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String message) {
        super(message);
    }
}
