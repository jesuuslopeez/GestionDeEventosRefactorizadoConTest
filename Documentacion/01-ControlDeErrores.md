## ⚙️ **Dónde y cómo introducir el control de errores**

### 🔹 **Momento ideal en la secuencia**

**Después de separar la capa de servicio (fase 2)**
y **antes de introducir DTO/paginación (fase 4–5)**.

Así ya tienes:

* Controlador limpio, que solo delega.
* Servicio donde está la lógica de negocio (validaciones, etc.).
* Repositorio gestionando acceso a datos.

👉 En ese punto puedes introducir **tu propio sistema de errores controlados.**

---

## 🧩 **Pasos concretos para la gestión de errores personalizada**

### **1️⃣ Crear excepciones personalizadas**

Ejemplo:

```java
public class EventoNotFoundException extends RuntimeException {
    public EventoNotFoundException(Long id) {
        super("No se encontró el evento con ID " + id);
    }
}
```

Puedes añadir otras, según el contexto:

```java
public class EventoDuplicadoException extends RuntimeException { ... }
public class FechaInvalidaException extends RuntimeException { ... }
```

---

### **2️⃣ Lanzar esas excepciones en el servicio**

```java
@Service
public class EventoService {
    @Autowired
    private EventoRepository repo;

    public Evento obtenerPorId(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new EventoNotFoundException(id));
    }

    public Evento crear(Evento evento) {
        if (repo.existsByNombre(evento.getNombre())) {
            throw new EventoDuplicadoException(evento.getNombre());
        }
        return repo.save(evento);
    }
}
```

---

### **3️⃣ Centralizar el manejo con `@ControllerAdvice`**

Crea una clase que capture todas tus excepciones y devuelva una respuesta controlada:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EventoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EventoNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            "Recurso no encontrado",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(EventoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleDuplicado(EventoDuplicadoException ex) {
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(),
            "Evento duplicado",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Error interno del servidor",
            "Ocurrió un error inesperado"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

---

### **4️⃣ Crear una clase `ErrorResponse` uniforme**

```java
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;

    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    // getters y setters
}
```

---

### **5️⃣ Resultado esperado (API limpia y segura)**

En lugar de la respuesta por defecto de Spring:

```json
{
  "timestamp": "2025-11-11T11:30:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "trace": "java.lang.NullPointerException...",
  "path": "/eventos/99"
}
```

Tendrás algo así:

```json
{
  "timestamp": "2025-11-11T11:30:00",
  "status": 404,
  "error": "Recurso no encontrado",
  "message": "No se encontró el evento con ID 99"
}
```

✅ Sin información interna del sistema
✅ Uniforme y fácil de documentar
✅ Compatible con Swagger/OpenAPI

---

## 📘 **Reubicado en la secuencia**

| Fase  | Paso                                                                   | Objetivo                        |
| ----- | ---------------------------------------------------------------------- | ------------------------------- |
| 1     | Controlador completo                                                   | Base funcional                  |
| 2     | Crear capa de servicio                                                 | Separación                      |
| **3** | **Control de errores y validaciones (ControllerAdvice + Excepciones)** | **Errores seguros y uniformes** |
| 4     | DTOs                                                                   | Encapsulación de datos          |
| 5     | Paginación / Ordenación                                                | Escalabilidad                   |
| 6     | Filtrado                                                               | Consultas útiles                |
| 7     | HATEOAS                                                                | REST completo                   |
| 8     | Tests                                                                  | Cobertura                       |

---
