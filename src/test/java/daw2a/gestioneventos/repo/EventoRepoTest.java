package daw2a.gestioneventos.repo;

import daw2a.gestioneventos.dominio.Evento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EventoRepoTest {

    @Autowired
    private EventoRepo eventoRepo;

    @Test
    void findByNombreContainingIgnoreCaseShouldReturnResults() {
        Evento e1 = Evento.builder().nombre("Java Conference").descripcion("x").build();
        Evento e2 = Evento.builder().nombre("Spring Workshop").descripcion("y").build();
        eventoRepo.save(e1);
        eventoRepo.save(e2);

        List<Evento> found = eventoRepo.findByNombreContainingIgnoreCase("java");
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getNombre()).containsIgnoringCase("java");
    }
}

