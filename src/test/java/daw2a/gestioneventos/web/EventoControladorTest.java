package daw2a.gestioneventos.web;

import daw2a.gestioneventos.dto.EventoRequestDTO;
import daw2a.gestioneventos.dto.EventoResponseDTO;
import daw2a.gestioneventos.servicio.EventoServicio;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventoControlador.class)
class EventoControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventoServicio eventoServicio;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listShouldReturnPageOfEvents() throws Exception {
        EventoResponseDTO dto = new EventoResponseDTO(1L, "Prueba", "2024-12-01T10:00:00", "2024-12-02T10:00:00", 1L);
        Page<EventoResponseDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);

        when(eventoServicio.listarEventos(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/eventos")
                .param("page", "0")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].nombre").value("Prueba"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getByIdShouldReturnEvent() throws Exception {
        EventoResponseDTO dto = new EventoResponseDTO(1L, "Prueba", "2024-12-01T10:00:00", "2024-12-02T10:00:00", 1L);

        when(eventoServicio.obtenEventoPorId(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/eventos/id/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Prueba"));
    }

    @Test
    void getByNombreShouldReturnEvent() throws Exception {
        EventoResponseDTO dto = new EventoResponseDTO(1L, "Prueba", "2024-12-01T10:00:00", "2024-12-02T10:00:00", 1L);

        when(eventoServicio.obtenEventoPorNombre("Prueba")).thenReturn(dto);

        mockMvc.perform(get("/api/v1/eventos/nombre/Prueba")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Prueba"));
    }

    @Test
    void createShouldReturnCreated() throws Exception {
        EventoRequestDTO requestDTO = new EventoRequestDTO("Nuevo", "Descripción",
                LocalDateTime.of(2024, 12, 1, 10, 0),
                LocalDateTime.of(2024, 12, 2, 10, 0),
                1L);

        EventoResponseDTO responseDTO = new EventoResponseDTO(10L, "Nuevo", "2024-12-01T10:00:00", "2024-12-02T10:00:00", 1L);

        when(eventoServicio.crearEvento(any(EventoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/eventos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nombre").value("Nuevo"));
    }

    @Test
    void updateShouldReturnUpdatedEvent() throws Exception {
        EventoRequestDTO requestDTO = new EventoRequestDTO("Actualizado", "Nueva Descripción",
                LocalDateTime.of(2024, 12, 1, 10, 0),
                LocalDateTime.of(2024, 12, 2, 10, 0),
                1L);

        EventoResponseDTO responseDTO = new EventoResponseDTO(1L, "Actualizado", "2024-12-01T10:00:00", "2024-12-02T10:00:00", 1L);

        when(eventoServicio.actualizarEvento(eq(1L), any(EventoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/eventos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Actualizado"));
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/eventos/1"))
                .andExpect(status().isNoContent());
    }
}
