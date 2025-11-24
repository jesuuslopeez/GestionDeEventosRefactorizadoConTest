package daw2a.gestioneventos.mapper;

import daw2a.gestioneventos.dominio.Evento;
import daw2a.gestioneventos.dominio.Organizador;
import daw2a.gestioneventos.dto.OrganizadorRequestDTO;
import daw2a.gestioneventos.dto.OrganizadorResponseDTO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OrganizadorMapper {

    public static Organizador toEntity(OrganizadorRequestDTO dto) {
        Organizador organizador = new Organizador();
        organizador.setNombre(dto.getNombre());
        return organizador;
    }

    public static OrganizadorResponseDTO toDTO(Organizador organizador) {
        List<Long> eventosIds = organizador.getEventos() != null
                ? organizador.getEventos().stream()
                    .map(Evento::getId)
                    .collect(Collectors.toList())
                : Collections.emptyList();

        return new OrganizadorResponseDTO(
                organizador.getId(),
                organizador.getNombre(),
                eventosIds
        );
    }
}
