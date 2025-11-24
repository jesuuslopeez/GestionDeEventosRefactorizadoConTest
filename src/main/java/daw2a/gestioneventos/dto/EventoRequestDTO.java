package daw2a.gestioneventos.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// DTO (de entrada) para la creación o actualización de un evento
public class EventoRequestDTO {
    @NotBlank(message = "El nombre es obligatorio" )
    private String nombre;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String descripcion;

    @FutureOrPresent(message = "La fecha no puede ser anterior a hoy")
    private LocalDateTime fechaInicio;

    @FutureOrPresent(message = "La fecha debe ser igual o posterior a la fecha de inicio")
    private LocalDateTime fechaFin;

    private Long organizadorId;
}
