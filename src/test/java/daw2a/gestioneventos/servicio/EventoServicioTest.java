package daw2a.gestioneventos.servicio;

import daw2a.gestioneventos.dominio.Evento;
import daw2a.gestioneventos.dominio.Organizador;
import daw2a.gestioneventos.dto.EventoRequestDTO;
import daw2a.gestioneventos.dto.EventoResponseDTO;
import daw2a.gestioneventos.exception.EventoDuplicadoException;
import daw2a.gestioneventos.exception.EventoNoEncontradoException;
import daw2a.gestioneventos.exception.OrganizadorNotFoundException;
import daw2a.gestioneventos.repo.EventoRepo;
import daw2a.gestioneventos.repo.OrganizadorRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
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
    void listarEventosShouldReturnPageOfEventos() {
        Organizador org = Organizador.builder().id(1L).nombre("Organizador").build();
        Evento e = Evento.builder()
                .id(1L)
                .nombre("Test")
                .descripcion("Desc")
                .fechaInicio(LocalDateTime.now())
                .fechaFin(LocalDateTime.now().plusDays(1))
                .organizador(org)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Evento> page = new PageImpl<>(List.of(e), pageable, 1);

        when(eventoRepo.findAll(pageable)).thenReturn(page);

        Page<EventoResponseDTO> result = eventoServicio.listarEventos(pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getNombre()).isEqualTo("Test");
        verify(eventoRepo).findAll(pageable);
    }

    @Test
    void obtenEventoPorIdShouldReturnDTOWhenExists() {
        Organizador org = Organizador.builder().id(1L).nombre("Organizador").build();
        Evento e = Evento.builder()
                .id(1L)
                .nombre("Test")
                .descripcion("Desc")
                .fechaInicio(LocalDateTime.now())
                .fechaFin(LocalDateTime.now().plusDays(1))
                .organizador(org)
                .build();

        when(eventoRepo.findById(1L)).thenReturn(Optional.of(e));

        EventoResponseDTO result = eventoServicio.obtenEventoPorId(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Test");
        verify(eventoRepo).findById(1L);
    }

    @Test
    void obtenEventoPorIdShouldThrowWhenNotExists() {
        when(eventoRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EventoNoEncontradoException.class)
                .isThrownBy(() -> eventoServicio.obtenEventoPorId(99L));

        verify(eventoRepo).findById(99L);
    }

    @Test
    void obtenEventoPorNombreShouldReturnDTOWhenExists() {
        Organizador org = Organizador.builder().id(1L).nombre("Organizador").build();
        Evento e = Evento.builder()
                .id(1L)
                .nombre("Test")
                .descripcion("Desc")
                .fechaInicio(LocalDateTime.now())
                .fechaFin(LocalDateTime.now().plusDays(1))
                .organizador(org)
                .build();

        when(eventoRepo.findByNombre("Test")).thenReturn(e);

        EventoResponseDTO result = eventoServicio.obtenEventoPorNombre("Test");

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Test");
        verify(eventoRepo).findByNombre("Test");
    }

    @Test
    void obtenEventoPorNombreShouldThrowWhenNotExists() {
        when(eventoRepo.findByNombre("NoExiste")).thenReturn(null);

        assertThatExceptionOfType(EventoNoEncontradoException.class)
                .isThrownBy(() -> eventoServicio.obtenEventoPorNombre("NoExiste"));

        verify(eventoRepo).findByNombre("NoExiste");
    }

    @Test
    void crearEventoShouldThrowWhenNombreYaExiste() {
        EventoRequestDTO dto = new EventoRequestDTO("Duplicado", "Desc",
                LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L);

        when(eventoRepo.existsByNombre("Duplicado")).thenReturn(true);

        assertThatExceptionOfType(EventoDuplicadoException.class)
                .isThrownBy(() -> eventoServicio.crearEvento(dto));

        verify(eventoRepo).existsByNombre("Duplicado");
        verify(eventoRepo, never()).save(any(Evento.class));
    }

    @Test
    void crearEventoShouldThrowWhenOrganizadorNotExists() {
        EventoRequestDTO dto = new EventoRequestDTO("Nuevo", "Desc",
                LocalDateTime.now(), LocalDateTime.now().plusDays(1), 99L);

        when(eventoRepo.existsByNombre("Nuevo")).thenReturn(false);
        when(organizadorRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(OrganizadorNotFoundException.class)
                .isThrownBy(() -> eventoServicio.crearEvento(dto));

        verify(eventoRepo).existsByNombre("Nuevo");
        verify(organizadorRepo).findById(99L);
        verify(eventoRepo, never()).save(any(Evento.class));
    }

    @Test
    void crearEventoShouldSaveAndReturnDTOWhenValid() {
        Organizador org = Organizador.builder().id(1L).nombre("Organizador").build();
        EventoRequestDTO dto = new EventoRequestDTO("Nuevo", "Desc",
                LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L);

        Evento guardado = Evento.builder()
                .id(10L)
                .nombre("Nuevo")
                .descripcion("Desc")
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .organizador(org)
                .build();

        when(eventoRepo.existsByNombre("Nuevo")).thenReturn(false);
        when(organizadorRepo.findById(1L)).thenReturn(Optional.of(org));
        when(eventoRepo.save(any(Evento.class))).thenReturn(guardado);

        EventoResponseDTO result = eventoServicio.crearEvento(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getNombre()).isEqualTo("Nuevo");
        verify(eventoRepo).existsByNombre("Nuevo");
        verify(organizadorRepo).findById(1L);
        verify(eventoRepo).save(any(Evento.class));
    }

    @Test
    void actualizarEventoShouldThrowWhenEventoNoExiste() {
        EventoRequestDTO dto = new EventoRequestDTO("Actualizado", "Desc",
                LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L);

        when(eventoRepo.findById(1L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EventoNoEncontradoException.class)
                .isThrownBy(() -> eventoServicio.actualizarEvento(1L, dto));

        verify(eventoRepo).findById(1L);
        verify(eventoRepo, never()).save(any(Evento.class));
    }

    @Test
    void actualizarEventoShouldUpdateAndReturnDTO() {
        Organizador orgViejo = Organizador.builder().id(1L).nombre("Org1").build();
        Organizador orgNuevo = Organizador.builder().id(2L).nombre("Org2").build();

        Evento existente = Evento.builder()
                .id(1L)
                .nombre("Viejo")
                .descripcion("DescVieja")
                .fechaInicio(LocalDateTime.now())
                .fechaFin(LocalDateTime.now().plusDays(1))
                .organizador(orgViejo)
                .build();

        EventoRequestDTO dto = new EventoRequestDTO("Nuevo", "DescNueva",
                LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3), 2L);

        when(eventoRepo.findById(1L)).thenReturn(Optional.of(existente));
        when(organizadorRepo.findById(2L)).thenReturn(Optional.of(orgNuevo));
        when(eventoRepo.save(any(Evento.class))).thenReturn(existente);

        EventoResponseDTO result = eventoServicio.actualizarEvento(1L, dto);

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Nuevo");
        verify(eventoRepo).findById(1L);
        verify(organizadorRepo).findById(2L);
        verify(eventoRepo).save(existente);
    }

    @Test
    void eliminarEventoShouldThrowWhenNoExiste() {
        when(eventoRepo.findById(1L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EventoNoEncontradoException.class)
                .isThrownBy(() -> eventoServicio.eliminarEvento(1L));

        verify(eventoRepo).findById(1L);
        verify(eventoRepo, never()).delete(any(Evento.class));
    }

    @Test
    void eliminarEventoShouldDeleteWhenExiste() {
        Evento existente = Evento.builder()
                .id(1L)
                .nombre("Test")
                .descripcion("Desc")
                .build();

        when(eventoRepo.findById(1L)).thenReturn(Optional.of(existente));

        eventoServicio.eliminarEvento(1L);

        verify(eventoRepo).findById(1L);
        verify(eventoRepo).delete(existente);
    }
}
