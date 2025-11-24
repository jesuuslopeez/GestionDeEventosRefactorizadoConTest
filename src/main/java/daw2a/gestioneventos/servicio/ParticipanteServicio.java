package daw2a.gestioneventos.servicio;

import daw2a.gestioneventos.dominio.Evento;
import daw2a.gestioneventos.dominio.Participante;
import daw2a.gestioneventos.dto.ParticipanteRequestDTO;
import daw2a.gestioneventos.dto.ParticipanteResponseDTO;
import daw2a.gestioneventos.exception.EventoNoEncontradoException;
import daw2a.gestioneventos.exception.ParticipanteNotFoundException;
import daw2a.gestioneventos.exception.UsuarioYaExisteException;
import daw2a.gestioneventos.mapper.ParticipanteMapper;
import daw2a.gestioneventos.repo.EventoRepo;
import daw2a.gestioneventos.repo.ParticipanteRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ParticipanteServicio {
    private final ParticipanteRepo participanteRepo;
    private final EventoRepo eventoRepo;

    public ParticipanteServicio(ParticipanteRepo participanteRepo, EventoRepo eventoRepo) {
        this.participanteRepo = participanteRepo;
        this.eventoRepo = eventoRepo;
    }

    public Page<ParticipanteResponseDTO> listarParticipantes(Pageable pageable){
        return participanteRepo.findAll(pageable)
                .map(ParticipanteMapper::toDTO);
    }

    public ParticipanteResponseDTO obtenerPorId(Long id){
        Participante participante = participanteRepo.findById(id)
                .orElseThrow(() -> new ParticipanteNotFoundException(id));
        return ParticipanteMapper.toDTO(participante);
    }

    public ParticipanteResponseDTO crearParticipante(ParticipanteRequestDTO dto){
        // Validar que el usuario no exista
        if (participanteRepo.existsByUsuario(dto.getUsuario())) {
            throw new UsuarioYaExisteException(dto.getUsuario());
        }

        // Validar que el evento existe
        Evento evento = eventoRepo.findById(dto.getEventoId())
                .orElseThrow(() -> new EventoNoEncontradoException(dto.getEventoId()));

        // Mapear DTO a entidad
        Participante participante = ParticipanteMapper.toEntity(dto);
        participante.setEvento(evento);

        // Guardar y devolver
        Participante guardado = participanteRepo.save(participante);
        return ParticipanteMapper.toDTO(guardado);
    }
}

