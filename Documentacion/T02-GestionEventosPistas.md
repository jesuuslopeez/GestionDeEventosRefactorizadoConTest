## Gestion de eventos: Pistas


### Estructura de las entidades y ejemplos de código:

#### 1. **Entidad Evento**

```java
@Entity
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descripcion;
    private LocalDate fecha;

    @ManyToOne
    private Organizador organizador;

    @OneToMany(mappedBy = "evento")
    private List<Participante> participantes;

    // Getters y Setters
}
```

#### 2. **Entidad Participante**

```java
@Entity
public class Participante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String email;

    @ManyToOne
    private Evento evento;

    // Getters y Setters
}
```

#### 3. **Entidad Organizador**

```java
@Entity
public class Organizador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String email;

    @OneToMany(mappedBy = "organizador")
    private List<Evento> eventos;

    // Getters y Setters
}
```

---

### Repositorios

#### **Repositorio Evento**

```java
public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findByTitulo(String titulo);
}
```

#### **Repositorio Participante**

```java
public interface ParticipanteRepository extends JpaRepository<Participante, Long> {
    List<Participante> findByEmail(String email);
}
```

#### **Repositorio Organizador**

```java
public interface OrganizadorRepository extends JpaRepository<Organizador, Long> {
    List<Organizador> findByNombre(String nombre);
}
```

---

### Controladores

#### **Controlador Evento**

```java
@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    @Autowired
    private EventoRepository eventoRepository;

    @GetMapping
    public List<Evento> getAllEventos() {
        return eventoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> getEventoById(@PathVariable Long id) {
        return eventoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Evento createEvento(@RequestBody Evento evento) {
        return eventoRepository.save(evento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> updateEvento(@PathVariable Long id, @RequestBody Evento eventoDetails) {
        return eventoRepository.findById(id).map(evento -> {
            evento.setTitulo(eventoDetails.getTitulo());
            evento.setDescripcion(eventoDetails.getDescripcion());
            evento.setFecha(eventoDetails.getFecha());
            return ResponseEntity.ok(eventoRepository.save(evento));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvento(@PathVariable Long id) {
        if (eventoRepository.existsById(id)) {
            eventoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
