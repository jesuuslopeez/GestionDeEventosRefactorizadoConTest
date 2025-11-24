package daw2a.gestioneventos.servicio;

import daw2a.gestioneventos.dominio.Evento;
import daw2a.gestioneventos.dominio.Organizador;
import daw2a.gestioneventos.dto.OrganizadorRequestDTO;
import daw2a.gestioneventos.dto.OrganizadorResponseDTO;
import daw2a.gestioneventos.exception.OrganizadorDuplicadoException;
import daw2a.gestioneventos.exception.OrganizadorNotFoundException;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

class OrganizadorServiceTest {

    @Mock
    private OrganizadorRepo organizadorRepo;

    @InjectMocks
    private OrganizadorServicio organizadorServicio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarOrganizadoresShouldReturnPageOfOrganizadores() {
        Evento evento = Evento.builder().id(1L).nombre("Evento 1").build();
        Organizador o = Organizador.builder()
                .id(1L)
                .nombre("ACME")
                .eventos(List.of(evento))
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Organizador> page = new PageImpl<>(List.of(o), pageable, 1);

        when(organizadorRepo.findAll(pageable)).thenReturn(page);

        Page<OrganizadorResponseDTO> result = organizadorServicio.listarOrganizadores(pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getNombre()).isEqualTo("ACME");
        verify(organizadorRepo).findAll(pageable);
    }

    @Test
    void obtenerPorIdShouldReturnDTOWhenExists() {
        Organizador o = Organizador.builder()
                .id(1L)
                .nombre("ACME")
                .eventos(List.of())
                .build();

        when(organizadorRepo.findById(1L)).thenReturn(Optional.of(o));

        OrganizadorResponseDTO result = organizadorServicio.obtenerPorId(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("ACME");
        verify(organizadorRepo).findById(1L);
    }

    @Test
    void obtenerPorIdShouldThrowWhenNotExists() {
        when(organizadorRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatExceptionOfType(OrganizadorNotFoundException.class)
                .isThrownBy(() -> organizadorServicio.obtenerPorId(99L));

        verify(organizadorRepo).findById(99L);
    }

    @Test
    void crearOrganizadorShouldThrowWhenNombreYaExiste() {
        OrganizadorRequestDTO dto = new OrganizadorRequestDTO("ACME");

        when(organizadorRepo.existsByNombre("ACME")).thenReturn(true);

        assertThatExceptionOfType(OrganizadorDuplicadoException.class)
                .isThrownBy(() -> organizadorServicio.crearOrganizador(dto));

        verify(organizadorRepo).existsByNombre("ACME");
        verify(organizadorRepo, never()).save(any(Organizador.class));
    }

    @Test
    void crearOrganizadorShouldSaveAndReturnDTOWhenValid() {
        OrganizadorRequestDTO dto = new OrganizadorRequestDTO("Nueva Org");

        Organizador guardado = Organizador.builder()
                .id(10L)
                .nombre("Nueva Org")
                .eventos(List.of())
                .build();

        when(organizadorRepo.existsByNombre("Nueva Org")).thenReturn(false);
        when(organizadorRepo.save(any(Organizador.class))).thenReturn(guardado);

        OrganizadorResponseDTO result = organizadorServicio.crearOrganizador(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getNombre()).isEqualTo("Nueva Org");
        verify(organizadorRepo).existsByNombre("Nueva Org");
        verify(organizadorRepo).save(any(Organizador.class));
    }
}
