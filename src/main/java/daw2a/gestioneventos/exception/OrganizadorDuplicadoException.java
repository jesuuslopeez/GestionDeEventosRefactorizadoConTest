package daw2a.gestioneventos.exception;

public class OrganizadorDuplicadoException extends RuntimeException {
    public OrganizadorDuplicadoException(String nombre) {
        super("Ya existe un organizador con el nombre: " + nombre);
    }
}
