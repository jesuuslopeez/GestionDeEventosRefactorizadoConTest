package daw2a.gestioneventos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventoResponseDTO {
    private final Long id;
    private final String nombre;
    private final String fechaInicio;
    private final String fechaFin;
    private final Long organizadorId;

}
