package daw2a.gestioneventos.repo;

import daw2a.gestioneventos.dominio.Participante;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ParticipanteRepoTest {

    @Autowired
    private ParticipanteRepo participanteRepo;

    @Test
    void findByNombreContainingIgnoreCaseShouldReturnResults() {
        Participante p1 = Participante.builder().nombre("Alice").usuario("alice01").contrasenia("secret").build();
        Participante p2 = Participante.builder().nombre("Bob").usuario("bob02").contrasenia("secret").build();
        participanteRepo.save(p1);
        participanteRepo.save(p2);

        List<Participante> found = participanteRepo.findByNombreContainingIgnoreCase("ali");
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getNombre()).containsIgnoringCase("ali");
    }
}
