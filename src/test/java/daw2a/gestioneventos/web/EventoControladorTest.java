package daw2a.gestioneventos.web;

import daw2a.gestioneventos.dominio.Evento;
import daw2a.gestioneventos.repo.EventoRepo;
import daw2a.gestioneventos.repo.OrganizadorRepo;
import daw2a.gestioneventos.servicio.EventoServicio;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventoControlador.class)
class EventoControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventoRepo eventoRepo; // se mantiene en caso de que otros beans lo necesiten

    @MockBean
    private OrganizadorRepo organizadorRepo;

    @MockBean
    private EventoServicio eventoServicio; // mock del servicio usado por el controlador

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listShouldReturnEvents() throws Exception {
        Evento e = Evento.builder().id(1L).nombre("Prueba").descripcion("Desc").build();
        when(eventoServicio.listarEventos()).thenReturn(List.of(e));

        mockMvc.perform(get("/api/v1/eventos").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void createShouldReturnCreated() throws Exception {
        // Datos de entrada y salida
        Evento in = Evento.builder().nombre("Nuevo").descripcion("X").build();
        Evento saved = Evento.builder().id(10L).nombre("Nuevo").descripcion("X").build();
        // Configurar el mock del servicio
        // when el servicio cree un evento, devolver el evento "guardado"
        when(eventoServicio.crearEvento(any(Evento.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/eventos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }
}
