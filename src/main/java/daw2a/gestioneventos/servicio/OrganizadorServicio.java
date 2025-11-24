package daw2a.gestioneventos.servicio;

import daw2a.gestioneventos.dominio.Organizador;
import daw2a.gestioneventos.repo.OrganizadorRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizadorServicio {
    private final OrganizadorRepo organizadorRepo;

    public OrganizadorServicio(OrganizadorRepo organizadorRepo) {
        this.organizadorRepo = organizadorRepo;
    }

    public List<Organizador> listarOrganizadores(){
        return organizadorRepo.findAll();
    }

    public Organizador obtenerPorId(Long id){
        return organizadorRepo.findById(id).orElse(null);
    }

    public Organizador crearOrganizador(Organizador organizador){
        return organizadorRepo.save(organizador);
    }
}

