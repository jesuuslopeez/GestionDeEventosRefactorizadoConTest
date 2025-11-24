package daw2a.gestioneventos.repo;

import daw2a.gestioneventos.dominio.Evento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventoRepo extends JpaRepository<Evento,Long> {
    public Evento findByNombre(String nombre);
    public Optional<Evento> findEventoByDescripcion(String descripcion);
    public List<Evento> findByNombreContainingIgnoreCase(String nombre);
    public boolean existsByNombre(String nombre);

   }
