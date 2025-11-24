# Fase siguiente: Introducción de DTOs (Data Transfer Objects)

Tras refactorizar los servicios y añadir errores/excepciones, el siguiente paso natural es
introducir **DTOs** (objetos de transferencia) en la API.

La idea de esta fase es:

- Dejar de exponer directamente las **entidades JPA** en los controladores.
- Separar claramente:
  - Lo que se guarda en la base de datos (entidades).
  - Lo que se expone en la API REST (DTOs de entrada y de salida).
- Preparar el terreno para validaciones más finas, evolución de la API y seguridad.

Esta fase también se trabajará **con TDD**, empezando por tests de controlador y servicio
que usen DTOs, y adaptando después el código.

---

## 1. Problema actual

Actualmente los controladores (`EventoControlador`, `ParticipanteControlador`, `OrganizadorControlador`)
exponen directamente las entidades:

- En las peticiones de entrada (`@RequestBody Evento`, `@RequestBody Participante`, ...)
- En las respuestas (`ResponseEntity<Evento>`, `ResponseEntity<List<Evento>>`, ...)

Esto tiene varios inconvenientes:

1. **Acoplamiento fuerte** entre la API y el modelo JPA.
   - Cualquier cambio en la entidad puede romper clientes.
2. Las entidades pueden tener **relaciones perezosas (lazy)** y campos que no queremos exponer
   tal cual (por ejemplo, contraseñas, referencias circulares, campos internos).
3. La validación (anotaciones como `@Size`, `@NotNull`, etc.) está mezclada en las entidades,
   cuando a veces tiene más sentido validarlo a nivel de DTO de entrada.

---

## 2. Objetivos de la fase de DTOs

1. Introducir **DTOs de entrada** para crear/actualizar recursos, p.ej.:
   - `EventoCreateDTO`
   - `EventoUpdateDTO`

2. Introducir **DTOs de salida/listado** para exponer datos minimizados/transformados:
   - `EventoResponseDTO` o `EventoListDTO`.

3. Ajustar **controladores** para que:
   - Reciban DTOs en `@RequestBody`.
   - Devuelvan DTOs en las respuestas.

4. Ajustar **servicios** para que:
   - Trabajen principalmente con entidades internamente.
   - Tengan métodos de conversión a/desde DTO (o bien delegar esa conversión a un mapper).

5. Añadir/ajustar **tests de controlador y servicio** para que:
   - Verifiquen la conversión correcta entre DTOs y entidades.
   - Comprueben que la API no expone campos sensibles.

---

## 3. Diseño de DTOs para `Evento`

Como ejemplo, trabajaremos primero con `Evento`.

### 3.1. DTO de creación: `EventoCreateDTO`

Propósito:

- Representar los datos necesarios para **crear** un evento.
- Evitar que el cliente envíe campos que no deben especificarse (por ejemplo, `id`).

Campos sugeridos:

```java
public class EventoCreateDTO {
    @NotBlank
    private String nombre;

    @NotBlank
    private String descripcion;

    private TipoEvento tipo; // opcional: por defecto CONGRESO

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    private Long organizadorId; // identificador del organizador
    // getters y setters
}
```

### 3.2. DTO de actualización: `EventoUpdateDTO`

Propósito:

- Representar los campos que se pueden **actualizar**.
- Permitir actualizaciones parciales (`PATCH`/`PUT` flexible) dejando algunos campos en `null`.

Sugerencia:

```java
public class EventoUpdateDTO {
    private String nombre;
    private String descripcion;
    private TipoEvento tipo;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Long organizadorId;
    // getters y setters
}
```

### 3.3. DTO de salida: `EventoResponseDTO`

Propósito:

- Representar lo que devolvemos al cliente cuando listamos u obtenemos un evento.
- No exponer relaciones completas (por ejemplo, lista de participantes completa) si no hace falta.

Sugerencia inicial:

```java
public class EventoResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private TipoEvento tipo;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Long organizadorId;
    private String organizadorNombre; // opcional
    // getters y setters
}
```

Más adelante se podrían añadir DTOs específicos para listados (`EventoListItemDTO`) con menos campos
si la API lo requiere.

---

## 4. Conversión entre Entidad y DTO

Hay varias opciones:

1. Métodos estáticos en la entidad o en los DTOs (`fromEntity`, `toEntity`).
2. Una clase mapper dedicada (ej. `EventoMapper`).
3. Librerías como MapStruct (más avanzado; probablemente excesivo para esta unidad).

Para esta práctica, se recomienda la opción 2 (mapper simple) o 1 (métodos estáticos), para que
los alumnos vean claramente la conversión.

### 4.1. Ejemplo de mapper sencillo

```java
public class EventoMapper {

    public static Evento toEntity(EventoCreateDTO dto, Organizador organizador) {
        return Evento.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .tipo(Optional.ofNullable(dto.getTipo()).orElse(TipoEvento.CONGRESO))
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .organizador(organizador)
                .build();
    }

    public static void applyUpdate(EventoUpdateDTO dto, Evento evento, Organizador organizador) {
        if (dto.getNombre() != null) evento.setNombre(dto.getNombre());
        if (dto.getDescripcion() != null) evento.setDescripcion(dto.getDescripcion());
        if (dto.getTipo() != null) evento.setTipo(dto.getTipo());
        if (dto.getFechaInicio() != null) evento.setFechaInicio(dto.getFechaInicio());
        if (dto.getFechaFin() != null) evento.setFechaFin(dto.getFechaFin());
        if (organizador != null) evento.setOrganizador(organizador);
    }

    public static EventoResponseDTO toResponseDTO(Evento evento) {
        EventoResponseDTO dto = new EventoResponseDTO();
        dto.setId(evento.getId());
        dto.setNombre(evento.getNombre());
        dto.setDescripcion(evento.getDescripcion());
        dto.setTipo(evento.getTipo());
        dto.setFechaInicio(evento.getFechaInicio());
        dto.setFechaFin(evento.getFechaFin());
        if (evento.getOrganizador() != null) {
            dto.setOrganizadorId(evento.getOrganizador().getId());
            dto.setOrganizadorNombre(evento.getOrganizador().getNombre());
        }
        return dto;
    }
}
```

---

## 5. Cambios previstos en los controladores

Tomando `EventoControlador` como ejemplo, los cambios serán:

### 5.1. Crear evento

Actualmente:

```java
@PostMapping
public ResponseEntity<Evento> creaEvento(@RequestBody Evento evento) {
    Evento eventoGuardado = eventoServicio.crearEvento(evento);
    if (eventoGuardado == null) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
    return ResponseEntity.status(HttpStatus.CREATED).body(eventoGuardado);
}
```

Con DTOs:

```java
@PostMapping
public ResponseEntity<EventoResponseDTO> creaEvento(@RequestBody EventoCreateDTO dto) {
    EventoResponseDTO creado = eventoServicio.crearEvento(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(creado);
}
```

El servicio se encargará de:

- Buscar el organizador por `organizadorId` (si procede).
- Crear la entidad `Evento` a partir del DTO.
- Guardar y devolver un `EventoResponseDTO`.

### 5.2. Actualizar evento

Actualmente:

```java
@PutMapping("/{id}")
public ResponseEntity<Evento> editEvento(@PathVariable Long id, @RequestBody Evento evento) {
    Evento eventoActualizado = eventoServicio.actualizarEvento(id, evento);
    if (eventoActualizado == null) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(eventoActualizado);
}
```

Con DTOs:

```java
@PutMapping("/{id}")
public ResponseEntity<EventoResponseDTO> editEvento(@PathVariable Long id,
                                                    @RequestBody EventoUpdateDTO dto) {
    EventoResponseDTO actualizado = eventoServicio.actualizarEvento(id, dto);
    return ResponseEntity.ok(actualizado);
}
```

### 5.3. Listar/obtener eventos

- `GET /eventos` devolverá `List<EventoResponseDTO>`.
- `GET /eventos/{id}` devolverá `EventoResponseDTO`.

Esto permite cambiar la estructura externa (por ejemplo, añadir campos calculados) sin
romper el dominio interno.

---

## 6. Cambios previstos en `EventoServicio`

Habrá que añadir sobrecargas o nuevos métodos en `EventoServicio` que acepten DTOs:

- `EventoResponseDTO crearEvento(EventoCreateDTO dto)`
- `EventoResponseDTO actualizarEvento(Long id, EventoUpdateDTO dto)`
- `List<EventoResponseDTO> listarEventosDTO()` (o que el controlador convierta `List<Evento>` a DTO).

Diseño sugerido (no definitivo, para discutir en clase):

```java
public EventoResponseDTO crearEvento(EventoCreateDTO dto) {
    // 1. Validar duplicados (usando nombre)
    // 2. Cargar organizador si viene organizadorId
    // 3. Convertir DTO -> Entidad (mapper)
    // 4. Guardar
    // 5. Convertir Entidad -> DTO de respuesta
}

public EventoResponseDTO actualizarEvento(Long id, EventoUpdateDTO dto) {
    // 1. Buscar evento existente (o lanzar EventoNoEncontradoException)
    // 2. Si hay organizadorId, cargar organizador
    // 3. Aplicar cambios (mapper.applyUpdate)
    // 4. Guardar
    // 5. Convertir a EventoResponseDTO
}
```

Los métodos existentes que trabajan con `Evento` se pueden mantener temporalmente para no romper
código mientras se migra y se escriben tests nuevos.

---

## 7. Tests a añadir/modificar para DTOs

### 7.1. Tests de controlador (`EventoControladorTest`)

- Nuevos tests que envíen/esperen DTOs en lugar de entidades.
- Por ejemplo:

```java
@Test
void createShouldReturnCreatedEventoResponseDTO() throws Exception {
    EventoCreateDTO in = new EventoCreateDTO();
    in.setNombre("Nuevo");
    in.setDescripcion("X");

    EventoResponseDTO out = new EventoResponseDTO();
    out.setId(10L);
    out.setNombre("Nuevo");

    when(eventoServicio.crearEvento(any(EventoCreateDTO.class))).thenReturn(out);

    mockMvc.perform(post("/api/v1/eventos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(in)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.nombre").value("Nuevo"));
}
```

### 7.2. Tests de servicio (`EventoServicioTest`)

- Tests que verifiquen la conversión DTO → Entidad y viceversa.
- Ejemplo:

```java
@Test
void crearEventoDesdeDtoShouldMapAndSaveCorrectly() {
    EventoCreateDTO dto = new EventoCreateDTO();
    dto.setNombre("Nuevo");
    dto.setDescripcion("X");

    Evento guardado = Evento.builder().id(10L).nombre("Nuevo").descripcion("X").build();
    when(eventoRepo.findByNombre("Nuevo")).thenReturn(null);
    when(eventoRepo.save(any(Evento.class))).thenReturn(guardado);

    EventoResponseDTO resultado = eventoServicio.crearEvento(dto);

    assertThat(resultado.getId()).isEqualTo(10L);
    assertThat(resultado.getNombre()).isEqualTo("Nuevo");
}
```

---

## 8. "Con todo": resumen de la fase de DTOs

1. **Definir DTOs** para `Evento`:
   - `EventoCreateDTO` (entrada creación).
   - `EventoUpdateDTO` (entrada actualización).
   - `EventoResponseDTO` (salida/listado).

2. **Elegir estrategia de mapeo** (métodos estáticos o `EventoMapper`).

3. **Modificar `EventoControlador`** para que:
   - Reciba DTOs en `@RequestBody`.
   - Devuelva DTOs en `ResponseEntity`.

4. **Extender `EventoServicio`** con métodos que acepten DTOs y devuelvan DTOs de respuesta.

5. **Actualizar/añadir tests de controlador** para trabajar con DTOs.

6. **Actualizar/añadir tests de servicio** para comprobar las conversiones DTO ↔ Entidad.

7. Repetir el patrón, si se desea, para `Organizador` y `Participante`.

Como en fases anteriores, la idea es hacerlo **paso a paso con TDD**: primero los tests (idealmente
con TODOs y comentarios), luego los DTOs, mappers, servicios y controladores hasta que todo esté
de nuevo en verde.

