package daw2a.gestioneventos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParticipanteResponseDTO {
    private final Long id;
    private final String nombre;
    private final String usuario;
    private final Long eventoId;
    private final String eventoNombre;
}
