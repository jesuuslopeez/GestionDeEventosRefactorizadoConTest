package daw2a.gestioneventos.web;

import daw2a.gestioneventos.dto.OrganizadorRequestDTO;
import daw2a.gestioneventos.dto.OrganizadorResponseDTO;
import daw2a.gestioneventos.servicio.OrganizadorServicio;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrganizadorControlador.class)
class OrganizadorControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrganizadorServicio organizadorServicio;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listShouldReturnPageOfOrganizadores() throws Exception {
        OrganizadorResponseDTO dto = new OrganizadorResponseDTO(1L, "ACME", Collections.emptyList());
        Page<OrganizadorResponseDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);

        when(organizadorServicio.listarOrganizadores(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/organizadores")
                .param("page", "0")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].nombre").value("ACME"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getByIdShouldReturnOrganizador() throws Exception {
        OrganizadorResponseDTO dto = new OrganizadorResponseDTO(1L, "ACME", Collections.emptyList());

        when(organizadorServicio.obtenerPorId(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/organizadores/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("ACME"));
    }

    @Test
    void createShouldReturnCreated() throws Exception {
        OrganizadorRequestDTO requestDTO = new OrganizadorRequestDTO("ACME");
        OrganizadorResponseDTO responseDTO = new OrganizadorResponseDTO(5L, "ACME", Collections.emptyList());

        when(organizadorServicio.crearOrganizador(any(OrganizadorRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/organizadores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.nombre").value("ACME"));
    }
}
