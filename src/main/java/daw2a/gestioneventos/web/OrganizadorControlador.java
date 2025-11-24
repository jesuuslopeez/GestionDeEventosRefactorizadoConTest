package daw2a.gestioneventos.web;

import daw2a.gestioneventos.dto.OrganizadorRequestDTO;
import daw2a.gestioneventos.dto.OrganizadorResponseDTO;
import daw2a.gestioneventos.servicio.OrganizadorServicio;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/organizadores")
@RestController
public class OrganizadorControlador {

    private final OrganizadorServicio organizadorServicio;

    public OrganizadorControlador(OrganizadorServicio organizadorServicio) {
        this.organizadorServicio = organizadorServicio;
    }

    @GetMapping
    public ResponseEntity<Page<OrganizadorResponseDTO>> listar(Pageable pageable) {
        Page<OrganizadorResponseDTO> organizadores = organizadorServicio.listarOrganizadores(pageable);
        return ResponseEntity.ok(organizadores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizadorResponseDTO> obtenerPorId(@PathVariable Long id) {
        OrganizadorResponseDTO organizador = organizadorServicio.obtenerPorId(id);
        return ResponseEntity.ok(organizador);
    }

    @PostMapping
    public ResponseEntity<OrganizadorResponseDTO> crear(@Valid @RequestBody OrganizadorRequestDTO organizador) {
        OrganizadorResponseDTO creado = organizadorServicio.crearOrganizador(organizador);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
}
