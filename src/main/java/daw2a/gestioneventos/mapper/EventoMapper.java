package daw2a.gestioneventos.mapper;

import daw2a.gestioneventos.dominio.Evento;
import daw2a.gestioneventos.dto.EventoRequestDTO;
import daw2a.gestioneventos.dto.EventoResponseDTO;

import java.time.LocalDateTime;

public class EventoMapper {
    // Mapper de DTO de solicitud a entidad Sí se Usa
    public static Evento toEntity(EventoRequestDTO dto) {

        Evento evento = new Evento();
        evento.setNombre(dto.getNombre());
        evento.setDescripcion(dto.getDescripcion());
        evento.setFechaInicio(dto.getFechaInicio());
        evento.setFechaFin(dto.getFechaFin());

        return evento;
    }
    // Mapper de DTO de respuesta a entidad, probablemente No se Usará nunca
    public static Evento toEntity(EventoResponseDTO dto) {
        Evento evento = new Evento();
        evento.setId(dto.getId());
        evento.setNombre(dto.getNombre());
        evento.setFechaInicio(LocalDateTime.parse(dto.getFechaInicio()));
        evento.setFechaFin(LocalDateTime.parse(dto.getFechaFin()));

        return evento;
    }
    // Mapper de entidad a DTO de respuesta Sí se Usa
    public static EventoResponseDTO toDTO(Evento evento) {
        return new EventoResponseDTO(
                evento.getId(),
                evento.getNombre(),
                evento.getFechaInicio().toString(),
                evento.getFechaFin().toString(),
                evento.getOrganizador() != null ? evento.getOrganizador().getId() : null
        );
    }
    // Mapper de entidad a DTO de solicitud, probablemente No se Usará nunca
    public static EventoRequestDTO requestToDTO(Evento evento) {
        return new EventoRequestDTO(
                evento.getNombre(),
                evento.getDescripcion(),
                evento.getFechaInicio(),
                evento.getFechaFin(),
                evento.getOrganizador() != null ? evento.getOrganizador().getId() : null
        );
    }

}
