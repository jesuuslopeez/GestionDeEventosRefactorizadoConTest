package daw2a.gestioneventos.exception;

public class OrganizadorNotFoundException extends RuntimeException {
    public OrganizadorNotFoundException(Long id) {
        super("No se encontró el organizador con id=" + id);
    }
}
