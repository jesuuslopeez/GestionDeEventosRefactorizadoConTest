package daw2a.gestioneventos.servicio;

import daw2a.gestioneventos.dominio.Organizador;
import daw2a.gestioneventos.dto.OrganizadorRequestDTO;
import daw2a.gestioneventos.dto.OrganizadorResponseDTO;
import daw2a.gestioneventos.exception.OrganizadorDuplicadoException;
import daw2a.gestioneventos.exception.OrganizadorNotFoundException;
import daw2a.gestioneventos.mapper.OrganizadorMapper;
import daw2a.gestioneventos.repo.OrganizadorRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrganizadorServicio {
    private final OrganizadorRepo organizadorRepo;

    public OrganizadorServicio(OrganizadorRepo organizadorRepo) {
        this.organizadorRepo = organizadorRepo;
    }

    public Page<OrganizadorResponseDTO> listarOrganizadores(Pageable pageable){
        return organizadorRepo.findAll(pageable)
                .map(OrganizadorMapper::toDTO);
    }

    public OrganizadorResponseDTO obtenerPorId(Long id){
        Organizador organizador = organizadorRepo.findById(id)
                .orElseThrow(() -> new OrganizadorNotFoundException(id));
        return OrganizadorMapper.toDTO(organizador);
    }

    public OrganizadorResponseDTO crearOrganizador(OrganizadorRequestDTO dto){
        // Validar que el nombre no exista
        if (organizadorRepo.existsByNombre(dto.getNombre())) {
            throw new OrganizadorDuplicadoException(dto.getNombre());
        }

        Organizador organizador = OrganizadorMapper.toEntity(dto);
        Organizador guardado = organizadorRepo.save(organizador);
        return OrganizadorMapper.toDTO(guardado);
    }
}

