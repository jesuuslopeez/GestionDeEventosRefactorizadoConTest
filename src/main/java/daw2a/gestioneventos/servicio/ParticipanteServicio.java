package daw2a.gestioneventos.servicio;

import daw2a.gestioneventos.dominio.Participante;
import daw2a.gestioneventos.repo.ParticipanteRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParticipanteServicio {
    private final ParticipanteRepo participanteRepo;

    public ParticipanteServicio(ParticipanteRepo participanteRepo) {
        this.participanteRepo = participanteRepo;
    }

    public List<Participante> listarParticipantes(){
        return participanteRepo.findAll();
    }

    public Participante obtenerPorId(Long id){
        return participanteRepo.findById(id).orElse(null);
    }

    public Participante crearParticipante(Participante participante){
        return participanteRepo.save(participante);
    }
}

