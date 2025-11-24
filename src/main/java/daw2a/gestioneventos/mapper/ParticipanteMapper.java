package daw2a.gestioneventos.mapper;

import daw2a.gestioneventos.dominio.Participante;
import daw2a.gestioneventos.dto.ParticipanteRequestDTO;
import daw2a.gestioneventos.dto.ParticipanteResponseDTO;

public class ParticipanteMapper {

    public static Participante toEntity(ParticipanteRequestDTO dto) {
        Participante participante = new Participante();
        participante.setNombre(dto.getNombre());
        participante.setUsuario(dto.getUsuario());
        participante.setContrasenia(dto.getContrasenia());
        return participante;
    }

    public static ParticipanteResponseDTO toDTO(Participante participante) {
        return new ParticipanteResponseDTO(
                participante.getId(),
                participante.getNombre(),
                participante.getUsuario(),
                participante.getEvento() != null ? participante.getEvento().getId() : null,
                participante.getEvento() != null ? participante.getEvento().getNombre() : null
        );
    }
}
