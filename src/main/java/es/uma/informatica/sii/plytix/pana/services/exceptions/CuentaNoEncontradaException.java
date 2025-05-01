package es.uma.informatica.sii.plytix.pana.services.exceptions;

public class CuentaNoEncontradaException extends RuntimeException{

    public CuentaNoEncontradaException(Long id) { super("Cuenta no encontrada"); }
}
