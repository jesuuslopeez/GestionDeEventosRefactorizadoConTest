package daw2a.gestioneventos.servicio;

import daw2a.gestioneventos.dominio.Evento;
import daw2a.gestioneventos.dominio.Participante;
import daw2a.gestioneventos.dto.ParticipanteRequestDTO;
import daw2a.gestioneventos.dto.ParticipanteResponseDTO;
import daw2a.gestioneventos.exception.EventoNoEncontradoException;
import daw2a.gestioneventos.exception.ParticipanteNotFoundException;
import daw2a.gestioneventos.exception.UsuarioYaExisteException;
import daw2a.gestioneventos.repo.EventoRepo;
import daw2a.gestioneventos.repo.ParticipanteRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

class ParticipanteServiceTest {

    @Mock
    private ParticipanteRepo participanteRepo;

    @Mock
    private EventoRepo eventoRepo;

    @InjectMocks
    private ParticipanteServicio participanteServicio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarParticipantesShouldReturnPageOfParticipantes() {
        Evento evento = Evento.builder().id(1L).nombre("Evento Test").build();
        Participante p = Participante.builder()
                .id(1L)
                .nombre("Alice")
                .usuario("alice01")
                .contrasenia("secret")
                .evento(evento)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Participante> page = new PageImpl<>(List.of(p), pageable, 1);

        when(participanteRepo.findAll(pageable)).thenReturn(page);

        Page<ParticipanteResponseDTO> result = participanteServicio.listarParticipantes(pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getNombre()).isEqualTo("Alice");
        assertThat(result.getContent().get(0).getUsuario()).isEqualTo("alice01");
        verify(participanteRepo).findAll(pageable);
    }

    @Test
    void obtenerPorIdShouldReturnDTOWhenExists() {
        Evento evento = Evento.builder().id(1L).nombre("Evento Test").build();
        Participante p = Participante.builder()
                .id(1L)
                .nombre("Alice")
                .usuario("alice01")
                .contrasenia("secret")
                .evento(evento)
                .build();

        when(participanteRepo.findById(1L)).thenReturn(Optional.of(p));

        ParticipanteResponseDTO result = participanteServicio.obtenerPorId(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Alice");
        assertThat(result.getUsuario()).isEqualTo("alice01");
        verify(participanteRepo).findById(1L);
    }

    @Test
    void obtenerPorIdShouldThrowWhenNotExists() {
        when(participanteRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(ParticipanteNotFoundException.class)
                .isThrownBy(() -> participanteServicio.obtenerPorId(99L));

        verify(participanteRepo).findById(99L);
    }

    @Test
    void crearParticipanteShouldThrowWhenUsuarioYaExiste() {
        ParticipanteRequestDTO dto = new ParticipanteRequestDTO("Alice", "alice01", "password", 1L);

        when(participanteRepo.existsByUsuario("alice01")).thenReturn(true);

        assertThatExceptionOfType(UsuarioYaExisteException.class)
                .isThrownBy(() -> participanteServicio.crearParticipante(dto));

        verify(participanteRepo).existsByUsuario("alice01");
        verify(participanteRepo, never()).save(any(Participante.class));
    }

    @Test
    void crearParticipanteShouldThrowWhenEventoNotExists() {
        ParticipanteRequestDTO dto = new ParticipanteRequestDTO("Alice", "alice01", "password", 99L);

        when(participanteRepo.existsByUsuario("alice01")).thenReturn(false);
        when(eventoRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EventoNoEncontradoException.class)
                .isThrownBy(() -> participanteServicio.crearParticipante(dto));

        verify(participanteRepo).existsByUsuario("alice01");
        verify(eventoRepo).findById(99L);
        verify(participanteRepo, never()).save(any(Participante.class));
    }

    @Test
    void crearParticipanteShouldSaveAndReturnDTOWhenValid() {
        Evento evento = Evento.builder().id(1L).nombre("Evento Test").build();
        ParticipanteRequestDTO dto = new ParticipanteRequestDTO("Alice", "alice01", "password", 1L);

        Participante guardado = Participante.builder()
                .id(10L)
                .nombre("Alice")
                .usuario("alice01")
                .contrasenia("password")
                .evento(evento)
                .build();

        when(participanteRepo.existsByUsuario("alice01")).thenReturn(false);
        when(eventoRepo.findById(1L)).thenReturn(Optional.of(evento));
        when(participanteRepo.save(any(Participante.class))).thenReturn(guardado);

        ParticipanteResponseDTO result = participanteServicio.crearParticipante(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getNombre()).isEqualTo("Alice");
        assertThat(result.getUsuario()).isEqualTo("alice01");
        verify(participanteRepo).existsByUsuario("alice01");
        verify(eventoRepo).findById(1L);
        verify(participanteRepo).save(any(Participante.class));
    }
}
