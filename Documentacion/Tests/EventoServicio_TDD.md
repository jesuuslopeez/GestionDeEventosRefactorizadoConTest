# TDD sobre `EventoServicio.actualizarEvento`

Este documento describe los cambios que hay que hacer en `EventoServicio` para que el test
`actualizarEventoShouldActualizarCamposBasicosYOrganizadorYParticipantes()` de `EventoServicioTest`
funcione. La idea es que puedas ir aplicándolos en vivo con el alumnado.

## Situación inicial

Método actual en `EventoServicio`:

```java
public Evento actualizarEvento(Long id, Evento evento) {
    Optional<Evento> eventoExistente = eventoRepo.findById(id);

    if (eventoExistente.isEmpty()) {
        return null;
    }

    Evento eventoActualizado = eventoExistente.get();

    // Actualizar campos básicos
    actualizaCamposBasicos(evento, eventoActualizado);

    // Actualizar organizador
    if (evento.getOrganizador() != null) {
        Optional<Organizador> organizadorOpt = organizadorRepo.findById(evento.getOrganizador().getId());
        organizadorOpt.ifPresent(eventoActualizado::setOrganizador);
    }

    // Actualizar participantes
    // En lugar de reemplazar la lista
    if (evento.getParticipantes() != null && !evento.getParticipantes().isEmpty()) {
        List<Participante> participantesActuales = eventoActualizado.getParticipantes();
        if (participantesActuales == null) {
            participantesActuales = new ArrayList<>();
        }
        // Añadir solo los participantes que no estén ya en la lista
        for (Participante participante : evento.getParticipantes()) {
            if (!participantesActuales.contains(participante)) {
                participantesActuales.add(participante);
            }
        }
        eventoActualizado.setParticipantes(participantesActuales);
    }

    return eventoRepo.save(eventoActualizado);
}
```

## Objetivos del test

El test (cuando se implemente) comprobará que:

1. Si el evento **no existe**, se devuelve `null` y **no** se guarda nada. *(ya cubierto por otro test)*
2. Si el evento existe:
   - Se actualizan:
     - `nombre`, `descripcion`, `tipo`, `fechaInicio`, `fechaFin` **solo** si vienen informados en el objeto `evento`.
   - Se actualiza el **organizador** buscando por `id` en `organizadorRepo`.
   - Se **añaden** nuevos participantes a la lista actual sin perder los existentes.

Además, queremos evitar problemas al modificar la lista de participantes, en especial cuando
`eventoActualizado.getParticipantes()` devuelva una lista inmutable.

## Paso 1: asegurar lista mutable de participantes

En el código actual, `participantesActuales` puede ser la lista que ya trae el evento desde JPA
(o desde el builder del test). Si esa lista es inmutable (por ejemplo, creada con `List.of(...)`),
la llamada a `add()` lanzará `UnsupportedOperationException`.

**Cambio recomendado:** copiar siempre a una `ArrayList` antes de modificar:

```java
List<Participante> participantesActuales = eventoActualizado.getParticipantes();
if (participantesActuales == null) {
    participantesActuales = new ArrayList<>();
} else {
    participantesActuales = new ArrayList<>(participantesActuales);
}
```

Después, se trabaja y se hace `setParticipantes(participantesActuales)` como hasta ahora.

## Paso 2: añadir sólo nuevos participantes

La intención actual ya es añadir sólo los que no están (`contains`). Esto se puede mantener igual,
pero conviene comentar con el alumnado:

- `contains` usa `equals`/`hashCode` de `Participante`. Si no están sobreescritos, la comparación
  será por referencia.
- Para el test, normalmente bastará con comparar objetos concretos que el propio test crea.

Código tal cual (con la lista mutable del paso 1):

```java
for (Participante participante : evento.getParticipantes()) {
    if (!participantesActuales.contains(participante)) {
        participantesActuales.add(participante);
    }
}
```

## Paso 3: asegurar actualización de campos básicos

La función `actualizaCamposBasicos` ya usa `Optional.ofNullable(...).ifPresent(...)`, lo cual
encaja con el test: sólo se actualizan los campos que vienen informados en el `evento` de entrada.

Puedes reforzar con comentarios para el alumnado:

```java
private void actualizaCamposBasicos(Evento evento, Evento eventoActualizado) {
    // Sólo actualizamos si el campo viene con un valor no nulo en "evento".
    Optional.ofNullable(evento.getNombre()).ifPresent(eventoActualizado::setNombre);
    Optional.ofNullable(evento.getDescripcion()).ifPresent(eventoActualizado::setDescripcion);
    Optional.ofNullable(evento.getFechaInicio()).ifPresent(eventoActualizado::setFechaInicio);
    Optional.ofNullable(evento.getFechaFin()).ifPresent(eventoActualizado::setFechaFin);
    Optional.ofNullable(evento.getTipo()).ifPresent(eventoActualizado::setTipo);
}
```

## Paso 4: actualización del organizador

La lógica existente ya cumple lo que se pide en el test:

```java
if (evento.getOrganizador() != null) {
    Optional<Organizador> organizadorOpt = organizadorRepo.findById(evento.getOrganizador().getId());
    organizadorOpt.ifPresent(eventoActualizado::setOrganizador);
}
```

*Posibles extensiones para TDD avanzado:*

- ¿Qué hacer si el organizador no existe? ¿Lanzar una excepción? ¿Dejar el organizador sin cambios?
- Añadir un test que cubra ese comportamiento.

## Resumen de cambios mínimos para pasar el test

1. En `actualizarEvento`, justo antes del bucle de participantes, cambiar el bloque que inicializa
   `participantesActuales` por una versión que **copie a `ArrayList`**:

   ```java
   List<Participante> participantesActuales = eventoActualizado.getParticipantes();
   if (participantesActuales == null) {
       participantesActuales = new ArrayList<>();
   } else {
       participantesActuales = new ArrayList<>(participantesActuales);
   }
   ```

2. Mantener el bucle que añade nuevos participantes si no están en la lista.

3. Asegurarse de que, al final, se llama a `eventoActualizado.setParticipantes(participantesActuales);`.

Con estos cambios implementados, el test
`actualizarEventoShouldActualizarCamposBasicosYOrganizadorYParticipantes()` debería pasar,
siempre que el propio test cree correctamente:

- Un `Evento` existente con una lista de participantes (posiblemente inmutable).
- Un objeto `cambios` con nuevos datos y participantes.
- Mocks apropiados para `eventoRepo` y `organizadorRepo`.

La idea es que este documento sea la guía para ir haciendo esos cambios en directo con los alumnos
mientras hacen TDD.

