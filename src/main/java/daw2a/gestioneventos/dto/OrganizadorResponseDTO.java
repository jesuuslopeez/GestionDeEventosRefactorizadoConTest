package daw2a.gestioneventos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrganizadorResponseDTO {
    private final Long id;
    private final String nombre;
    private final List<Long> eventosIds;
}
