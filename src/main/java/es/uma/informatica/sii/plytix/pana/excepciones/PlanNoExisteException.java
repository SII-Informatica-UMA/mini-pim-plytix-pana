package es.uma.informatica.sii.plytix.pana.excepciones;

public class PlanNoExisteException extends RuntimeException {
    public PlanNoExisteException(String message) {
        super(message);
    }

    public PlanNoExisteException(){ super();}
}
