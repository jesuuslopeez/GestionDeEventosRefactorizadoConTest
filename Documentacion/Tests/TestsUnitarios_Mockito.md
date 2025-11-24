# Guía de Tests Unitarios con Mockito y AssertJ
## Índice
1. [Introducción](#introducción)
2. [Conceptos Básicos](#conceptos-básicos)
3. [Configuración de Tests](#configuración-de-tests)
4. [Mocks y Stubs](#mocks-y-stubs)
5. [Stubbing Avanzado](#stubbing-avanzado)
6. [Assertions (AssertJ)](#assertions-assertj)
7. [Aserciones Avanzadas](#aserciones-avanzadas)
8. [Verificaciones (Mockito)](#verificaciones-mockito)
9. [Verificaciones Avanzadas](#verificaciones-avanzadas)
10. [ArgumentCaptor](#argumentcaptor)
11. [BDD Mockito](#bdd-mockito)
12. [Métodos VOID en Mockito](#métodos-void-en-mockito)
13. [Análisis de Tests Completos](#análisis-de-tests-completos)
14. [Ejercicio Práctico](#ejercicio-práctico)

---

## Introducción

Los **tests unitarios** son pruebas automatizadas que verifican el comportamiento de unidades pequeñas de código (métodos, clases) de forma aislada.
En este caso estamo probando la capa de servicios.

**¿Por qué usar mocks?**
- Aíslan la unidad bajo prueba de sus dependencias (repositorios, servicios externos, etc.)
- Permiten controlar el comportamiento de las dependencias
- Hacen los tests rápidos (no acceden a base de datos real)
- Permiten probar casos de error sin necesidad de provocarlos realmente

**Herramientas principales:**
- **JUnit 5**: Framework de testing para Java
- **Mockito**: Framework para crear mocks (objetos simulados)
- **AssertJ**: Biblioteca para hacer aserciones (verificaciones) más legibles

---

## Conceptos Básicos

### ¿Qué es un Mock?

Un **mock** es un objeto simulado que imita el comportamiento de un objeto real. Se usa para:
- Sustituir dependencias (repositorios, servicios, etc.)
- Controlar qué devuelven los métodos
- Verificar que se llamaron correctamente

### ¿Qué es un Stub?

Un **stub** es cuando configuramos un mock para que devuelva valores específicos cuando se llaman sus métodos.

### ¿Qué es una Aserción?

Una **aserción** es una verificación que comprueba si un resultado es el esperado. Si falla, el test falla.

### Estructura básica de un test

```java
@Test
void nombreDescriptivoDelTest() {
    // 1. ARRANGE (Preparar): Configurar datos y mocks
    // 2. ACT (Actuar): Ejecutar el método a probar
    // 3. ASSERT (Afirmar): Verificar el resultado
}
```
---

## Configuración de Tests
### Forma recomendada (JUnit 5)

```java
@ExtendWith(MockitoExtension.class)
class EventoServicioTest {

    @Mock
    EventoRepo eventoRepo;

    @InjectMocks
    EventoServicio eventoServicio;
}
```
✔️ **No necesitas `openMocks()`**
✔️ Más limpio y moderno

### Forma clásica (válida)

```java
@BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);
}
```



### Anotaciones principales

```java
class EventoServicioTest {

    @Mock  // Crea un mock (objeto simulado) de EventoRepo
    private EventoRepo eventoRepo;

    @Mock  // Crea un mock de OrganizadorRepo
    private OrganizadorRepo organizadorRepo;

    @InjectMocks  // Crea una instancia de EventoServicio inyectando los mocks anteriores
    private EventoServicio eventoServicio;

    @BeforeEach  // Se ejecuta ANTES de cada test
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Inicializa los mocks
    }
}
```

**Explicación:**
- `@Mock`: Crea objetos simulados (no reales)
- `@InjectMocks`: Crea el objeto a probar e inyecta los mocks en él
- `@BeforeEach`: Método que se ejecuta antes de cada test (útil para inicializar)
- `MockitoAnnotations.openMocks(this)`: Activa los mocks anotados

---

## Mocks y Stubs

### `when().thenReturn()` - Configurar respuestas

**Sintaxis:**
```java
when(mock.metodo(parametros)).thenReturn(valorADevolver);
```

**Ejemplo 1: Devolver un objeto**
```java
Evento evento = Evento.builder().id(1L).nombre("Test").build();
when(eventoRepo.findById(1L)).thenReturn(Optional.of(evento));
```
➡️ **Significado**: "Cuando se llame a `eventoRepo.findById(1L)`, devuelve `Optional.of(evento)`"

**Ejemplo 2: Devolver una lista**
```java
when(eventoRepo.findAll()).thenReturn(List.of(evento1, evento2));
```
➡️ **Significado**: "Cuando se llame a `findAll()`, devuelve una lista con 2 eventos"

**Ejemplo 3: Devolver null**
```java
when(eventoRepo.findByNombre("Inexistente")).thenReturn(null);
```
➡️ **Significado**: "Cuando se busque ese nombre, no se encuentra nada (null)"

**Ejemplo 4: Devolver Optional vacío**
```java
when(eventoRepo.findById(99L)).thenReturn(Optional.empty());
```
➡️ **Significado**: "Cuando se busque ese ID, no existe"

### `when().thenAnswer()` - Respuestas dinámicas

**Uso:** Cuando necesitas devolver algo basado en los argumentos recibidos.

```java
when(eventoRepo.save(any(Evento.class)))
    .thenAnswer(invocation -> invocation.getArgument(0));
```
➡️ **Significado**: "Cuando se llame a `save()`, devuelve el mismo objeto que recibió como argumento"

**Desglose:**
- `invocation`: Información sobre la llamada al método
- `invocation.getArgument(0)`: Obtiene el primer argumento (índice 0)

### `any()` - Matchers de argumentos

```java
when(eventoRepo.save(any(Evento.class))).thenReturn(evento);
```
➡️ **Significado**: "Cuando se llame a `save()` con **cualquier** objeto de tipo Evento"

**Otros matchers útiles:**
- `anyLong()`: Cualquier Long
- `anyString()`: Cualquier String
- `eq(valor)`: Un valor específico
- `isNull()`: Cuando el argumento es null

---
## Stubbing Avanzado

### ➤ **thenThrow()**

```java
when(repo.findById(1L)).thenThrow(new RuntimeException("Error"));
```

### ➤ **thenCallRealMethod()**

```java
when(servicioReal.metodo()).thenCallRealMethod();
```

### ➤ **Stubbing consecutivo**

```java
when(repo.findAll())
    .thenReturn(List.of(a))
    .thenReturn(List.of(b))
    .thenReturn(List.of(c));
```

### ➤ **doReturn()** (evita problemas con mocks parciales)

```java
doReturn(evento).when(repo).save(any());
```

### ➤ **Métodos VOID → doNothing(), doThrow()**

```java
doNothing().when(repo).delete(any());
doThrow(new IllegalStateException()).when(repo).deleteById(5L);
```

### ➤ **doAnswer() para métodos void**

```java
doAnswer(inv -> {
    System.out.println("Borrando " + inv.getArgument(0));
    return null;
}).when(repo).delete(any());
```
## Assertions (AssertJ)

AssertJ proporciona una sintaxis fluida y legible para hacer verificaciones.

### `assertThat()` - Verificaciones básicas

**Sintaxis:**
```java
assertThat(valor).condicion();
```

### Verificaciones comunes

**1. Verificar igualdad**
```java
assertThat(evento.getNombre()).isEqualTo("Test");
```
➡️ "Verifica que el nombre sea 'Test'"

**2. Verificar que NO es null**
```java
assertThat(evento).isNotNull();
```
➡️ "Verifica que el evento no sea nulo"

**3. Verificar que SÍ es null**
```java
assertThat(evento).isNull();
```
➡️ "Verifica que el evento sea nulo"

**4. Verificar tamaño de lista**
```java
assertThat(listaEventos).hasSize(3);
```
➡️ "Verifica que la lista tenga 3 elementos"

**5. Verificar que lista está vacía**
```java
assertThat(listaEventos).isEmpty();
```
➡️ "Verifica que la lista esté vacía"

**6. Verificar que lista NO está vacía**
```java
assertThat(listaEventos).isNotEmpty();
```

**7. Verificar que lista contiene elementos**
```java
assertThat(listaEventos).contains(evento1, evento2);
```
➡️ "Verifica que la lista contenga esos eventos"

**8. Extraer y verificar propiedades**
```java
assertThat(listaEventos)
    .extracting("nombre")
    .containsExactlyInAnyOrder("Evento1", "Evento2");
```
➡️ "Extrae el campo 'nombre' de cada evento y verifica que contenga esos valores (en cualquier orden)"

### `assertThatExceptionOfType()` - Verificar excepciones

**Sintaxis:**
```java
assertThatExceptionOfType(TipoExcepcion.class)
    .isThrownBy(() -> codigo_que_debe_lanzar_excepcion);
```

**Ejemplo completo:**
```java
assertThatExceptionOfType(EventoNoEncontradoException.class)
    .isThrownBy(() -> eventoServicio.obtenEventoPorId(99L));
```
➡️ **Significado**: "Verifica que al llamar a `obtenEventoPorId(99L)` se lance una excepción de tipo `EventoNoEncontradoException`"

**Verificar también el mensaje:**
```java
assertThatExceptionOfType(EventoNoEncontradoException.class)
    .isThrownBy(() -> eventoServicio.obtenEventoPorId(99L))
    .withMessage("Evento no encontrado con id=99");
```

## Aserciones Avanzadas

### Sobre números

```java
assertThat(total).isGreaterThan(0);
assertThat(total).isBetween(5, 10);
```

### Sobre cadenas de texto

```java
assertThat(nombre)
    .startsWith("Ev")
    .contains("ven")
    .endsWith("to");
```

### Sobre Optional

```java
assertThat(optional).isPresent();
assertThat(optional).contains(evento);
```

### Sobre Mapas

```java
assertThat(mapa).containsKey("id");
assertThat(mapa).containsEntry("nombre", "Evento X");
```

### Extraer propiedades

```java
assertThat(lista)
    .extracting("id")
    .containsExactly(1L, 2L, 3L);
```
---

## Verificaciones (Mockito)
Las verificaciones comprueban que los mocks fueron usados correctamente.

### `verify()` - Verificar llamadas a métodos

**Sintaxis:**
```java
verify(mock).metodo(parametros);
```

**Ejemplo 1: Verificar que se llamó**
```java
verify(eventoRepo).findById(1L);
```
➡️ "Verifica que se llamó a `findById(1L)` exactamente 1 vez"

**Ejemplo 2: Verificar múltiples llamadas**
```java
verify(eventoRepo, times(2)).findAll();
```
➡️ "Verifica que se llamó a `findAll()` exactamente 2 veces"

**Ejemplo 3: Verificar que NO se llamó**
```java
verify(eventoRepo, never()).save(any(Evento.class));
```
➡️ "Verifica que NUNCA se llamó a `save()`"

**Ejemplo 4: Verificar al menos una vez**
```java
verify(eventoRepo, atLeastOnce()).findAll();
```

**Ejemplo 5: Verificar que se llamó con un argumento específico**
```java
verify(eventoRepo).delete(eventoEspecifico);
```
➡️ "Verifica que se llamó a `delete()` con ese objeto específico"

### `verifyNoInteractions()` - Verificar que no hubo llamadas

```java
verifyNoInteractions(organizadorRepo);
```
➡️ "Verifica que NO se llamó a NINGÚN método de `organizadorRepo`"

**Uso típico:** Cuando un test no debería usar cierta dependencia.

### `verifyNoMoreInteractions()` - Verificar que no hay más llamadas

```java
verify(eventoRepo).findById(1L);
verifyNoMoreInteractions(eventoRepo);
```
➡️ "Verifica que solo se llamó a `findById()` y a ningún otro método"

---
# Verificaciones Avanzadas

### Al menos / como mucho

```java
verify(repo, atLeast(1)).findAll();
verify(repo, atMost(3)).save(any());
```

### Solo ese método

```java
verify(repo, only()).findAll();
```

### Verificación con timeout (hilos)

```java
verify(repo, timeout(200)).findAll();
```

---

## ArgumentCaptor

Muy útil para comprobar qué datos se guardan realmente.

```java
ArgumentCaptor<Evento> captor = ArgumentCaptor.forClass(Evento.class);
verify(eventoRepo).save(captor.capture());

Evento guardado = captor.getValue();
assertThat(guardado.getNombre()).isEqualTo("Nuevo evento");
```

---

## BDD Mockito

Alternativa más legible:

```java
given(repo.findById(1L)).willReturn(Optional.of(evento));

Evento e = servicio.obtenEvento(1L);

then(repo).should().findById(1L);
```

---

## Métodos VOID en Mockito

### doNothing()

```java
doNothing().when(repo).deleteById(1L);
```

### doThrow()

```java
doThrow(new IllegalStateException())
    .when(repo).deleteById(5L);
```

### Verificar

```java
verify(repo).deleteById(1L);
```
---
## Análisis de Tests Completos

### Test 1: Listar eventos exitosamente

```java
@Test
void listarEventosShouldDelegateToRepo() {
    // ARRANGE: Preparar datos
    Evento e = Evento.builder().id(1L).nombre("Test").descripcion("Desc").build();
    when(eventoRepo.findAll()).thenReturn(List.of(e));

    // ACT: Ejecutar el método
    List<Evento> result = eventoServicio.listarEventos();

    // ASSERT: Verificar resultados
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(1L);
    verify(eventoRepo).findAll();
    verifyNoInteractions(organizadorRepo);
}
```

**Paso a paso:**
1. **Línea 4**: Creamos un evento de prueba usando el patrón Builder
2. **Línea 5**: Configuramos el mock para que `findAll()` devuelva una lista con ese evento
3. **Línea 8**: Ejecutamos el método que queremos probar
4. **Línea 11**: Verificamos que la lista tiene 1 elemento
5. **Línea 12**: Verificamos que el ID del primer elemento es 1L
6. **Línea 13**: Verificamos que se llamó a `findAll()` del repositorio
7. **Línea 14**: Verificamos que NO se usó el repositorio de organizadores (no era necesario)

### Test 2: Listar eventos cuando no hay ninguno

```java
@Test
void listarEventosShouldThrowWhenEmpty() {
    // ARRANGE: Configurar repo vacío
    when(eventoRepo.findAll()).thenReturn(List.of());

    // ACT + ASSERT: Verificar que lanza excepción
    assertThatExceptionOfType(EventoNoEncontradoException.class)
            .isThrownBy(() -> eventoServicio.listarEventos());

    // ASSERT: Verificar interacciones
    verify(eventoRepo).findAll();
    verifyNoInteractions(organizadorRepo);
}
```

**Paso a paso:**
1. **Línea 4**: El mock devuelve una lista vacía
2. **Línea 7-8**: Verificamos que el servicio lanza `EventoNoEncontradoException` cuando no hay eventos
3. **Línea 11**: Verificamos que sí se intentó buscar en el repositorio
4. **Línea 12**: Verificamos que no se usó el repositorio de organizadores

### Test 3: Obtener evento por ID existente

```java
@Test
void obtenEventoPorIdShouldReturnEventoWhenExists() {
    // ARRANGE
    Evento e = Evento.builder().id(1L).nombre("Test").descripcion("Desc").build();
    when(eventoRepo.findById(1L)).thenReturn(Optional.of(e));

    // ACT
    Evento found = eventoServicio.obtenEventoPorId(1L);

    // ASSERT
    assertThat(found).isNotNull();
    assertThat(found.getId()).isEqualTo(1L);
    verify(eventoRepo).findById(1L);
    verifyNoInteractions(organizadorRepo);
}
```

**Paso a paso:**
1. **Línea 4**: Creamos un evento de prueba
2. **Línea 5**: Configuramos el mock para devolver un Optional con el evento cuando se busque por ID 1L
3. **Línea 8**: Llamamos al método del servicio
4. **Línea 11**: Verificamos que el resultado NO es null
5. **Línea 12**: Verificamos que el ID es correcto
6. **Línea 13**: Verificamos que se llamó a `findById(1L)`

### Test 4: Obtener evento por ID inexistente

```java
@Test
void obtenEventoPorIdShouldThrowWhenNotExists() {
    // ARRANGE
    when(eventoRepo.findById(99L)).thenReturn(Optional.empty());

    // ACT + ASSERT
    assertThatExceptionOfType(EventoNoEncontradoException.class)
            .isThrownBy(() -> eventoServicio.obtenEventoPorId(99L));

    // ASSERT
    verify(eventoRepo).findById(99L);
    verifyNoInteractions(organizadorRepo);
}
```

**Paso a paso:**
1. **Línea 4**: El mock devuelve un Optional vacío (no se encontró)
2. **Línea 7-8**: Verificamos que el servicio lanza una excepción
3. **Línea 11**: Verificamos que sí se intentó buscar

### Test 5: Crear evento con nombre duplicado

```java
@Test
void crearEventoShouldThrowWhenNombreYaExiste() {
    // ARRANGE
    Evento existente = Evento.builder().id(1L).nombre("Duplicado").descripcion("Desc").build();
    Evento nuevo = Evento.builder().nombre("Duplicado").descripcion("X").build();
    when(eventoRepo.findByNombre("Duplicado")).thenReturn(existente);

    // ACT + ASSERT
    assertThatExceptionOfType(EventoDuplicadoException.class)
            .isThrownBy(() -> eventoServicio.crearEvento(nuevo));

    // ASSERT
    verify(eventoRepo).findByNombre("Duplicado");
    verify(eventoRepo, never()).save(any(Evento.class));
    verifyNoInteractions(organizadorRepo);
}
```

**Paso a paso:**
1. **Línea 4**: Simulamos que ya existe un evento con ese nombre
2. **Línea 5**: Creamos un nuevo evento con el mismo nombre
3. **Línea 6**: Configuramos el mock para devolver el evento existente
4. **Línea 9-10**: Verificamos que lanza `EventoDuplicadoException`
5. **Línea 13**: Verificamos que se buscó por nombre
6. **Línea 14**: Verificamos que NUNCA se intentó guardar (porque ya existía)

### Test 6: Crear evento nuevo

```java
@Test
void crearEventoShouldSaveWhenNombreNoExiste() {
    // ARRANGE
    Evento nuevo = Evento.builder().nombre("Nuevo").descripcion("X").build();
    Evento guardado = Evento.builder().id(10L).nombre("Nuevo").descripcion("X").build();
    when(eventoRepo.findByNombre("Nuevo")).thenReturn(null);
    when(eventoRepo.save(any(Evento.class))).thenReturn(guardado);

    // ACT
    Evento creado = eventoServicio.crearEvento(nuevo);

    // ASSERT
    assertThat(creado).isNotNull();
    assertThat(creado.getId()).isEqualTo(10L);
    verify(eventoRepo).findByNombre("Nuevo");
    verify(eventoRepo).save(any(Evento.class));
    verifyNoInteractions(organizadorRepo);
}
```

**Paso a paso:**
1. **Línea 4**: Evento sin ID (no está guardado aún)
2. **Línea 5**: Evento con ID (como lo devolvería la BD después de guardar)
3. **Línea 6**: El mock indica que NO existe evento con ese nombre
4. **Línea 7**: El mock indica que al guardar, devuelve el evento con ID
5. **Línea 10**: Llamamos al método de crear
6. **Línea 13-14**: Verificamos que el evento creado tiene ID
7. **Línea 15-16**: Verificamos que se buscó por nombre Y se guardó

### Test 7: Actualizar evento inexistente

```java
@Test
void actualizarEventoShouldThrowWhenEventoNoExiste() {
    // ARRANGE
    when(eventoRepo.findById(1L)).thenReturn(Optional.empty());

    // ACT + ASSERT
    assertThatExceptionOfType(EventoNoEncontradoException.class)
            .isThrownBy(() -> eventoServicio.actualizarEvento(1L, new Evento()));

    // ASSERT
    verify(eventoRepo).findById(1L);
    verify(eventoRepo, never()).save(any(Evento.class));
    verifyNoInteractions(organizadorRepo);
}
```

**Paso a paso:**
1. **Línea 4**: El evento con ID 1 no existe
2. **Línea 7-8**: Verificamos que lanza excepción
3. **Línea 11**: Se intentó buscar
4. **Línea 12**: NUNCA se intentó guardar (porque no existía)

### Test 8: Eliminar evento inexistente

```java
@Test
void eliminarEventoShouldThrowWhenNoExiste() {
    // ARRANGE
    when(eventoRepo.findById(1L)).thenReturn(Optional.empty());

    // ACT + ASSERT
    assertThatExceptionOfType(EventoNoEncontradoException.class)
            .isThrownBy(() -> eventoServicio.eliminarEvento(1L));

    // ASSERT
    verify(eventoRepo).findById(1L);
    verify(eventoRepo, never()).delete(any(Evento.class));
    verifyNoInteractions(organizadorRepo);
}
```

**Paso a paso:**
1. **Línea 4**: El evento no existe
2. **Línea 7-8**: Debe lanzar excepción
3. **Línea 11**: Se buscó el evento
4. **Línea 12**: NUNCA se intentó eliminar

### Test 9: Eliminar evento existente

```java
@Test
void eliminarEventoShouldDeleteWhenExiste() {
    // ARRANGE
    Evento existente = Evento.builder().id(1L).nombre("Test").descripcion("Desc").build();
    when(eventoRepo.findById(1L)).thenReturn(Optional.of(existente));

    // ACT
    eventoServicio.eliminarEvento(1L);

    // ASSERT
    verify(eventoRepo).findById(1L);
    verify(eventoRepo).delete(existente);
    verifyNoInteractions(organizadorRepo);
}
```

**Paso a paso:**
1. **Línea 4-5**: El evento existe
2. **Línea 8**: Se elimina (método void, no devuelve nada)
3. **Línea 11**: Se buscó el evento
4. **Línea 12**: Se eliminó el evento específico
5. **Línea 13**: No se usó el repositorio de organizadores

---

## Ejercicio Práctico

### Test pendiente: Actualizar evento completo

Completa el siguiente test siguiendo los pasos:

```java
@Test
void actualizarEventoShouldActualizarCamposBasicosYOrganizadorYParticipantes() {
    // TODO: COMPLETAR CON LOS ALUMNOS
    // Objetivo de este test:
    //  - Dado un Evento existente en la BD
    //  - Y un objeto "cambios" con nuevo nombre, descripción, tipo, fechas, organizador y participantes
    //  - Cuando llamamos a eventoServicio.actualizarEvento(id, cambios)
    //  - Entonces se deben actualizar:
    //      * nombre, descripcion, tipo, fechaInicio, fechaFin
    //      * organizador (buscándolo en organizadorRepo por id)
    //      * añadir los nuevos participantes a la lista existente (sin perder los que hubiera)
    //
    // Pistas:
    //  - Usa mocks de eventoRepo y organizadorRepo con Mockito
    //  - eventoRepo.findById(id) debe devolver un Evento "existente"
    //  - organizadorRepo.findById(idOrganizador) debe devolver el Organizador
    //  - eventoRepo.save(...) puede devolver el mismo objeto que recibe (thenAnswer)
    //  - Verifica con assertThat(...) que los cambios se han aplicado correctamente
    //
    // Nota: este test se deja intencionadamente como TODO para practicar TDD en clase.
}
```

### Solución paso a paso

**Paso 1: Crear el evento existente**
```java
Evento existente = Evento.builder()
        .id(1L)
        .nombre("Evento Original")
        .descripcion("Descripción Original")
        .tipo(TipoEvento.CONFERENCIA)
        .fechaInicio(LocalDateTime.of(2024, 1, 1, 10, 0))
        .fechaFin(LocalDateTime.of(2024, 1, 1, 12, 0))
        .participantes(new ArrayList<>(List.of(
                Participante.builder().id(1L).nombre("Participante 1").build()
        )))
        .build();
```

**Paso 2: Crear el objeto con los cambios**
```java
Organizador nuevoOrganizador = Organizador.builder()
        .id(5L)
        .nombre("Organizador Nuevo")
        .build();

Evento cambios = Evento.builder()
        .nombre("Evento Actualizado")
        .descripcion("Descripción Actualizada")
        .tipo(TipoEvento.TALLER)
        .fechaInicio(LocalDateTime.of(2024, 2, 1, 14, 0))
        .fechaFin(LocalDateTime.of(2024, 2, 1, 16, 0))
        .organizador(nuevoOrganizador)
        .participantes(List.of(
                Participante.builder().id(2L).nombre("Participante 2").build()
        ))
        .build();
```

**Paso 3: Configurar los mocks**
```java
when(eventoRepo.findById(1L)).thenReturn(Optional.of(existente));
when(organizadorRepo.findById(5L)).thenReturn(Optional.of(nuevoOrganizador));
when(eventoRepo.save(any(Evento.class))).thenAnswer(invocation -> invocation.getArgument(0));
```

**Explicación de cada línea:**
- Línea 1: Cuando se busque el evento por ID 1, devuelve el existente
- Línea 2: Cuando se busque el organizador por ID 5, devuelve el nuevo organizador
- Línea 3: Cuando se guarde, devuelve el mismo objeto que se pasó (simula el comportamiento real)

**Paso 4: Ejecutar el método**
```java
Evento actualizado = eventoServicio.actualizarEvento(1L, cambios);
```

**Paso 5: Verificar campos básicos**
```java
assertThat(actualizado.getNombre()).isEqualTo("Evento Actualizado");
assertThat(actualizado.getDescripcion()).isEqualTo("Descripción Actualizada");
assertThat(actualizado.getTipo()).isEqualTo(TipoEvento.TALLER);
assertThat(actualizado.getFechaInicio()).isEqualTo(LocalDateTime.of(2024, 2, 1, 14, 0));
assertThat(actualizado.getFechaFin()).isEqualTo(LocalDateTime.of(2024, 2, 1, 16, 0));
```

**Paso 6: Verificar organizador**
```java
assertThat(actualizado.getOrganizador()).isNotNull();
assertThat(actualizado.getOrganizador().getId()).isEqualTo(5L);
assertThat(actualizado.getOrganizador().getNombre()).isEqualTo("Organizador Nuevo");
```

**Paso 7: Verificar participantes (sin perder los existentes)**
```java
assertThat(actualizado.getParticipantes()).hasSize(2);
assertThat(actualizado.getParticipantes())
        .extracting("id")
        .containsExactlyInAnyOrder(1L, 2L);
```

**Explicación:**
- La lista debe tener 2 participantes (el original + el nuevo)
- Extraemos los IDs de todos los participantes
- Verificamos que contenga tanto el ID 1 (original) como el ID 2 (nuevo), en cualquier orden

**Paso 8: Verificar interacciones con los mocks**
```java
verify(eventoRepo).findById(1L);
verify(organizadorRepo).findById(5L);
verify(eventoRepo).save(any(Evento.class));
verifyNoMoreInteractions(eventoRepo, organizadorRepo);
```

**Explicación:**
- Se buscó el evento por ID
- Se buscó el organizador por ID
- Se guardó el evento actualizado
- No hubo más interacciones con los repositorios

---

## Resumen de Patrones Comunes

### Patrón: Probar operación exitosa
```java
@Test
void operacionExitosa() {
    // ARRANGE: Configurar mocks para caso exitoso
    when(repo.metodo()).thenReturn(valor);

    // ACT: Ejecutar método
    Resultado resultado = servicio.metodo();

    // ASSERT: Verificar resultado y llamadas
    assertThat(resultado).isNotNull();
    verify(repo).metodo();
}
```

### Patrón: Probar que lanza excepción
```java
@Test
void operacionFalla() {
    // ARRANGE: Configurar caso de error
    when(repo.findById(id)).thenReturn(Optional.empty());

    // ACT + ASSERT: Verificar excepción
    assertThatExceptionOfType(MiExcepcion.class)
            .isThrownBy(() -> servicio.metodo(id));

    // ASSERT: Verificar que no se guardó
    verify(repo, never()).save(any());
}
```

### Patrón: Probar validaciones
```java
@Test
void validacionFalla() {
    // ARRANGE: Dato inválido
    when(repo.buscar(dato)).thenReturn(yaExiste);

    // ACT + ASSERT
    assertThatExceptionOfType(ValidacionException.class)
            .isThrownBy(() -> servicio.crear(dato));

    verify(repo, never()).save(any());
}
```

---

## Buenas Prácticas

1. **Nombres descriptivos**: El nombre del test debe explicar qué se prueba y qué se espera
   - ✅ `crearEventoShouldThrowWhenNombreYaExiste`
   - ❌ `test1`

2. **Un concepto por test**: Cada test debe probar una sola cosa
   - ✅ Test separados para caso exitoso y caso de error
   - ❌ Un test que prueba 10 escenarios diferentes

3. **AAA Pattern**: Arrange, Act, Assert
   ```java
   // ARRANGE: Preparar
   // ACT: Ejecutar
   // ASSERT: Verificar
   ```

4. **Verificar todo lo necesario**:
   - Resultado del método
   - Excepciones lanzadas
   - Llamadas a dependencias
   - Que NO se llamen métodos innecesarios

5. **Usar `verifyNoInteractions()`**: Para asegurar que no se usaron dependencias innecesarias

6. **Tests independientes**: Cada test debe funcionar solo, sin depender de otros

---

## Glosario

- **Mock**: Objeto simulado que reemplaza una dependencia real
- **Stub**: Configuración de un mock para devolver valores específicos
- **Verify**: Comprobar que un método del mock fue llamado
- **Assert**: Verificar que un resultado es el esperado
- **AAA**: Arrange, Act, Assert (patrón para estructurar tests)
- **TDD**: Test-Driven Development (primero test, luego código)
- **Matcher**: Patrón para verificar argumentos (`any()`, `eq()`, etc.)
- **InjectMocks**: Inyecta mocks automáticamente en el objeto a probar

---

## Ejercicios Adicionales

1. **Añadir test**: Probar `obtenEventoPorNombre()` cuando existe y cuando no existe
2. **Añadir test**: Probar que `actualizarEvento()` no actualiza campos nulos
3. **Refactorizar**: Extraer la creación de eventos de prueba a métodos helper
4. **Investigar**: Cómo usar `@MockBean` en tests de integración con Spring Boot

---

**¡Éxito con los tests!** 🚀

## Tabla Resumen de Sintaxis Común

| Categoría      | Sintaxis                      | Ejemplo                                             |
| -------------- | ----------------------------- | --------------------------------------------------- |
| Stubbing       | `when(...).thenReturn()`      | `when(repo.findById(1)).thenReturn(Optional.of(e))` |
| Excepciones    | `thenThrow()`                 | `when(repo.save(any())).thenThrow(new X())`         |
| Void           | `doNothing().when(...)`       | `doNothing().when(repo).deleteById(1)`              |
| Verificación   | `verify(repo).método()`       | `verify(repo).save(e)`                              |
| Captura        | `ArgumentCaptor`              | `captor.getValue()`                                 |
| AssertJ básico | `assertThat(x).isEqualTo(y)`  | —                                                   |
| AssertJ listas | `.hasSize()`, `.contains()`   | —                                                   |
| Excepciones    | `assertThatExceptionOfType()` | —                                                   |# Guía de Tests Unitarios con Mockito y AssertJ
## Índice
1. [Introducción](#introducción)
2. [Conceptos Básicos](#conceptos-básicos)
3. [Configuración de Tests](#configuración-de-tests)
4. [Mocks y Stubs](#mocks-y-stubs)
5. [Stubbing Avanzado](#stubbing-avanzado)
6. [Assertions (AssertJ)](#assertions-assertj)
7. [Aserciones Avanzadas](#aserciones-avanzadas)
8. [Verificaciones (Mockito)](#verificaciones-mockito)
9. [Verificaciones Avanzadas](#verificaciones-avanzadas)
10. [ArgumentCaptor](#argumentcaptor)
11. [BDD Mockito](#bdd-mockito)
12. [Métodos VOID en Mockito](#métodos-void-en-mockito)
13. [Análisis de Tests Completos](#análisis-de-tests-completos)
14. [Ejercicio Práctico](#ejercicio-práctico)

---

## Introducción

Los **tests unitarios** son pruebas automatizadas que verifican el comportamiento de unidades pequeñas de código (métodos, clases) de forma aislada.

**¿Por qué usar mocks?**
- Aíslan la unidad bajo prueba de sus dependencias (repositorios, servicios externos, etc.)
- Permiten controlar el comportamiento de las dependencias
- Hacen los tests rápidos (no acceden a base de datos real)
- Permiten probar casos de error sin necesidad de provocarlos realmente

**Herramientas principales:**
- **JUnit 5**: Framework de testing para Java
- **Mockito**: Framework para crear mocks (objetos simulados)
- **AssertJ**: Biblioteca para hacer aserciones (verificaciones) más legibles

---

## Conceptos Básicos

### ¿Qué es un Mock?

Un **mock** es un objeto simulado que imita el comportamiento de un objeto real. Se usa para:
- Sustituir dependencias (repositorios, servicios, etc.)
- Controlar qué devuelven los métodos
- Verificar que se llamaron correctamente

### ¿Qué es un Stub?

Un **stub** es cuando configuramos un mock para que devuelva valores específicos cuando se llaman sus métodos.

### ¿Qué es una Aserción?

Una **aserción** es una verificación que comprueba si un resultado es el esperado. Si falla, el test falla.

### Estructura básica de un test

```java
@Test
void nombreDescriptivoDelTest() {
    // 1. ARRANGE (Preparar): Configurar datos y mocks
    // 2. ACT (Actuar): Ejecutar el método a probar
    // 3. ASSERT (Afirmar): Verificar el resultado
}
```
---

## Configuración de Tests
### Forma recomendada (JUnit 5)

```java
@ExtendWith(MockitoExtension.class)
class EventoServicioTest {

    @Mock
    EventoRepo eventoRepo;

    @InjectMocks
    EventoServicio eventoServicio;
}
```
✔️ **No necesitas `openMocks()`**
✔️ Más limpio y moderno

### Forma clásica (válida)

```java
@BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);
}
```



### Anotaciones principales

```java
class EventoServicioTest {

    @Mock  // Crea un mock (objeto simulado) de EventoRepo
    private EventoRepo eventoRepo;

    @Mock  // Crea un mock de OrganizadorRepo
    private OrganizadorRepo organizadorRepo;

    @InjectMocks  // Crea una instancia de EventoServicio inyectando los mocks anteriores
    private EventoServicio eventoServicio;

    @BeforeEach  // Se ejecuta ANTES de cada test
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Inicializa los mocks
    }
}
```

**Explicación:**
- `@Mock`: Crea objetos simulados (no reales)
- `@InjectMocks`: Crea el objeto a probar e inyecta los mocks en él
- `@BeforeEach`: Método que se ejecuta antes de cada test (útil para inicializar)
- `MockitoAnnotations.openMocks(this)`: Activa los mocks anotados

---

## Mocks y Stubs

### `when().thenReturn()` - Configurar respuestas

**Sintaxis:**
```java
when(mock.metodo(parametros)).thenReturn(valorADevolver);
```

**Ejemplo 1: Devolver un objeto**
```java
Evento evento = Evento.builder().id(1L).nombre("Test").build();
when(eventoRepo.findById(1L)).thenReturn(Optional.of(evento));
```
➡️ **Significado**: "Cuando se llame a `eventoRepo.findById(1L)`, devuelve `Optional.of(evento)`"

**Ejemplo 2: Devolver una lista**
```java
when(eventoRepo.findAll()).thenReturn(List.of(evento1, evento2));
```
➡️ **Significado**: "Cuando se llame a `findAll()`, devuelve una lista con 2 eventos"

**Ejemplo 3: Devolver null**
```java
when(eventoRepo.findByNombre("Inexistente")).thenReturn(null);
```
➡️ **Significado**: "Cuando se busque ese nombre, no se encuentra nada (null)"

**Ejemplo 4: Devolver Optional vacío**
```java
when(eventoRepo.findById(99L)).thenReturn(Optional.empty());
```
➡️ **Significado**: "Cuando se busque ese ID, no existe"

### `when().thenAnswer()` - Respuestas dinámicas

**Uso:** Cuando necesitas devolver algo basado en los argumentos recibidos.

```java
when(eventoRepo.save(any(Evento.class)))
    .thenAnswer(invocation -> invocation.getArgument(0));
```
➡️ **Significado**: "Cuando se llame a `save()`, devuelve el mismo objeto que recibió como argumento"

**Desglose:**
- `invocation`: Información sobre la llamada al método
- `invocation.getArgument(0)`: Obtiene el primer argumento (índice 0)

### `any()` - Matchers de argumentos

```java
when(eventoRepo.save(any(Evento.class))).thenReturn(evento);
```
➡️ **Significado**: "Cuando se llame a `save()` con **cualquier** objeto de tipo Evento"

**Otros matchers útiles:**
- `anyLong()`: Cualquier Long
- `anyString()`: Cualquier String
- `eq(valor)`: Un valor específico
- `isNull()`: Cuando el argumento es null

---
## Stubbing Avanzado

### ➤ **thenThrow()**

```java
when(repo.findById(1L)).thenThrow(new RuntimeException("Error"));
```

### ➤ **thenCallRealMethod()**

```java
when(servicioReal.metodo()).thenCallRealMethod();
```

### ➤ **Stubbing consecutivo**

```java
when(repo.findAll())
    .thenReturn(List.of(a))
    .thenReturn(List.of(b))
    .thenReturn(List.of(c));
```

### ➤ **doReturn()** (evita problemas con mocks parciales)

```java
doReturn(evento).when(repo).save(any());
```

### ➤ **Métodos VOID → doNothing(), doThrow()**

```java
doNothing().when(repo).delete(any());
doThrow(new IllegalStateException()).when(repo).deleteById(5L);
```

### ➤ **doAnswer() para métodos void**

```java
doAnswer(inv -> {
    System.out.println("Borrando " + inv.getArgument(0));
    return null;
}).when(repo).delete(any());
```
## Assertions (AssertJ)

AssertJ proporciona una sintaxis fluida y legible para hacer verificaciones.

### `assertThat()` - Verificaciones básicas

**Sintaxis:**
```java
assertThat(valor).condicion();
```

### Verificaciones comunes

**1. Verificar igualdad**
```java
assertThat(evento.getNombre()).isEqualTo("Test");
```
➡️ "Verifica que el nombre sea 'Test'"

**2. Verificar que NO es null**
```java
assertThat(evento).isNotNull();
```
➡️ "Verifica que el evento no sea nulo"

**3. Verificar que SÍ es null**
```java
assertThat(evento).isNull();
```
➡️ "Verifica que el evento sea nulo"

**4. Verificar tamaño de lista**
```java
assertThat(listaEventos).hasSize(3);
```
➡️ "Verifica que la lista tenga 3 elementos"

**5. Verificar que lista está vacía**
```java
assertThat(listaEventos).isEmpty();
```
➡️ "Verifica que la lista esté vacía"

**6. Verificar que lista NO está vacía**
```java
assertThat(listaEventos).isNotEmpty();
```

**7. Verificar que lista contiene elementos**
```java
assertThat(listaEventos).contains(evento1, evento2);
```
➡️ "Verifica que la lista contenga esos eventos"

**8. Extraer y verificar propiedades**
```java
assertThat(listaEventos)
    .extracting("nombre")
    .containsExactlyInAnyOrder("Evento1", "Evento2");
```
➡️ "Extrae el campo 'nombre' de cada evento y verifica que contenga esos valores (en cualquier orden)"

### `assertThatExceptionOfType()` - Verificar excepciones

**Sintaxis:**
```java
assertThatExceptionOfType(TipoExcepcion.class)
    .isThrownBy(() -> codigo_que_debe_lanzar_excepcion);
```

**Ejemplo completo:**
```java
assertThatExceptionOfType(EventoNoEncontradoException.class)
    .isThrownBy(() -> eventoServicio.obtenEventoPorId(99L));
```
➡️ **Significado**: "Verifica que al llamar a `obtenEventoPorId(99L)` se lance una excepción de tipo `EventoNoEncontradoException`"

**Verificar también el mensaje:**
```java
assertThatExceptionOfType(EventoNoEncontradoException.class)
    .isThrownBy(() -> eventoServicio.obtenEventoPorId(99L))
    .withMessage("Evento no encontrado con id=99");
```

## Aserciones Avanzadas

### Sobre números

```java
assertThat(total).isGreaterThan(0);
assertThat(total).isBetween(5, 10);
```

### Sobre cadenas de texto

```java
assertThat(nombre)
    .startsWith("Ev")
    .contains("ven")
    .endsWith("to");
```

### Sobre Optional

```java
assertThat(optional).isPresent();
assertThat(optional).contains(evento);
```

### Sobre Mapas

```java
assertThat(mapa).containsKey("id");
assertThat(mapa).containsEntry("nombre", "Evento X");
```

### Extraer propiedades

```java
assertThat(lista)
    .extracting("id")
    .containsExactly(1L, 2L, 3L);
```
---

## Verificaciones (Mockito)
Las verificaciones comprueban que los mocks fueron usados correctamente.

### `verify()` - Verificar llamadas a métodos

**Sintaxis:**
```java
verify(mock).metodo(parametros);
```

**Ejemplo 1: Verificar que se llamó**
```java
verify(eventoRepo).findById(1L);
```
➡️ "Verifica que se llamó a `findById(1L)` exactamente 1 vez"

**Ejemplo 2: Verificar múltiples llamadas**
```java
verify(eventoRepo, times(2)).findAll();
```
➡️ "Verifica que se llamó a `findAll()` exactamente 2 veces"

**Ejemplo 3: Verificar que NO se llamó**
```java
verify(eventoRepo, never()).save(any(Evento.class));
```
➡️ "Verifica que NUNCA se llamó a `save()`"

**Ejemplo 4: Verificar al menos una vez**
```java
verify(eventoRepo, atLeastOnce()).findAll();
```

**Ejemplo 5: Verificar que se llamó con un argumento específico**
```java
verify(eventoRepo).delete(eventoEspecifico);
```
➡️ "Verifica que se llamó a `delete()` con ese objeto específico"

### `verifyNoInteractions()` - Verificar que no hubo llamadas

```java
verifyNoInteractions(organizadorRepo);
```
➡️ "Verifica que NO se llamó a NINGÚN método de `organizadorRepo`"

**Uso típico:** Cuando un test no debería usar cierta dependencia.

### `verifyNoMoreInteractions()` - Verificar que no hay más llamadas

```java
verify(eventoRepo).findById(1L);
verifyNoMoreInteractions(eventoRepo);
```
➡️ "Verifica que solo se llamó a `findById()` y a ningún otro método"

---
# Verificaciones Avanzadas

### Al menos / como mucho

```java
verify(repo, atLeast(1)).findAll();
verify(repo, atMost(3)).save(any());
```

### Solo ese método

```java
verify(repo, only()).findAll();
```

### Verificación con timeout (hilos)

```java
verify(repo, timeout(200)).findAll();
```

---

## ArgumentCaptor

Muy útil para comprobar qué datos se guardan realmente.

```java
ArgumentCaptor<Evento> captor = ArgumentCaptor.forClass(Evento.class);
verify(eventoRepo).save(captor.capture());

Evento guardado = captor.getValue();
assertThat(guardado.getNombre()).isEqualTo("Nuevo evento");
```

---

## BDD Mockito

Alternativa más legible:

```java
given(repo.findById(1L)).willReturn(Optional.of(evento));

Evento e = servicio.obtenEvento(1L);

then(repo).should().findById(1L);
```

---

## Métodos VOID en Mockito

### doNothing()

```java
doNothing().when(repo).deleteById(1L);
```

### doThrow()

```java
doThrow(new IllegalStateException())
    .when(repo).deleteById(5L);
```

### Verificar

```java
verify(repo).deleteById(1L);
```
---
## Análisis de Tests Completos

### Test 1: Listar eventos exitosamente

```java
@Test
void listarEventosShouldDelegateToRepo() {
    // ARRANGE: Preparar datos
    Evento e = Evento.builder().id(1L).nombre("Test").descripcion("Desc").build();
    when(eventoRepo.findAll()).thenReturn(List.of(e));

    // ACT: Ejecutar el método
    List<Evento> result = eventoServicio.listarEventos();

    // ASSERT: Verificar resultados
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(1L);
    verify(eventoRepo).findAll();
    verifyNoInteractions(organizadorRepo);
}
```

**Paso a paso:**
1. **Línea 4**: Creamos un evento de prueba usando el patrón Builder
2. **Línea 5**: Configuramos el mock para que `findAll()` devuelva una lista con ese evento
3. **Línea 8**: Ejecutamos el método que queremos probar
4. **Línea 11**: Verificamos que la lista tiene 1 elemento
5. **Línea 12**: Verificamos que el ID del primer elemento es 1L
6. **Línea 13**: Verificamos que se llamó a `findAll()` del repositorio
7. **Línea 14**: Verificamos que NO se usó el repositorio de organizadores (no era necesario)

### Test 2: Listar eventos cuando no hay ninguno

```java
@Test
void listarEventosShouldThrowWhenEmpty() {
    // ARRANGE: Configurar repo vacío
    when(eventoRepo.findAll()).thenReturn(List.of());

    // ACT + ASSERT: Verificar que lanza excepción
    assertThatExceptionOfType(EventoNoEncontradoException.class)
            .isThrownBy(() -> eventoServicio.listarEventos());

    // ASSERT: Verificar interacciones
    verify(eventoRepo).findAll();
    verifyNoInteractions(organizadorRepo);
}
```

**Paso a paso:**
1. **Línea 4**: El mock devuelve una lista vacía
2. **Línea 7-8**: Verificamos que el servicio lanza `EventoNoEncontradoException` cuando no hay eventos
3. **Línea 11**: Verificamos que sí se intentó buscar en el repositorio
4. **Línea 12**: Verificamos que no se usó el repositorio de organizadores

### Test 3: Obtener evento por ID existente

```java
@Test
void obtenEventoPorIdShouldReturnEventoWhenExists() {
    // ARRANGE
    Evento e = Evento.builder().id(1L).nombre("Test").descripcion("Desc").build();
    when(eventoRepo.findById(1L)).thenReturn(Optional.of(e));

    // ACT
    Evento found = eventoServicio.obtenEventoPorId(1L);

    // ASSERT
    assertThat(found).isNotNull();
    assertThat(found.getId()).isEqualTo(1L);
    verify(eventoRepo).findById(1L);
    verifyNoInteractions(organizadorRepo);
}
```

**Paso a paso:**
1. **Línea 4**: Creamos un evento de prueba
2. **Línea 5**: Configuramos el mock para devolver un Optional con el evento cuando se busque por ID 1L
3. **Línea 8**: Llamamos al método del servicio
4. **Línea 11**: Verificamos que el resultado NO es null
5. **Línea 12**: Verificamos que el ID es correcto
6. **Línea 13**: Verificamos que se llamó a `findById(1L)`

### Test 4: Obtener evento por ID inexistente

```java
@Test
void obtenEventoPorIdShouldThrowWhenNotExists() {
    // ARRANGE
    when(eventoRepo.findById(99L)).thenReturn(Optional.empty());

    // ACT + ASSERT
    assertThatExceptionOfType(EventoNoEncontradoException.class)
            .isThrownBy(() -> eventoServicio.obtenEventoPorId(99L));

    // ASSERT
    verify(eventoRepo).findById(99L);
    verifyNoInteractions(organizadorRepo);
}
```

**Paso a paso:**
1. **Línea 4**: El mock devuelve un Optional vacío (no se encontró)
2. **Línea 7-8**: Verificamos que el servicio lanza una excepción
3. **Línea 11**: Verificamos que sí se intentó buscar

### Test 5: Crear evento con nombre duplicado

```java
@Test
void crearEventoShouldThrowWhenNombreYaExiste() {
    // ARRANGE
    Evento existente = Evento.builder().id(1L).nombre("Duplicado").descripcion("Desc").build();
    Evento nuevo = Evento.builder().nombre("Duplicado").descripcion("X").build();
    when(eventoRepo.findByNombre("Duplicado")).thenReturn(existente);

    // ACT + ASSERT
    assertThatExceptionOfType(EventoDuplicadoException.class)
            .isThrownBy(() -> eventoServicio.crearEvento(nuevo));

    // ASSERT
    verify(eventoRepo).findByNombre("Duplicado");
    verify(eventoRepo, never()).save(any(Evento.class));
    verifyNoInteractions(organizadorRepo);
}
```

**Paso a paso:**
1. **Línea 4**: Simulamos que ya existe un evento con ese nombre
2. **Línea 5**: Creamos un nuevo evento con el mismo nombre
3. **Línea 6**: Configuramos el mock para devolver el evento existente
4. **Línea 9-10**: Verificamos que lanza `EventoDuplicadoException`
5. **Línea 13**: Verificamos que se buscó por nombre
6. **Línea 14**: Verificamos que NUNCA se intentó guardar (porque ya existía)

### Test 6: Crear evento nuevo

```java
@Test
void crearEventoShouldSaveWhenNombreNoExiste() {
    // ARRANGE
    Evento nuevo = Evento.builder().nombre("Nuevo").descripcion("X").build();
    Evento guardado = Evento.builder().id(10L).nombre("Nuevo").descripcion("X").build();
    when(eventoRepo.findByNombre("Nuevo")).thenReturn(null);
    when(eventoRepo.save(any(Evento.class))).thenReturn(guardado);

    // ACT
    Evento creado = eventoServicio.crearEvento(nuevo);

    // ASSERT
    assertThat(creado).isNotNull();
    assertThat(creado.getId()).isEqualTo(10L);
    verify(eventoRepo).findByNombre("Nuevo");
    verify(eventoRepo).save(any(Evento.class));
    verifyNoInteractions(organizadorRepo);
}
```

**Paso a paso:**
1. **Línea 4**: Evento sin ID (no está guardado aún)
2. **Línea 5**: Evento con ID (como lo devolvería la BD después de guardar)
3. **Línea 6**: El mock indica que NO existe evento con ese nombre
4. **Línea 7**: El mock indica que al guardar, devuelve el evento con ID
5. **Línea 10**: Llamamos al método de crear
6. **Línea 13-14**: Verificamos que el evento creado tiene ID
7. **Línea 15-16**: Verificamos que se buscó por nombre Y se guardó

### Test 7: Actualizar evento inexistente

```java
@Test
void actualizarEventoShouldThrowWhenEventoNoExiste() {
    // ARRANGE
    when(eventoRepo.findById(1L)).thenReturn(Optional.empty());

    // ACT + ASSERT
    assertThatExceptionOfType(EventoNoEncontradoException.class)
            .isThrownBy(() -> eventoServicio.actualizarEvento(1L, new Evento()));

    // ASSERT
    verify(eventoRepo).findById(1L);
    verify(eventoRepo, never()).save(any(Evento.class));
    verifyNoInteractions(organizadorRepo);
}
```

**Paso a paso:**
1. **Línea 4**: El evento con ID 1 no existe
2. **Línea 7-8**: Verificamos que lanza excepción
3. **Línea 11**: Se intentó buscar
4. **Línea 12**: NUNCA se intentó guardar (porque no existía)

### Test 8: Eliminar evento inexistente

```java
@Test
void eliminarEventoShouldThrowWhenNoExiste() {
    // ARRANGE
    when(eventoRepo.findById(1L)).thenReturn(Optional.empty());

    // ACT + ASSERT
    assertThatExceptionOfType(EventoNoEncontradoException.class)
            .isThrownBy(() -> eventoServicio.eliminarEvento(1L));

    // ASSERT
    verify(eventoRepo).findById(1L);
    verify(eventoRepo, never()).delete(any(Evento.class));
    verifyNoInteractions(organizadorRepo);
}
```

**Paso a paso:**
1. **Línea 4**: El evento no existe
2. **Línea 7-8**: Debe lanzar excepción
3. **Línea 11**: Se buscó el evento
4. **Línea 12**: NUNCA se intentó eliminar

### Test 9: Eliminar evento existente

```java
@Test
void eliminarEventoShouldDeleteWhenExiste() {
    // ARRANGE
    Evento existente = Evento.builder().id(1L).nombre("Test").descripcion("Desc").build();
    when(eventoRepo.findById(1L)).thenReturn(Optional.of(existente));

    // ACT
    eventoServicio.eliminarEvento(1L);

    // ASSERT
    verify(eventoRepo).findById(1L);
    verify(eventoRepo).delete(existente);
    verifyNoInteractions(organizadorRepo);
}
```

**Paso a paso:**
1. **Línea 4-5**: El evento existe
2. **Línea 8**: Se elimina (método void, no devuelve nada)
3. **Línea 11**: Se buscó el evento
4. **Línea 12**: Se eliminó el evento específico
5. **Línea 13**: No se usó el repositorio de organizadores

---

## Ejercicio Práctico

### Test pendiente: Actualizar evento completo

Completa el siguiente test siguiendo los pasos:

```java
@Test
void actualizarEventoShouldActualizarCamposBasicosYOrganizadorYParticipantes() {
    // TODO: COMPLETAR CON LOS ALUMNOS
    // Objetivo de este test:
    //  - Dado un Evento existente en la BD
    //  - Y un objeto "cambios" con nuevo nombre, descripción, tipo, fechas, organizador y participantes
    //  - Cuando llamamos a eventoServicio.actualizarEvento(id, cambios)
    //  - Entonces se deben actualizar:
    //      * nombre, descripcion, tipo, fechaInicio, fechaFin
    //      * organizador (buscándolo en organizadorRepo por id)
    //      * añadir los nuevos participantes a la lista existente (sin perder los que hubiera)
    //
    // Pistas:
    //  - Usa mocks de eventoRepo y organizadorRepo con Mockito
    //  - eventoRepo.findById(id) debe devolver un Evento "existente"
    //  - organizadorRepo.findById(idOrganizador) debe devolver el Organizador
    //  - eventoRepo.save(...) puede devolver el mismo objeto que recibe (thenAnswer)
    //  - Verifica con assertThat(...) que los cambios se han aplicado correctamente
    //
    // Nota: este test se deja intencionadamente como TODO para practicar TDD en clase.
}
```

### Solución paso a paso

**Paso 1: Crear el evento existente**
```java
Evento existente = Evento.builder()
        .id(1L)
        .nombre("Evento Original")
        .descripcion("Descripción Original")
        .tipo(TipoEvento.CONFERENCIA)
        .fechaInicio(LocalDateTime.of(2024, 1, 1, 10, 0))
        .fechaFin(LocalDateTime.of(2024, 1, 1, 12, 0))
        .participantes(new ArrayList<>(List.of(
                Participante.builder().id(1L).nombre("Participante 1").build()
        )))
        .build();
```

**Paso 2: Crear el objeto con los cambios**
```java
Organizador nuevoOrganizador = Organizador.builder()
        .id(5L)
        .nombre("Organizador Nuevo")
        .build();

Evento cambios = Evento.builder()
        .nombre("Evento Actualizado")
        .descripcion("Descripción Actualizada")
        .tipo(TipoEvento.TALLER)
        .fechaInicio(LocalDateTime.of(2024, 2, 1, 14, 0))
        .fechaFin(LocalDateTime.of(2024, 2, 1, 16, 0))
        .organizador(nuevoOrganizador)
        .participantes(List.of(
                Participante.builder().id(2L).nombre("Participante 2").build()
        ))
        .build();
```

**Paso 3: Configurar los mocks**
```java
when(eventoRepo.findById(1L)).thenReturn(Optional.of(existente));
when(organizadorRepo.findById(5L)).thenReturn(Optional.of(nuevoOrganizador));
when(eventoRepo.save(any(Evento.class))).thenAnswer(invocation -> invocation.getArgument(0));
```

**Explicación de cada línea:**
- Línea 1: Cuando se busque el evento por ID 1, devuelve el existente
- Línea 2: Cuando se busque el organizador por ID 5, devuelve el nuevo organizador
- Línea 3: Cuando se guarde, devuelve el mismo objeto que se pasó (simula el comportamiento real)

**Paso 4: Ejecutar el método**
```java
Evento actualizado = eventoServicio.actualizarEvento(1L, cambios);
```

**Paso 5: Verificar campos básicos**
```java
assertThat(actualizado.getNombre()).isEqualTo("Evento Actualizado");
assertThat(actualizado.getDescripcion()).isEqualTo("Descripción Actualizada");
assertThat(actualizado.getTipo()).isEqualTo(TipoEvento.TALLER);
assertThat(actualizado.getFechaInicio()).isEqualTo(LocalDateTime.of(2024, 2, 1, 14, 0));
assertThat(actualizado.getFechaFin()).isEqualTo(LocalDateTime.of(2024, 2, 1, 16, 0));
```

**Paso 6: Verificar organizador**
```java
assertThat(actualizado.getOrganizador()).isNotNull();
assertThat(actualizado.getOrganizador().getId()).isEqualTo(5L);
assertThat(actualizado.getOrganizador().getNombre()).isEqualTo("Organizador Nuevo");
```

**Paso 7: Verificar participantes (sin perder los existentes)**
```java
assertThat(actualizado.getParticipantes()).hasSize(2);
assertThat(actualizado.getParticipantes())
        .extracting("id")
        .containsExactlyInAnyOrder(1L, 2L);
```

**Explicación:**
- La lista debe tener 2 participantes (el original + el nuevo)
- Extraemos los IDs de todos los participantes
- Verificamos que contenga tanto el ID 1 (original) como el ID 2 (nuevo), en cualquier orden

**Paso 8: Verificar interacciones con los mocks**
```java
verify(eventoRepo).findById(1L);
verify(organizadorRepo).findById(5L);
verify(eventoRepo).save(any(Evento.class));
verifyNoMoreInteractions(eventoRepo, organizadorRepo);
```

**Explicación:**
- Se buscó el evento por ID
- Se buscó el organizador por ID
- Se guardó el evento actualizado
- No hubo más interacciones con los repositorios

---

## Resumen de Patrones Comunes

### Patrón: Probar operación exitosa
```java
@Test
void operacionExitosa() {
    // ARRANGE: Configurar mocks para caso exitoso
    when(repo.metodo()).thenReturn(valor);

    // ACT: Ejecutar método
    Resultado resultado = servicio.metodo();

    // ASSERT: Verificar resultado y llamadas
    assertThat(resultado).isNotNull();
    verify(repo).metodo();
}
```

### Patrón: Probar que lanza excepción
```java
@Test
void operacionFalla() {
    // ARRANGE: Configurar caso de error
    when(repo.findById(id)).thenReturn(Optional.empty());

    // ACT + ASSERT: Verificar excepción
    assertThatExceptionOfType(MiExcepcion.class)
            .isThrownBy(() -> servicio.metodo(id));

    // ASSERT: Verificar que no se guardó
    verify(repo, never()).save(any());
}
```

### Patrón: Probar validaciones
```java
@Test
void validacionFalla() {
    // ARRANGE: Dato inválido
    when(repo.buscar(dato)).thenReturn(yaExiste);

    // ACT + ASSERT
    assertThatExceptionOfType(ValidacionException.class)
            .isThrownBy(() -> servicio.crear(dato));

    verify(repo, never()).save(any());
}
```

---

## Buenas Prácticas

1. **Nombres descriptivos**: El nombre del test debe explicar qué se prueba y qué se espera
   - ✅ `crearEventoShouldThrowWhenNombreYaExiste`
   - ❌ `test1`

2. **Un concepto por test**: Cada test debe probar una sola cosa
   - ✅ Test separados para caso exitoso y caso de error
   - ❌ Un test que prueba 10 escenarios diferentes

3. **AAA Pattern**: Arrange, Act, Assert
   ```java
   // ARRANGE: Preparar
   // ACT: Ejecutar
   // ASSERT: Verificar
   ```

4. **Verificar todo lo necesario**:
   - Resultado del método
   - Excepciones lanzadas
   - Llamadas a dependencias
   - Que NO se llamen métodos innecesarios

5. **Usar `verifyNoInteractions()`**: Para asegurar que no se usaron dependencias innecesarias

6. **Tests independientes**: Cada test debe funcionar solo, sin depender de otros

---

## Glosario

- **Mock**: Objeto simulado que reemplaza una dependencia real
- **Stub**: Configuración de un mock para devolver valores específicos
- **Verify**: Comprobar que un método del mock fue llamado
- **Assert**: Verificar que un resultado es el esperado
- **AAA**: Arrange, Act, Assert (patrón para estructurar tests)
- **TDD**: Test-Driven Development (primero test, luego código)
- **Matcher**: Patrón para verificar argumentos (`any()`, `eq()`, etc.)
- **InjectMocks**: Inyecta mocks automáticamente en el objeto a probar

---

## Ejercicios Adicionales

1. **Añadir test**: Probar `obtenEventoPorNombre()` cuando existe y cuando no existe
2. **Añadir test**: Probar que `actualizarEvento()` no actualiza campos nulos
3. **Refactorizar**: Extraer la creación de eventos de prueba a métodos helper
4. **Investigar**: Cómo usar `@MockBean` en tests de integración con Spring Boot

---

**¡Éxito con los tests!** 🚀

## Tabla Resumen de Sintaxis Común

| Categoría      | Sintaxis                      | Ejemplo                                             |
| -------------- | ----------------------------- | --------------------------------------------------- |
| Stubbing       | `when(...).thenReturn()`      | `when(repo.findById(1)).thenReturn(Optional.of(e))` |
| Excepciones    | `thenThrow()`                 | `when(repo.save(any())).thenThrow(new X())`         |
| Void           | `doNothing().when(...)`       | `doNothing().when(repo).deleteById(1)`              |
| Verificación   | `verify(repo).método()`       | `verify(repo).save(e)`                              |
| Captura        | `ArgumentCaptor`              | `captor.getValue()`                                 |
| AssertJ básico | `assertThat(x).isEqualTo(y)`  | —                                                   |
| AssertJ listas | `.hasSize()`, `.contains()`   | —                                                   |
| Excepciones    | `assertThatExceptionOfType()` | —                                                   |
