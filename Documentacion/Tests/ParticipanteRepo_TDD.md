# Ejercicio TDD con `ParticipanteRepoTest`

Este ejercicio parte del test de repositorio `ParticipanteRepoTest` que actualmente falla
por una violación de integridad referencial. Es un buen ejemplo para trabajar con el
alumnado el impacto de las relaciones JPA y las constraints en base de datos.

## 1. Código actual relevante

### Entidad `Participante`

```java
@Entity
public class Participante {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "sequence_participante", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    @Size(min=6,max=15)
    private String usuario;

    @Column(nullable = false)
    @JsonIgnore
    private String contrasenia;

    @ManyToOne
    @JoinColumn(nullable = false, name= "evento_id")
    @JsonBackReference
    private Evento evento;
}
```

### Test `ParticipanteRepoTest`

```java
@DataJpaTest
class ParticipanteRepoTest {

    @Autowired
    private ParticipanteRepo participanteRepo;

    @Test
    void findByNombreContainingIgnoreCaseShouldReturnResults() {
        Participante p1 = Participante.builder()
                .nombre("Alice").usuario("alice01").contrasenia("secret").build();
        Participante p2 = Participante.builder()
                .nombre("Bob").usuario("bob02").contrasenia("secret").build();
        participanteRepo.save(p1);
        participanteRepo.save(p2);

        List<Participante> found = participanteRepo.findByNombreContainingIgnoreCase("ali");
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getNombre()).containsIgnoringCase("ali");
    }
}
```

## 2. El problema actual

Al ejecutar este test aparece una excepción de integridad:

- `DataIntegrityViolationException` → `ConstraintViolationException` → `JdbcSQLIntegrityConstraintViolationException`.

Motivo:

- La columna `evento_id` está marcada como `nullable = false` en el `@JoinColumn`.
- En el test estamos creando `Participante` **sin** asignar ningún `Evento`.
- Cuando JPA intenta persistir el `Participante`, la base de datos rechaza la fila
  por no cumplir la constraint `NOT NULL` de `evento_id`.

## 3. Objetivo del ejercicio

Corregir el test (o el modelo, según se decida) para que:

- La constraint de integridad tenga sentido en el modelo.
- El test de búsqueda por nombre pueda ejecutarse sin violar las constraints.

## 4. Enfoque recomendado para clase

### Paso 1: Analizar la relación

Preguntas para el alumnado:

1. ¿Tiene sentido que un `Participante` **siempre** esté asociado a un `Evento`?
2. ¿En qué casos permitiríamos un participante sin evento?
3. ¿La constraint `nullable = false` en `evento_id` refleja correctamente las reglas de negocio?

Según la respuesta, se pueden plantear dos caminos.

### Paso 2A: Mantener la constraint y corregir el test

Si decidimos que **todo participante debe tener evento**, entonces el test está mal planteado.

En ese caso, el ejercicio es:

1. **Crear un `Evento` de prueba** en el test:

   ```java
   Evento evento = Evento.builder()
           .nombre("Evento de prueba")
           .descripcion("Desc")
           .build();
   // Persistir el evento si hace falta, o dejar que JPA haga cascade si se configura.
   ```

2. **Asignar ese evento a los participantes** antes de guardar:

   ```java
   p1.setEvento(evento);
   p2.setEvento(evento);
   ```

3. Guardar participantes como hasta ahora y ejecutar el test.

Aspectos a comentar:

- Si no hay `cascade` desde `Evento` a `Participante`, puede ser necesario guardar
  primero el evento con su propio repositorio.
- Esto ayuda a entender cómo se construyen datos de prueba consistentes en tests JPA.

### Paso 2B: Relajar la constraint en el modelo (discusión)

Si el grupo concluye que puede haber participantes sin evento (por ejemplo, usuarios
registrados aún sin asignación), se puede discutir:

- Cambiar `@JoinColumn(nullable = false)` a `nullable = true`.
- Ventajas: más flexibilidad en la creación de participantes.
- Inconvenientes: hay que tener claro qué significa un participante sin evento.

Este camino es más de diseño de dominio que de TDD, pero viene bien para ver cómo
el modelo impone restricciones a los tests.

## 5. Propuesta de solución tipo (sólo test)

Una posible solución centrada sólo en el test (sin tocar el modelo) sería:

```java
@Test
void findByNombreContainingIgnoreCaseShouldReturnResults() {
    // 1. Crear un evento de prueba
    Evento evento = Evento.builder()
            .nombre("Evento de prueba")
            .descripcion("Desc")
            .build();

    // 2. Crear participantes y asociarlos al evento
    Participante p1 = Participante.builder()
            .nombre("Alice").usuario("alice01").contrasenia("secret")
            .evento(evento)
            .build();

    Participante p2 = Participante.builder()
            .nombre("Bob").usuario("bob02").contrasenia("secret")
            .evento(evento)
            .build();

    participanteRepo.save(p1);
    participanteRepo.save(p2);

    // 3. Ejecutar la búsqueda y comprobar resultados
    List<Participante> found = participanteRepo.findByNombreContainingIgnoreCase("ali");

    assertThat(found).isNotEmpty();
    assertThat(found.get(0).getNombre()).containsIgnoringCase("ali");
}
```

Este fragmento puede ser construido poco a poco con los alumnos en clase, a partir
del fallo original, usando TDD:

1. Ejecutar el test original y observar la excepción.
2. Interpretar el mensaje de error y conectar con `nullable = false`.
3. Decidir que hay que crear un evento de prueba.
4. Añadir el evento y asociarlo a los participantes.
5. Volver a ejecutar el test y comprobar que pasa.

## 6. Extensiones posibles

- Añadir un segundo test que verifique que el `Evento` realmente se persiste junto con
  los participantes (dependiendo de si hay cascade o no).
- Cambiar el modelo para explorar qué pasa si `evento_id` se permite nulo y adaptar
  los tests en consecuencia.
- Introducir un `TestEntityManager` para crear los datos de prueba con más control.

La idea es que este documento sea el guión para trabajar este fallo de integridad
paso a paso con el alumnado.
// ...existing code...
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
        // Esqueleto sugerido:
        // 1. Preparar datos
        //    - Crear un Evento existente (builder) con algunos valores iniciales.
        //    - Crear un Organizador y un Participante de ejemplo.
        //    - Crear un objeto "cambios" con los nuevos valores.
        //
        // 2. Configurar mocks
        //    - when(eventoRepo.findById(...)).thenReturn(Optional.of(eventoExistente));
        //    - when(organizadorRepo.findById(...)).thenReturn(Optional.of(organizador));
        //    - when(eventoRepo.save(any(Evento.class))).thenAnswer(invocation -> invocation.getArgument(0));
        //
        // 3. Ejecutar el método a probar
        //    - Evento resultado = eventoServicio.actualizarEvento(id, cambios);
        //
        // 4. Verificar resultados con assertThat(...)
        //    - Comprobar que nombre, descripcion, tipo, fechas y organizador se han actualizado.
        //    - Comprobar que la lista de participantes contiene los nuevos participantes.
    }
// ...existing code...

