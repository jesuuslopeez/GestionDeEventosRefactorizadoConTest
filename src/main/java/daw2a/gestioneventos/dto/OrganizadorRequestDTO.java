package daw2a.gestioneventos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizadorRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
}
