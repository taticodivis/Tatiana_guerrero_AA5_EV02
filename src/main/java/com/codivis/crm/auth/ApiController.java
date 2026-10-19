package com.codivis.crm.auth;

import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Registro y autenticación; la sesión permite acceder a la API de clientes. */
@RestController
@RequestMapping("/api")
public class ApiController {
    private final UsuarioRepository usuarios;
    public record Resultado(boolean ok, String codigo, String mensaje) { }

    public ApiController(UsuarioRepository usuarios) { this.usuarios = usuarios; }

    @GetMapping("/estado")
    public ResponseEntity<Resultado> estado() {
        return respuesta(200, true, "API_DISPONIBLE", "API Codivis disponible.");
    }

    @PostMapping(value = "/registro", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<Resultado> registro(@RequestParam(name = "usuario", required = false) String usuario,
            @RequestParam(name = "clave", required = false) String clave, @RequestParam(name = "confirmacion", required = false) String confirmacion)
            throws IOException {
        if (vacio(usuario) || vacio(clave)) return campos();
        try {
            usuarios.crear(usuario, clave, confirmacion);
            return respuesta(201, true, "USUARIO_CREADO", "Usuario registrado correctamente.");
        } catch (IllegalArgumentException ex) {
            boolean duplicado = "Ese usuario ya está registrado.".equals(ex.getMessage());
            return respuesta(duplicado ? 409 : 400, false,
                    duplicado ? "USUARIO_DUPLICADO" : "DATOS_INVALIDOS", ex.getMessage());
        }
    }

    @PostMapping(value = "/login", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<Resultado> login(@RequestParam(name = "usuario", required = false) String usuario,
            @RequestParam(name = "clave", required = false) String clave, HttpServletRequest solicitud) {
        if (vacio(usuario) || vacio(clave)) return campos();
        boolean correcto = usuarios.autenticar(usuario, clave);
        if (correcto) {
            // Renueva la sesión al autenticar y evita reutilizar un identificador previo.
            var anterior = solicitud.getSession(false);
            if (anterior != null) anterior.invalidate();
            solicitud.getSession(true).setAttribute("usuarioAutenticado", usuario.strip().toLowerCase(java.util.Locale.ROOT));
        }
        return respuesta(correcto ? 200 : 401, correcto,
                correcto ? "AUTENTICACION_CORRECTA" : "CREDENCIALES_INVALIDAS",
                correcto ? "Autenticacion satisfactoria." : "Usuario o clave incorrectos.");
    }

    @PostMapping("/logout")
    public ResponseEntity<Resultado> salir(HttpServletRequest solicitud) {
        var sesion = solicitud.getSession(false);
        if (sesion != null) sesion.invalidate();
        return respuesta(200, true, "SESION_CERRADA", "Sesión cerrada.");
    }

    private static boolean vacio(String texto) { return texto == null || texto.isBlank(); }
    private static ResponseEntity<Resultado> campos() {
        return respuesta(400, false, "CAMPOS_OBLIGATORIOS", "Usuario y clave son obligatorios.");
    }
    static ResponseEntity<Resultado> respuesta(int estado, boolean ok, String codigo, String mensaje) {
        return ResponseEntity.status(estado).header("Cache-Control", "no-store")
                .header("X-Content-Type-Options", "nosniff").body(new Resultado(ok, codigo, mensaje));
    }
}
