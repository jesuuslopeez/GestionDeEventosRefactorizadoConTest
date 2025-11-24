package daw2a.gestioneventos.servicio;

import daw2a.gestioneventos.dominio.Evento;
import daw2a.gestioneventos.dominio.Organizador;
import daw2a.gestioneventos.dto.EventoRequestDTO;
import daw2a.gestioneventos.dto.EventoResponseDTO;
import daw2a.gestioneventos.exception.EventoDuplicadoException;
import daw2a.gestioneventos.exception.EventoNoEncontradoException;
import daw2a.gestioneventos.exception.OrganizadorNotFoundException;
import daw2a.gestioneventos.mapper.EventoMapper;
import daw2a.gestioneventos.repo.EventoRepo;
import daw2a.gestioneventos.repo.OrganizadorRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class EventoServicio {
    private final EventoRepo eventoRepo;
    private final OrganizadorRepo organizadorRepo;
    public EventoServicio(EventoRepo eventoRepo, OrganizadorRepo organizadorRepo) {
        this.eventoRepo = eventoRepo;
        this.organizadorRepo = organizadorRepo;
    }

    public Page<EventoResponseDTO> listarEventos(Pageable pageable){
        Page<EventoResponseDTO> eventos = eventoRepo.findAll(pageable)
                .map(EventoMapper::toDTO);
        return eventos;
    }

    public EventoResponseDTO obtenEventoPorId(Long id){
        Evento evento = eventoRepo.findById(id)
                         .orElseThrow(() -> new EventoNoEncontradoException(id));
        return EventoMapper.toDTO(evento);
    }
    public EventoResponseDTO obtenEventoPorNombre(String nombre){
        Evento evento = eventoRepo.findByNombre(nombre);
        if (evento  == null) {
            throw new EventoNoEncontradoException(nombre);
        }
        return EventoMapper.toDTO(evento);
    }

    public EventoResponseDTO crearEvento(EventoRequestDTO dto){
        // Validar si el evento ya existe por nombre
        if(eventoRepo.existsByNombre(dto.getNombre())){
            throw new EventoDuplicadoException(dto.getNombre());
        }
        //Validiar si el organizador existe
        Organizador organizador = organizadorRepo.findById(dto.getOrganizadorId())
                .orElseThrow(() -> new OrganizadorNotFoundException(dto.getOrganizadorId()));
        // Mapear DTO a entidad
        Evento evento = EventoMapper.toEntity(dto);
        evento.setOrganizador(organizador); // <-- Asignar el organizador al evento

        // Guardar y retornar el evento
        Evento guardado = eventoRepo.save(evento);
        return EventoMapper.toDTO(guardado);
    }

    public EventoResponseDTO actualizarEvento(Long id, EventoRequestDTO dto) {
        // Buscar el evento existente
        Evento existente = eventoRepo.findById(id).orElseThrow(()-> new EventoNoEncontradoException(id));

        // Validar si el organizador existe
        Organizador organizador = organizadorRepo.findById(dto.getOrganizadorId())
                .orElseThrow(() -> new OrganizadorNotFoundException(dto.getOrganizadorId()));
        // Actualizar el organizador si es necesario
        existente.setOrganizador(organizador);
        // Actualizar campos básicos
        actualizaCamposBasicos(dto, existente);

        // Actualizar participantes
        // En lugar de reemplazar la lista
        /*if (dto.getParticipantes() != null && !dto.getParticipantes().isEmpty()) {
            List<Participante> participantesActuales = existente.getParticipantes();
            if (participantesActuales == null) {
                participantesActuales = new ArrayList<>();
            }
            // Añadir solo los participantes que no estén ya en la lista
            for (Participante participante : dto.getParticipantes()) {
                if (!participantesActuales.contains(participante)) {
                    participantesActuales.add(participante);
                }
            }
            existente.setParticipantes(participantesActuales);
        }*/
        Evento actualizado = eventoRepo.save(existente);
        return EventoMapper.toDTO(actualizado);
    }

    private void actualizaCamposBasicos(EventoRequestDTO evento, Evento eventoActualizado) {
        Optional.ofNullable(evento.getNombre()).ifPresent(eventoActualizado::setNombre);
        Optional.ofNullable(evento.getDescripcion()).ifPresent(eventoActualizado::setDescripcion);
        Optional.ofNullable(evento.getFechaInicio()).ifPresent(eventoActualizado::setFechaInicio);
        Optional.ofNullable(evento.getFechaFin()).ifPresent(eventoActualizado::setFechaFin);
        //Optional.ofNullable(evento.getTipo()).ifPresent(eventoActualizado::setTipo);

    }


    public void eliminarEvento(Long id) {
        Evento evento = eventoRepo.findById(id)
                .orElseThrow(() -> new EventoNoEncontradoException(id));
        eventoRepo.delete(evento);
    }
}


