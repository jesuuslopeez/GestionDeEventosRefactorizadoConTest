package daw2a.gestioneventos.exception;

public class ParticipanteDuplicadoException extends RuntimeException {
    public ParticipanteDuplicadoException(String usuario) {
        super("Ya existe un participante con el usuario: " + usuario);
    }
}