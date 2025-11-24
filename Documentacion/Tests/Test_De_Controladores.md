# Tests de Controladores en Spring Boot con `@WebMvcTest` y `MockMvc`

## Índice
1. Introducción
2. ¿Qué es `@WebMvcTest`?
3. Inyección de dependencias con `@MockBean`
4. `MockMvc`: pruebas de la capa web sin servidor
5. Envío y validación de JSON
6. Validación de respuestas con `jsonPath`
7. Ejemplos completos (GET, POST, PUT, DELETE)
8. Tabla resumen
9. Ejercicio propuesto

---

## 1. Introducción
Los **tests de controladores** verifican que la capa web (endpoints HTTP) funcione correctamente **sin levantar un servidor real**. Para ello, Spring Boot ofrece `@WebMvcTest` junto con `MockMvc`.

Estos tests se diferencian de los tests unitarios porque:
- No usan `@Mock` ni `@InjectMocks`.
- No prueban la lógica del servicio: **solo la capa web**.
- Utilizan `@MockBean` para sustituir los servicios reales por mocks.

---

## 2. ¿Qué es `@WebMvcTest`?
`@WebMvcTest` carga únicamente:
- Controladores (`@RestController`)
- Validación (`@Valid`)
- Jackson (JSON)
- Configuración MVC

❌ **No carga:**
- Servicios reales (`@Service`)
- Repositorios reales (`@Repository`)
- Seguridad, salvo que se configure

Ejemplo:
```java
@WebMvcTest(EventoControlador.class)
class EventoControladorTest { }
```

---

## 3. Inyección de dependencias con `@MockBean`
A diferencia de `@Mock`, `@MockBean` registra el mock como **bean dentro del ApplicationContext de Spring**.

```java
@MockBean
private EventoServicio eventoServicio;
```

Esto significa:
- El controlador usa este mock en lugar del servicio real.
- Si falta un `@MockBean` para una dependencia del controlador, el test fallará al cargar el contexto.

---

## 4. `MockMvc`: Pruebas HTTP sin servidor
`MockMvc` permite simular peticiones HTTP como si tuvieras un servidor real.

Ejemplo básico:
```java
mockMvc.perform(get("/api/v1/eventos"))
       .andExpect(status().isOk());
```

Se puede probar:
- GET
- POST
- PUT
- DELETE
- Headers
- JSON de entrada y salida

`MockMvc` se inyecta con:
```java
@Autowired
private MockMvc mockMvc;
```

---

## 5. Envío de JSON con `ObjectMapper`
Para enviar objetos en peticiones POST/PUT, se convierten a JSON mediante:

```java
@Autowired
private ObjectMapper objectMapper;
```

Uso:
```java
mockMvc.perform(post("/api/v1/eventos")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(objeto)))
        .andExpect(status().isCreated());
```

---

## 6. Validación de respuestas con `jsonPath`
`jsonPath` permite inspeccionar propiedades del JSON devuelto.

### Propiedad simple
```java
jsonPath("$.id").value(10)
```

### Elemento del array
```java
jsonPath("$[0].nombre").value("Prueba")
```

### Verificar que existe
```java
jsonPath("$.error").exists()
```

### Verificar tamaño
```java
jsonPath("$", hasSize(3))
```

---

## 7. Ejemplos completos

### ✔️ GET: listar eventos
```java
@Test
void listShouldReturnEvents() throws Exception {
    Evento e = Evento.builder().id(1L).nombre("Prueba").descripcion("Desc").build();
    when(eventoServicio.listarEventos()).thenReturn(List.of(e));

    mockMvc.perform(get("/api/v1/eventos")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1));
}
```

---

### ✔️ POST: crear evento
```java
@Test
void createShouldReturnCreated() throws Exception {
    Evento in = Evento.builder().nombre("Nuevo").descripcion("X").build();
    Evento saved = Evento.builder().id(10L).nombre("Nuevo").descripcion("X").build();

    when(eventoServicio.crearEvento(any(Evento.class))).thenReturn(saved);

    mockMvc.perform(post("/api/v1/eventos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(in)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(10));
}
```

---

### ✔️ PUT: actualizar evento
```java
@Test
void updateShouldReturnUpdatedEvento() throws Exception {
    Evento cambios = Evento.builder().nombre("NuevoNombre").build();
    Evento actualizado = Evento.builder().id(1L).nombre("NuevoNombre").build();

    when(eventoServicio.actualizarEvento(eq(1L), any(Evento.class))).thenReturn(actualizado);

    mockMvc.perform(put("/api/v1/eventos/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cambios)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("NuevoNombre"));
}
```

---

### ✔️ DELETE: eliminar evento
```java
@Test
void deleteShouldReturnNoContent() throws Exception {
    mockMvc.perform(delete("/api/v1/eventos/1"))
            .andExpect(status().isNoContent());
}
```

---

## 8. Tabla Resumen

| Elemento       | Descripción                     |
| -------------- | ------------------------------- |
| `@WebMvcTest`  | Carga solo la capa web          |
| `@MockBean`    | Registra mocks dentro de Spring |
| `MockMvc`      | Simula peticiones HTTP          |
| `jsonPath`     | Valida partes del JSON          |
| `ObjectMapper` | Convierte objetos ↔ JSON        |
| `perform()`    | Ejecuta una petición HTTP       |
| `andExpect()`  | Comprueba condiciones           |

---

## 9. Ejercicio propuesto
Crea una batería de tests para el endpoint:
```
GET /api/v1/eventos/{id}
```

### Debe probar:
- Caso exitoso → devuelve 200 con el JSON del evento
- Caso inexistente → el servicio lanza excepción, el controlador devuelve 404
- Validar el contenido completo del JSON

---

**¡Documento listo para trabajar exclusivamente con tests de controladores!** 🚀
