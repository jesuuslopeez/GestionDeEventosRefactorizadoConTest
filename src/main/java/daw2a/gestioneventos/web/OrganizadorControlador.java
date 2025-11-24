package daw2a.gestioneventos.web;

import daw2a.gestioneventos.dominio.Organizador;
import daw2a.gestioneventos.servicio.OrganizadorServicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/organizadores")
@RestController
public class OrganizadorControlador {

    private final OrganizadorServicio organizadorServicio;

    public OrganizadorControlador(OrganizadorServicio organizadorServicio) {
        this.organizadorServicio = organizadorServicio;
    }

    @GetMapping
    public ResponseEntity<List<Organizador>> listar() {
        // TODO: implementar lógica real de listado de organizadores
        List<Organizador> organizadores = organizadorServicio.listarOrganizadores();
        return ResponseEntity.ok(organizadores);
    }

    @PostMapping
    public ResponseEntity<Organizador> crear(@RequestBody Organizador organizador) {
        // TODO: implementar lógica real de creación (validaciones, etc.)
        Organizador creado = organizadorServicio.crearOrganizador(organizador);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
}
