package daw2a.gestioneventos.web;

import daw2a.gestioneventos.dto.ParticipanteRequestDTO;
import daw2a.gestioneventos.dto.ParticipanteResponseDTO;
import daw2a.gestioneventos.servicio.ParticipanteServicio;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/participantes")
public class ParticipanteControlador {

    private final ParticipanteServicio participanteServicio;

    public ParticipanteControlador(ParticipanteServicio participanteServicio) {
        this.participanteServicio = participanteServicio;
    }

    @GetMapping
    public ResponseEntity<Page<ParticipanteResponseDTO>> listar(Pageable pageable) {
        Page<ParticipanteResponseDTO> participantes = participanteServicio.listarParticipantes(pageable);
        return ResponseEntity.ok(participantes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParticipanteResponseDTO> obtenerPorId(@PathVariable Long id) {
        ParticipanteResponseDTO participante = participanteServicio.obtenerPorId(id);
        return ResponseEntity.ok(participante);
    }

    @PostMapping
    public ResponseEntity<ParticipanteResponseDTO> crear(@Valid @RequestBody ParticipanteRequestDTO participante) {
        ParticipanteResponseDTO creado = participanteServicio.crearParticipante(participante);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
}
