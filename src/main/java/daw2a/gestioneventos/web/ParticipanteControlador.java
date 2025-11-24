package daw2a.gestioneventos.web;

import daw2a.gestioneventos.dominio.Participante;
import daw2a.gestioneventos.servicio.ParticipanteServicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/participantes")
public class ParticipanteControlador {

    private final ParticipanteServicio participanteServicio;

    public ParticipanteControlador(ParticipanteServicio participanteServicio) {
        this.participanteServicio = participanteServicio;
    }

    @GetMapping
    public ResponseEntity<List<Participante>> listar() {
        // TODO: implementar lógica real de listado de participantes
        List<Participante> participantes = participanteServicio.listarParticipantes();
        return ResponseEntity.ok(participantes);
    }

    @PostMapping
    public ResponseEntity<Participante> crear(@RequestBody Participante participante) {
        // TODO: implementar lógica real de creación (validaciones, etc.)
        Participante creado = participanteServicio.crearParticipante(participante);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
}
