package es.uma.informatica.sii.plytix.pana.excepciones;

public class PlanNoEncontrado extends RuntimeException {
    public PlanNoEncontrado(String message) {
        super(message);
    }
}
