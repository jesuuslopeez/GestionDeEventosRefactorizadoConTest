package daw2a.gestioneventos.config;

import daw2a.gestioneventos.dominio.Organizador;
import daw2a.gestioneventos.repo.EventoRepo;
import daw2a.gestioneventos.repo.OrganizadorRepo;
import daw2a.gestioneventos.repo.ParticipanteRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class DataInitializer  {
    @Autowired
    private OrganizadorRepo organizadorRepo;
    @Autowired
    private EventoRepo eventoRepo;
    @Autowired
    private ParticipanteRepo participanteRepo;
    
    @PostConstruct
    public void init() throws Exception {
        if(organizadorRepo.count() == 0){
            Organizador org1 = new Organizador();
            org1.setNombre("Organizador 1");
            organizadorRepo.save(org1);
            Organizador org2 = new Organizador();
            org2.setNombre("Organizador 2");
            organizadorRepo.save(org2);
            Organizador org3 = new Organizador();
            org3.setNombre("Organizador 3");
            organizadorRepo.save(org3);
        }

        if(eventoRepo.count() == 0){
            // Crear eventos de ejemplo
            for(int i=1; i<=5; i++){
                daw2a.gestioneventos.dominio.Evento evento = new daw2a.gestioneventos.dominio.Evento();
                evento.setNombre("Evento " + i);
                evento.setDescripcion("Descripcion del Evento " + i);
                evento.setOrganizador(organizadorRepo.findById((long)((i % 3) + 1)).orElse(null));
                eventoRepo.save(evento);
            }
        }

        if(participanteRepo.count() == 0){
            // Crear participantes de ejemplo
            for(int i=1; i<=5; i++){
                daw2a.gestioneventos.dominio.Participante participante = new daw2a.gestioneventos.dominio.Participante();
                participante.setNombre("Participante " + i);
                participante.setUsuario("usuario" + i); // Mínimo 6 caracteres
                participante.setContrasenia("password" + i);
                // Asignar a un evento (round-robin entre los 5 eventos)
                participante.setEvento(eventoRepo.findById((long)((i % 5) + 1)).orElse(null));
                participanteRepo.save(participante);
            }
        }


    }
}
