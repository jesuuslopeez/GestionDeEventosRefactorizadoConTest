package daw2a.gestioneventos.repo;

import daw2a.gestioneventos.dominio.Organizador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrganizadorRepo extends JpaRepository<Organizador,Long> {
    List<Organizador> findByNombreContainingIgnoreCase(String nombre);
}
