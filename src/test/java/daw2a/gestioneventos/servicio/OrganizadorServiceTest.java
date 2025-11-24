package daw2a.gestioneventos.servicio;

import daw2a.gestioneventos.dominio.Organizador;
import daw2a.gestioneventos.repo.OrganizadorRepo;
import daw2a.gestioneventos.servicio.OrganizadorServicio;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class OrganizadorServiceTest {

    @Mock
    private OrganizadorRepo organizadorRepo;

    @InjectMocks
    private OrganizadorServicio organizadorServicio;

    public OrganizadorServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void obtenerPorIdShouldReturnOrganizadorWhenExists() {
        Organizador o = Organizador.builder().id(1L).nombre("ACME").build();
        when(organizadorRepo.findById(1L)).thenReturn(Optional.of(o));

        Organizador found = organizadorServicio.obtenerPorId(1L);
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
    }
}
