package daw2a.gestioneventos.servicio;

import daw2a.gestioneventos.dominio.Evento;
import daw2a.gestioneventos.exception.EventoDuplicadoException;
import daw2a.gestioneventos.exception.EventoNoEncontradoException;
import daw2a.gestioneventos.repo.EventoRepo;
import daw2a.gestioneventos.repo.OrganizadorRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

class EventoServicioTest {

    @Mock
    private EventoRepo eventoRepo;

    @Mock
    private OrganizadorRepo organizadorRepo;

    @InjectMocks
    private EventoServicio eventoServicio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarEventosShouldDelegateToRepo() {
        Evento e = Evento.builder().id(1L).nombre("Test").descripcion("Desc").build();
        when(eventoRepo.findAll()).thenReturn(List.of(e));

        Page<Evento> result = eventoServicio.listarEventos();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(eventoRepo).findAll();
        verifyNoInteractions(organizadorRepo);
    }

    @Test
    void listarEventosShouldThrowWhenEmpty() {
        when(eventoRepo.findAll()).thenReturn(List.of());

        assertThatExceptionOfType(EventoNoEncontradoException.class)
                .isThrownBy(() -> eventoServicio.listarEventos());

        verify(eventoRepo).findAll();
        verifyNoInteractions(organizadorRepo);
    }

    @Test
    void obtenEventoPorIdShouldReturnEventoWhenExists() {
        Evento e = Evento.builder().id(1L).nombre("Test").descripcion("Desc").build();
        when(eventoRepo.findById(1L)).thenReturn(Optional.of(e));

        Evento found = eventoServicio.obtenEventoPorId(1L);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        verify(eventoRepo).findById(1L);
        verifyNoInteractions(organizadorRepo);
    }

    @Test
    void obtenEventoPorIdShouldThrowWhenNotExists() {
        when(eventoRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EventoNoEncontradoException.class)
                .isThrownBy(() -> eventoServicio.obtenEventoPorId(99L));

        verify(eventoRepo).findById(99L);
        verifyNoInteractions(organizadorRepo);
    }

    @Test
    void crearEventoShouldThrowWhenNombreYaExiste() {
        Evento existente = Evento.builder().id(1L).nombre("Duplicado").descripcion("Desc").build();
        Evento nuevo = Evento.builder().nombre("Duplicado").descripcion("X").build();
        when(eventoRepo.findByNombre("Duplicado")).thenReturn(existente);

        assertThatExceptionOfType(EventoDuplicadoException.class)
                .isThrownBy(() -> eventoServicio.crearEvento(nuevo));

        verify(eventoRepo).findByNombre("Duplicado");
        verify(eventoRepo, never()).save(any(Evento.class));
        verifyNoInteractions(organizadorRepo);
    }

    @Test
    void crearEventoShouldSaveWhenNombreNoExiste() {
        Evento nuevo = Evento.builder().nombre("Nuevo").descripcion("X").build();
        Evento guardado = Evento.builder().id(10L).nombre("Nuevo").descripcion("X").build();
        when(eventoRepo.findByNombre("Nuevo")).thenReturn(null);
        when(eventoRepo.save(any(Evento.class))).thenReturn(guardado);

        Evento creado = eventoServicio.crearEvento(nuevo);

        assertThat(creado).isNotNull();
        assertThat(creado.getId()).isEqualTo(10L);
        verify(eventoRepo).findByNombre("Nuevo");
        verify(eventoRepo).save(any(Evento.class));
        verifyNoInteractions(organizadorRepo);
    }

    @Test
    void actualizarEventoShouldThrowWhenEventoNoExiste() {
        when(eventoRepo.findById(1L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EventoNoEncontradoException.class)
                .isThrownBy(() -> eventoServicio.actualizarEvento(1L, new Evento()));

        verify(eventoRepo).findById(1L);
        verify(eventoRepo, never()).save(any(Evento.class));
        verifyNoInteractions(organizadorRepo);
    }

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

    @Test
    void eliminarEventoShouldThrowWhenNoExiste() {
        when(eventoRepo.findById(1L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EventoNoEncontradoException.class)
                .isThrownBy(() -> eventoServicio.eliminarEvento(1L));

        verify(eventoRepo).findById(1L);
        verify(eventoRepo, never()).delete(any(Evento.class));
        verifyNoInteractions(organizadorRepo);
    }

    @Test
    void eliminarEventoShouldDeleteWhenExiste() {
        Evento existente = Evento.builder().id(1L).nombre("Test").descripcion("Desc").build();
        when(eventoRepo.findById(1L)).thenReturn(Optional.of(existente));

        eventoServicio.eliminarEvento(1L);

        verify(eventoRepo).findById(1L);
        verify(eventoRepo).delete(existente);
        verifyNoInteractions(organizadorRepo);
    }
}
