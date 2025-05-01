package es.uma.informatica.sii.plytix.pana.service;

public class CuentaNotFoundException extends RuntimeException {
    public CuentaNotFoundException(Long id) {
        super("Cuenta no encontrada con ID: " + id);
    }
}
