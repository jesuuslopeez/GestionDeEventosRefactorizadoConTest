package daw2a.gestioneventos.web;

import daw2a.gestioneventos.dto.ParticipanteRequestDTO;
import daw2a.gestioneventos.dto.ParticipanteResponseDTO;
import daw2a.gestioneventos.servicio.ParticipanteServicio;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
    void listShouldReturnPageOfParticipantes() throws Exception {
        ParticipanteResponseDTO dto = new ParticipanteResponseDTO(1L, "Alice", "alice01", 1L, "Evento Test");
        Page<ParticipanteResponseDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);

        when(participanteServicio.listarParticipantes(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/participantes")
                .param("page", "0")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].nombre").value("Alice"))
                .andExpect(jsonPath("$.content[0].usuario").value("alice01"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getByIdShouldReturnParticipante() throws Exception {
        ParticipanteResponseDTO dto = new ParticipanteResponseDTO(1L, "Alice", "alice01", 1L, "Evento Test");

        when(participanteServicio.obtenerPorId(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/participantes/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Alice"))
                .andExpect(jsonPath("$.usuario").value("alice01"));
    }

    @Test
    void createShouldReturnCreated() throws Exception {
        ParticipanteRequestDTO requestDTO = new ParticipanteRequestDTO("Alice", "alice01", "password123", 1L);
        ParticipanteResponseDTO responseDTO = new ParticipanteResponseDTO(7L, "Alice", "alice01", 1L, "Evento Test");

        when(participanteServicio.crearParticipante(any(ParticipanteRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/participantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.nombre").value("Alice"))
                .andExpect(jsonPath("$.usuario").value("alice01"));
    }
}
