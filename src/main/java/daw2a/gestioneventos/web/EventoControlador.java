package daw2a.gestioneventos.web;

import daw2a.gestioneventos.dominio.Evento;
import daw2a.gestioneventos.dto.EventoRequestDTO;
import daw2a.gestioneventos.dto.EventoResponseDTO;
import daw2a.gestioneventos.servicio.EventoServicio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/eventos")
public class EventoControlador {

    private final EventoServicio eventoServicio;

    public EventoControlador(EventoServicio eventoServicio) {
        this.eventoServicio = eventoServicio;
    }

    @GetMapping
    public ResponseEntity<Page<EventoResponseDTO>> getEventos(Pageable pageable) {
        Page<EventoResponseDTO> eventos = eventoServicio.listarEventos(pageable);
        return ResponseEntity.ok(eventos);
    }
    //Para distinguir dos endpoints que sólo se diferencian por el tipo de parámetro
    //Hay varias posibilidades una es usar una expresión regular. En estos dos endpoint
    //tipo GetMapping uno recibe un número (id) el otro el nombre (string).
    //Pero, ¿qué pasaría si el id fuera de tipo UUID?
    //Otra solución es tener una única función, un único path y recibir parámetros, uno por cada
    //tipo que vayamos a recibir, en este caso habría que tener dos @RequestParam
    //@GetMapping
    //public ResponseEntity<Evento> obtener(@RequestParam(required = false) Long id,
    //                                      @RequestParam(required = false) String nombre)
    //Le mejor solucíón sería tener nuestros dos endpoints separados, poniendo en el path
    //por qué elemento buscamos
    //@GetMapping("/id/{id}")
    //public ResponseEntity<Evento> obtenerPorId(@PathVariable Long id) { ... }
    //@GetMapping("/nombre/{nombre}")
    //public ResponseEntity<Evento> obtenerPorNombre(@PathVariable String nombre) { ... }
    @GetMapping("/id/{id}")
    public ResponseEntity<EventoResponseDTO> obtenEventoPorId(@PathVariable Long id) {
        EventoResponseDTO evento = eventoServicio.obtenEventoPorId(id); // lanza EventoNoEncontradoException si no existe
        return ResponseEntity.ok(evento);
    }

    //Para distinguir dos endpopints que sólo se diferencian por el tipo de parámetro
    //Hay varias posibilidades una es usar una expresión regular
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<EventoResponseDTO> obtenEventoPorNombre(@PathVariable String nombre) {
        EventoResponseDTO evento = eventoServicio.obtenEventoPorNombre(nombre);

        return ResponseEntity.ok(evento);
    }
    @PostMapping
    public ResponseEntity<EventoResponseDTO> creaEvento(@RequestBody EventoRequestDTO evento) {
        EventoResponseDTO eventoGuardado = eventoServicio.crearEvento(evento);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoGuardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> editEvento(@PathVariable Long id, @RequestBody EventoRequestDTO evento) {
        EventoResponseDTO eventoActualizado = eventoServicio.actualizarEvento(id, evento);
        return ResponseEntity.ok(eventoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Long id) {
       eventoServicio.eliminarEvento(id);

        return ResponseEntity.noContent().build();
    }
}

