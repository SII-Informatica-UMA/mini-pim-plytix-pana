package es.uma.informatica.sii.plytix.pana.services.exceptions;

public class RelacionesAsociadasException extends RuntimeException{

    public RelacionesAsociadasException() { super("Sin permisos suficientes o con algún recurso asociado (producto, activo, categoría de producto o activo, o relación)."); }
}
