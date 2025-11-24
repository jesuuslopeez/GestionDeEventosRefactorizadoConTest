# Revisión de las entidades del paquete `dominio`

Propósito: documento didáctico para aula con el análisis de las entidades (`Evento`, `Organizador`, `Participante`, `TipoEvento`), problemas detectados, recomendaciones y ejercicios prácticos.

Plan breve
- Leer las entidades actuales y resumir su estado.
- Señalar riesgos y malas prácticas.
- Proponer cambios concretos (pequeños fragmentos de código y motivos).
- Proponer ejercicios para trabajar en clase.

Checklist (qué incluye este documento)
- [x] Resumen y observaciones por clase
- [x] Problemas y riesgos principales
- [x] Recomendaciones concretas (snippets)
- [x] Casos límite y pruebas a realizar
- [x] Ejercicios para alumnos

---

## Resumen general
Las cuatro clases del paquete `daw2a.gestioneventos.dominio` modelan correctamente la idea básica: `Evento` tiene un `Organizador`, varios `Participante` y un `TipoEvento` (enum). Se usa JPA (`jakarta.persistence`) y Lombok para reducir boilerplate.

Sin embargo hay varias mejoras importantes a nivel de persistencia y seguridad que conviene aplicar antes de usar estas entidades en producción o exponerlas por REST.

---

## Revisión por clase

### `Evento`
Observaciones:
- `id` usa `@GeneratedValue(strategy = GenerationType.SEQUENCE)` (válido).
- `organizador` es `@ManyToOne(fetch = FetchType.LAZY)` sin `@JoinColumn` explícito (JPA crea la columna por defecto).
- `tipo` es un `enum` pero no se anota con `@Enumerated` (se persistirá por ordinal).
- `participantes` está anotado con `@OneToMany` sin `mappedBy` ni cascade, lo que provoca una relación no sincronizada y/o una tabla intermedia inesperada.
- `fechaInicio` y `fechaFin` usan `java.util.Date` sin `@Temporal`.

Problemas concretos:
- Persistir enum por ordinal puede romper datos si se cambia el enum.
- `OneToMany` sin `mappedBy` no referencia correctamente la relación definida en la entidad `Participante`.
- Uso de `java.util.Date` es obsoleto; conviene usar `java.time`.

Recomendaciones (cambios mínimos):
- Persistir enum por nombre:
  - `@Enumerated(EnumType.STRING)`
- Mapear correctamente la relación con participantes:
  - `@OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)`
- Usar `LocalDateTime` (o `LocalDate`) para fechas, por ejemplo:
  - `private LocalDateTime fechaInicio;`
  - `private LocalDateTime fechaFin;`
  - Asegurarse de que la dependencia del driver/hibernate soporte `java.time` (Hibernate 5+ lo hace).


### `Organizador`
Observaciones:
- `nombre` marcado como `unique = true, nullable = false` — razonable si se desea unicidad.
- `eventos` está `@OneToMany(mappedBy = "organizador")` — correcto como lado inverso.

Recomendaciones:
- Añadir `fetch = FetchType.LAZY` explícito en `eventos` para claridad (por defecto es LAZY, pero explícito mejora lectura).
- Decidir cascades: si eliminar un organizador debe eliminar eventos asociados, poner `cascade = CascadeType.ALL` y `orphanRemoval = true`; si no, no añadir cascade.


### `Participante`
Observaciones:
- Campos `nombre`, `usuario`, `contrasenia` sin validaciones ni protección de contraseñas.
- `evento` es `@ManyToOne` con `@JoinColumn(nullable = false, name = "evento_id")` — correcto.

Problemas:
- `contrasenia` está en claro dentro de la entidad: riesgo de seguridad.
- `usuario` no tiene `unique`, por lo que pueden existir duplicados.

Recomendaciones:
- No guardar contraseñas en claro. Aplicar hashing (BCrypt) en el servicio antes de persistir. En la entidad marcar el campo con `@JsonIgnore` si se usa Jackson para no exponerlo.
- Si los usuarios deben ser únicos, usar `@Column(unique = true, nullable = false)`.
- Usar `@ManyToOne(fetch = FetchType.LAZY)` explícito.
- Añadir anotaciones de validación (`@NotBlank`, `@Size`) si se integra con Jakarta Bean Validation.


### `TipoEvento` (enum)
- Está bien.
- Recomendación: persistir como `STRING` para seguridad ante cambios futuros.

---

## Consideraciones transversales
- Lombok y JPA: usar `@NoArgsConstructor(access = AccessLevel.PROTECTED)` puede ser más seguro. Evitar `@EqualsAndHashCode` que incluya colecciones.
- Serialización JSON: al exponer entidades, evitar bucles con `@JsonManagedReference` / `@JsonBackReference` o `@JsonIgnore` en los lados adecuados.
- Validación: usar Jakarta Validation en campos que lo requieran.
- Id generators: si se quiere control fino del `Sequence`, definir `@SequenceGenerator`.

---

## Fragmentos recomendados (para copiar en clase)

1) `Evento.tipo` como string:

```java
// dentro de Evento
@Enumerated(EnumType.STRING)
@Column(nullable = false)
private TipoEvento tipo = TipoEvento.CONGRESO;
```

2) `Evento.participantes` mapeado correctamente:

```java
@OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
private List<Participante> participantes = new ArrayList<>();
```

3) `Participante` seguridad y mapeo:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(nullable = false, name = "evento_id")
@JsonBackReference // evita bucle con Jackson
private Evento evento;

@Column(nullable = false, unique = true)
private String usuario;

@JsonIgnore
private String contrasenia; // almacenar el hash, no la contraseña en claro
```

4) Fechas modernas:

```java
private LocalDateTime fechaInicio;
private LocalDateTime fechaFin;
```

---

## Casos límite y pruebas que hacer en clase
- Persistir un `Evento` con 0, 1 y muchos `Participante` y comprobar que la relación se guarda correctamente.
- Eliminar un `Evento` y comprobar si los participantes se eliminan (si se puso `orphanRemoval` y cascade).
- Cambiar el orden/valores de `TipoEvento` y demostrar por qué `EnumType.STRING` evita roturas.
- Registrar un `Participante` y verificar que la contraseña se guarda como hash y no en claro.
- Serializar `Evento` a JSON y verificar que no haya bucles ni exposición de la contraseña.

---

## Ejercicios sugeridos para los alumnos
1. Corregir `Evento` para usar `@Enumerated(EnumType.STRING)` y explicar el motivo.
2. Corregir la relación `Evento` <-> `Participante` usando `mappedBy` y explicar la diferencia entre usar `mappedBy` y no usarlo.
3. Cambiar `Date` por `LocalDateTime` y actualizar el repositorio/serialización.
4. Implementar un servicio sencillo que registre participantes y guarde la contraseña con BCrypt; escribir test unitarios que verifiquen que el hash no coincide con la contraseña en claro.
5. Añadir validaciones (`@NotBlank`, `@Size`) y escribir tests que verifiquen que las validaciones fallan cuando corresponde.

---

## Siguientes pasos (opciones)
- Puedo aplicar automáticamente los cambios mínimos en el código (enum as STRING, mappedBy en `Evento.participantes`, cascades y uso de `LocalDateTime`), y después ejecutar una comprobación de errores/compilación.
- O dejar el documento tal cual para que lo utilices en clase y obligar a los alumnos a implementar las correcciones como ejercicios.

Dime qué prefieres: aplicar los cambios automáticamente y validar la compilación, o sólo dejar el documento listo para la clase.

---

Documento generado el: 2025-11-05

