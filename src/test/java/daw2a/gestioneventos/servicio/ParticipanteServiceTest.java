package daw2a.gestioneventos.servicio;

import daw2a.gestioneventos.dominio.Participante;
import daw2a.gestioneventos.repo.ParticipanteRepo;
import daw2a.gestioneventos.servicio.ParticipanteServicio;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ParticipanteServiceTest {

    @Mock
    private ParticipanteRepo participanteRepo;

    @InjectMocks
    private ParticipanteServicio participanteServicio;

    public ParticipanteServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void obtenerPorIdShouldReturnParticipanteWhenExists() {
        Participante p = Participante.builder().id(1L).nombre("Alice").usuario("alice01").contrasenia("secret").build();
        when(participanteRepo.findById(1L)).thenReturn(Optional.of(p));

        Participante found = participanteServicio.obtenerPorId(1L);
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
    }
}
