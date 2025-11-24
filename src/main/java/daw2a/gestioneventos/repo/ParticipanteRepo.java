package daw2a.gestioneventos.repo;

import daw2a.gestioneventos.dominio.Participante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipanteRepo extends JpaRepository<Participante,Long> {
    List<Participante> findByNombreContainingIgnoreCase(String nombre);
}
