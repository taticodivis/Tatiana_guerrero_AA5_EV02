package com.codivis.crm;

import java.nio.file.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Comprueba las cinco operaciones protegidas y la creación e invalidación de sesión. */
@SpringBootTest
@AutoConfigureMockMvc
class AccesoClientesTest {
    static final Path datos = carpetaTemporal();
    @Autowired MockMvc mvc;
    static Path carpetaTemporal() {
        try { return Files.createTempDirectory("codivis-sesion-test-"); }
        catch (java.io.IOException ex) { throw new java.io.UncheckedIOException(ex); }
    }
    @DynamicPropertySource
    static void configuracion(DynamicPropertyRegistry registry) {
        registry.add("crm.data.dir", datos::toString);
    }
    @AfterAll
    static void limpiar() throws Exception {
        try (var files = Files.list(datos)) {
            for (Path file : files.toList()) Files.delete(file);
        }
        Files.delete(datos);
    }
    @Test
    void todasLasOperacionesExigenSesion() throws Exception {
        var solicitudes = java.util.List.of(get("/api/clientes"), get("/api/clientes/1"),
                post("/api/clientes").contentType("application/json").content("{}"),
                put("/api/clientes/1").contentType("application/json").content("{}"),
                delete("/api/clientes/1"));
        for (var solicitud : solicitudes) {
            mvc.perform(solicitud).andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.codigo").value("AUTENTICACION_REQUERIDA"));
        }
    }
    @Test
    void loginRenuevaSesionYLogoutRevocaAcceso() throws Exception {
        mvc.perform(post("/api/registro").contentType("application/x-www-form-urlencoded")
                .param("usuario", "sesion_prueba").param("clave", "PruebaSegura2026!")
                .param("confirmacion", "PruebaSegura2026!")).andExpect(status().isCreated());
        MockHttpSession previa = new MockHttpSession();
        var login = mvc.perform(post("/api/login").session(previa)
                .contentType("application/x-www-form-urlencoded")
                .param("usuario", "sesion_prueba").param("clave", "PruebaSegura2026!"))
                .andExpect(status().isOk()).andReturn();
        MockHttpSession autenticada = (MockHttpSession) login.getRequest().getSession(false);
        assertNotNull(autenticada);
        assertTrue(previa.isInvalid());
        assertNotEquals(previa.getId(), autenticada.getId());
        mvc.perform(get("/api/clientes").session(autenticada)).andExpect(status().isOk());
        mvc.perform(get("/api/clientes").session(new MockHttpSession())).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/logout").session(autenticada)).andExpect(status().isOk());
        assertTrue(autenticada.isInvalid());
        mvc.perform(get("/api/clientes")).andExpect(status().isUnauthorized());
    }
}
