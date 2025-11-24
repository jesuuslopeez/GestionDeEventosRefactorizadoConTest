package daw2a.gestioneventos.web;

import daw2a.gestioneventos.dominio.Organizador;
import daw2a.gestioneventos.servicio.OrganizadorServicio;
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

@WebMvcTest(OrganizadorControlador.class)
class OrganizadorControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrganizadorServicio organizadorServicio;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listShouldReturnOrganizadores() throws Exception {
        Organizador o = Organizador.builder().id(1L).nombre("ACME").build();
        when(organizadorServicio.listarOrganizadores()).thenReturn(List.of(o));

        mockMvc.perform(get("/api/v1/organizadores").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void createShouldReturnCreated() throws Exception {
        Organizador in = Organizador.builder().nombre("ACME").build();
        Organizador saved = Organizador.builder().id(5L).nombre("ACME").build();
        when(organizadorServicio.crearOrganizador(any(Organizador.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/organizadores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));
    }
}
