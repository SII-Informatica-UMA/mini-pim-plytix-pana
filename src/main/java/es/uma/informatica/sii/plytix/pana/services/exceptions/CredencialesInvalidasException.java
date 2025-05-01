package es.uma.informatica.sii.plytix.pana.services.exceptions;

public class CredencialesInvalidasException extends RuntimeException{

    CredencialesInvalidasException() { super("Credenciales no validas o faltantes"); }
}
