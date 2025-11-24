package daw2a.gestioneventos.exception;

public class EventoDuplicadoException extends RuntimeException {
    public EventoDuplicadoException(String nombre) {
        super("Ya existe un evento con nombre=" + nombre);
    }
}
