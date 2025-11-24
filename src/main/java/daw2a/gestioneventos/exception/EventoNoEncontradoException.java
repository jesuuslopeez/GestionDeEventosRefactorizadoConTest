package daw2a.gestioneventos.exception;

public class EventoNoEncontradoException extends RuntimeException {
    public EventoNoEncontradoException(Long id) {
        super("Evento no encontrado con id=" + id);
    }

    public EventoNoEncontradoException(String nombre) {
        super("Evento no encontrado con nombre=" + nombre);
    }

    public EventoNoEncontradoException() {
        super("No hay eventos disponibles");
    }
}