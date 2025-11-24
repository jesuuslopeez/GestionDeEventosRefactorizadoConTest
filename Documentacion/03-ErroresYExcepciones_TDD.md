# Errores y Excepciones en la API de Gestión de Eventos

Este documento describe el **plan de refactorización** para introducir:

- Excepciones personalizadas en la capa de servicio.
- Un manejador global de errores (`@ControllerAdvice`).
- Tests de servicio y de controlador que verifiquen los casos de error.

La idea es trabajar estos cambios **con TDD** en clase, apoyándonos en los tests y en la
separación controlador/servicio que ya hemos hecho.

---

## 1. Problema actual

Ahora mismo los servicios (`EventoServicio`, `OrganizadorServicio`, `ParticipanteServicio`) devuelven:

- `null` cuando algo no se encuentra (por ejemplo, evento no existente).
- `boolean` (`true`/`false`) para indicar si un borrado ha tenido éxito o no.
- `null` en `crearEvento` cuando hay un conflicto (nombre duplicado).

Y los controladores deciden el **código HTTP** en base a esas devoluciones:

```java
Evento evento = eventoServicio.obtenEventoPorId(id);
return (evento != null)
        ? ResponseEntity.ok(evento)
        : ResponseEntity.notFound().build();
```

```java
Evento eventoGuardado = eventoServicio.crearEvento(evento);
if (eventoGuardado == null) {
    return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409
}
return ResponseEntity.status(HttpStatus.CREATED).body(eventoGuardado);
```

Esto funciona, pero tiene varias limitaciones:

1. Mezclamos **lógica de dominio** (qué significa no encontrar un evento) con lógica HTTP.
2. Es fácil olvidar controlar el `null` y provocar un `NullPointerException` en alguna rama.
3. No tenemos un formato uniforme de respuesta de error (a veces sólo un código, sin cuerpo).

---

## 2. Objetivo de la refactorización

Queremos:

1. Que la **capa de servicio** exprese los errores con **excepciones de dominio** significativas.
2. Que la **capa web** (controladores) se limite a:
   - Delegar en el servicio.
   - Devolver lo que corresponde cuando todo va bien.
3. Que un `@ControllerAdvice` global convierta las excepciones en **respuestas HTTP coherentes**,
   con un cuerpo de error estándar.
4. Tener **tests** que aseguren que:
   - El servicio lanza las excepciones correctas.
   - El `ControllerAdvice` mapea esas excepciones a los códigos HTTP esperados.

---

## 3. Tipos de excepciones que se van a añadir

### 3.1. Excepciones de dominio

Ejemplos (nombres orientativos):

1. `EventoNoEncontradoException`
   - Extiende `RuntimeException`.
   - Usada cuando un evento con un `id` dado no existe.
   - Puede incluir el `id` en el mensaje para logging.

   ```java
   public class EventoNoEncontradoException extends RuntimeException {
       public EventoNoEncontradoException(Long id) {
           super("Evento no encontrado con id=" + id);
       }
   }
   ```

2. `EventoDuplicadoException`
   - Extiende `RuntimeException`.
   - Usada cuando se intenta crear un evento con un `nombre` ya existente.

   ```java
   public class EventoDuplicadoException extends RuntimeException {
       public EventoDuplicadoException(String nombre) {
           super("Ya existe un evento con nombre=" + nombre);
       }
   }
   ```

Más adelante se pueden añadir equivalentes para `Organizador` y `Participante`, pero empezaremos
por `Evento` para no complicar demasiado.

### 3.2. Ventajas de usar excepciones personalizadas

- El código del servicio **expresa claramente la intención**:
  - `throw new EventoNoEncontradoException(id);` es más legible que `return null;`.
- Podemos centralizar en un punto (el `@ControllerAdvice`) la conversión de excepciones a
  códigos HTTP y cuerpos de error.
- Los tests de servicio pueden verificar que se lanza una excepción concreta, sin preocuparse
  aún de los detalles HTTP.

---

## 4. Cambios previstos en `EventoServicio`

Tomando como ejemplo `EventoServicio`, los cambios típicos serán:

### 4.1. `obtenEventoPorId(Long id)`

Actualmente:

```java
public Evento obtenEventoPorId(Long id){
    Evento evento = eventoRepo.findById(id).orElse(null);
    return evento;
}
```

Con excepciones:

```java
public Evento obtenEventoPorId(Long id) {
    return eventoRepo.findById(id)
            .orElseThrow(() -> new EventoNoEncontradoException(id));
}
```

### 4.2. `crearEvento(Evento evento)`

Actualmente devuelve `null` si el nombre ya existe:

```java
public Evento crearEvento(Evento evento){
    Evento eventoExistente = eventoRepo.findByNombre(evento.getNombre());
    if (eventoExistente != null) {
        return null;
    }
    return eventoRepo.save(evento);
}
```

Con excepciones:

```java
public Evento crearEvento(Evento evento) {
    Evento eventoExistente = eventoRepo.findByNombre(evento.getNombre());
    if (eventoExistente != null) {
        throw new EventoDuplicadoException(evento.getNombre());
    }
    return eventoRepo.save(evento);
}
```

### 4.3. `actualizarEvento(Long id, Evento evento)` y `eliminarEvento(Long id)`

Estos métodos ya comprueban si existe el evento. Se puede sustituir el `null`/`false` por
`EventoNoEncontradoException` para un comportamiento más consistente.

---

## 5. `@ControllerAdvice` global

Vamos a crear una clase (por ejemplo `ApiExceptionHandler`) anotada con `@RestControllerAdvice` o
`@ControllerAdvice` que:

- Escuche las excepciones de dominio (`EventoNoEncontradoException`, `EventoDuplicadoException`, etc.).
- Devuelva:
  - 404 Not Found cuando el recurso no existe.
  - 409 Conflict cuando hay conflicto de datos (duplicado).
- Emita un cuerpo de error con información básica:

  ```json
  {
    "timestamp": "2025-11-16T19:30:00",
    "status": 404,
    "error": "Not Found",
    "message": "Evento no encontrado con id=999",
    "path": "/api/v1/eventos/999"
  }
  ```

### 5.1. DTO de error

Se puede crear un DTO sencillo, por ejemplo `ApiError`:

```java
public class ApiError {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    // getters, setters, builder
}
```

### 5.2. Handler de excepciones

Ejemplo de manejador para `EventoNoEncontradoException`:

```java
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EventoNoEncontradoException.class)
    public ResponseEntity<ApiError> handleEventoNoEncontrado(
            EventoNoEncontradoException ex,
            HttpServletRequest request) {

        ApiError error = new ApiError();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
        error.setMessage(ex.getMessage());
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(EventoDuplicadoException.class)
    public ResponseEntity<ApiError> handleEventoDuplicado(
            EventoDuplicadoException ex,
            HttpServletRequest request) {

        ApiError error = new ApiError();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setError(HttpStatus.CONFLICT.getReasonPhrase());
        error.setMessage(ex.getMessage());
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
```

Con esto, los controladores pueden simplificarse, ya que no tienen que comprobar `null`/`boolean`
para estos casos: si el servicio lanza una excepción, el handler se encarga.

---

## 6. Impacto en los servicios y controladores

### 6.1. Servicios

- Dejan de devolver `null`/`false` en casos de error y pasan a lanzar excepciones específicas.
- El código refleja mejor la lógica de dominio y es más fácil de testear con asserts sobre excepciones.

### 6.2. Controladores

- Para los casos gestionados por excepciones, pueden eliminar mucha lógica de `if (x == null) ...`.
- Siguen decidiendo el código HTTP para casos "normales" (por ejemplo, `201 CREATED` tras crear un recurso).

Ejemplo simplificado de controlador tras la refactorización:

```java
@GetMapping("/{id}")
public ResponseEntity<Evento> obtenEventoPorId(@PathVariable Long id) {
    Evento evento = eventoServicio.obtenEventoPorId(id); // lanza EventoNoEncontradoException si no existe
    return ResponseEntity.ok(evento);
}
```

El `404` será responsabilidad del `@ControllerAdvice`.

---

## 7. Tests a añadir/modificar

### 7.1. Tests de servicio (`EventoServicioTest`)

Además de los tests ya existentes, se pueden añadir (o adaptar):

1. **Evento no encontrado**:

   ```java
   @Test
   void obtenEventoPorIdShouldThrowWhenNotFound() {
       when(eventoRepo.findById(99L)).thenReturn(Optional.empty());

       assertThatThrownBy(() -> eventoServicio.obtenEventoPorId(99L))
           .isInstanceOf(EventoNoEncontradoException.class)
           .hasMessageContaining("99");
   }
   ```

2. **Evento duplicado**:

   ```java
   @Test
   void crearEventoShouldThrowWhenNombreDuplicado() {
       Evento existente = Evento.builder().id(1L).nombre("Duplicado").build();
       Evento nuevo = Evento.builder().nombre("Duplicado").build();
       when(eventoRepo.findByNombre("Duplicado")).thenReturn(existente);

       assertThatThrownBy(() -> eventoServicio.crearEvento(nuevo))
           .isInstanceOf(EventoDuplicadoException.class)
           .hasMessageContaining("Duplicado");
   }
   ```

### 7.2. Tests de controlador (`EventoControladorTest`)

Con `MockMvc` se pueden añadir tests que verifiquen
el comportamiento del `@ControllerAdvice` cuando el servicio lanza excepciones:

1. **GET /eventos/{id} cuando no existe**:

   ```java
   @Test
   void getByIdShouldReturn404WhenEventoNotFound() throws Exception {
       when(eventoServicio.obtenEventoPorId(99L))
           .thenThrow(new EventoNoEncontradoException(99L));

       mockMvc.perform(get("/api/v1/eventos/99"))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.status").value(404));
   }
   ```

2. **POST /eventos con nombre duplicado**:

   ```java
   @Test
   void createShouldReturn409WhenNombreDuplicado() throws Exception {
       Evento in = Evento.builder().nombre("Duplicado").descripcion("X").build();

       when(eventoServicio.crearEvento(any(Evento.class)))
           .thenThrow(new EventoDuplicadoException("Duplicado"));

       mockMvc.perform(post("/api/v1/eventos")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(in)))
           .andExpect(status().isConflict())
           .andExpect(jsonPath("$.status").value(409));
   }
   ```

Estos tests obligarán a que el `@ControllerAdvice` esté correctamente configurado.

---

## 8. "Con todo": resumen de la película completa

1. **Añadir excepciones personalizadas** de dominio (`EventoNoEncontradoException`, `EventoDuplicadoException`).
2. **Refactorizar `EventoServicio`** para lanzar estas excepciones en lugar de devolver `null`/`false` en casos de error.
3. **Crear un `ApiError` DTO** para respuestas de error consistentes.
4. **Crear un `@RestControllerAdvice` (`ApiExceptionHandler`)** que mapee estas excepciones a
   respuestas HTTP apropiadas (404, 409, etc.).
5. **Actualizar/añadir tests de servicio** (`EventoServicioTest`) para comprobar que se lanzan
   las excepciones correctas.
6. **Añadir tests de controlador** (`EventoControladorTest`) que simulen esas excepciones
   y verifiquen el comportamiento del `ControllerAdvice`.
7. **Limpiar los controladores**, eliminando lógicas de `if (evento == null)` que ahora
   se resuelven con excepciones y handlers.

Todo este trabajo se puede hacer **paso a paso con TDD**, primero escribiendo/adaptando los tests
(idealmente con TODOs en los métodos de test) y luego implementando la lógica en el servicio y el
`@ControllerAdvice` hasta que todo vuelva a estar en verde.

