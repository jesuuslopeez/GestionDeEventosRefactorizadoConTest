package daw2a.gestioneventos.repo;

import daw2a.gestioneventos.dominio.Organizador;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrganizadorRepoTest {

    @Autowired
    private OrganizadorRepo organizadorRepo;

    @Test
    void findByNombreContainingIgnoreCaseShouldReturnResults() {
        Organizador o1 = Organizador.builder().nombre("ACME Org").build();
        Organizador o2 = Organizador.builder().nombre("Other").build();
        organizadorRepo.save(o1);
        organizadorRepo.save(o2);

        List<Organizador> found = organizadorRepo.findByNombreContainingIgnoreCase("acme");
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getNombre()).containsIgnoringCase("acme");
    }
}
