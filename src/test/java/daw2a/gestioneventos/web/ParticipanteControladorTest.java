package daw2a.gestioneventos.web;

import daw2a.gestioneventos.dominio.Participante;
import daw2a.gestioneventos.servicio.ParticipanteServicio;
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

@WebMvcTest(ParticipanteControlador.class)
class ParticipanteControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParticipanteServicio participanteServicio;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listShouldReturnParticipantes() throws Exception {
        Participante p = Participante.builder().id(1L).nombre("Alice").usuario("alice01").contrasenia("secret").build();
        when(participanteServicio.listarParticipantes()).thenReturn(List.of(p));

        mockMvc.perform(get("/api/v1/participantes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void createShouldReturnCreated() throws Exception {
        Participante in = Participante.builder().nombre("Alice").usuario("alice01").contrasenia("secret").build();
        Participante saved = Participante.builder().id(7L).nombre("Alice").usuario("alice01").contrasenia("secret").build();
        when(participanteServicio.crearParticipante(any(Participante.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/participantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7));
    }
}

