package es.uma.informatica.sii.plytix.pana.services.exceptions;

public class PlanNoEncontradoException extends RuntimeException{
    public PlanNoEncontradoException(Long idPlan) { super("El plan no existe: " + idPlan); }
}
