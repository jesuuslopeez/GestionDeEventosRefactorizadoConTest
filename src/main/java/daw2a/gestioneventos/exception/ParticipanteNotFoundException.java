package daw2a.gestioneventos.exception;

public class ParticipanteNotFoundException extends RuntimeException {
    public ParticipanteNotFoundException(Long id) {
        super("Participante no encontrado con id: " + id);
    }

    public ParticipanteNotFoundException(String usuario) {
        super("Participante no encontrado con usuario: " + usuario);
    }
}
