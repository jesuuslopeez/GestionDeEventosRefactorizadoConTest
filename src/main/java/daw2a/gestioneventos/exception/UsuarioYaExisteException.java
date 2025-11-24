package daw2a.gestioneventos.exception;

public class UsuarioYaExisteException extends RuntimeException {
    public UsuarioYaExisteException(String usuario) {
        super("El usuario '" + usuario + "' ya está registrado");
    }
}