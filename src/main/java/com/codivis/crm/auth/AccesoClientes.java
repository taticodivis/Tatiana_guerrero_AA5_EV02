package com.codivis.crm.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Exige una sesión autenticada para todos los métodos de la API de clientes. */
@Configuration
public class AccesoClientes implements WebMvcConfigurer {
    private final ObjectMapper mapper;
    public AccesoClientes(ObjectMapper mapper) { this.mapper = mapper; }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                    throws Exception {
                var session = request.getSession(false);
                if (session != null && session.getAttribute("usuarioAutenticado") != null) return true;
                response.setStatus(401);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Cache-Control", "no-store");
                mapper.writeValue(response.getWriter(), new ApiController.Resultado(false,
                        "AUTENTICACION_REQUERIDA", "Inicia sesión para acceder a los clientes."));
                return false;
            }
        }).addPathPatterns("/api/clientes", "/api/clientes/**");
    }
}
