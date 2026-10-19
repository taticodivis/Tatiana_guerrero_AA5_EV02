package com.codivis.crm.clientes;

import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** API local de clientes: creación, listado, consulta, actualización y eliminación. */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteRepository clientes;
    public record Datos(String nombre, String correo, String telefono) { }
    public record Resultado(boolean ok, String codigo, String mensaje, Object datos) { }
    public ClienteController(ClienteRepository clientes) { this.clientes = clientes; }

    @GetMapping
    public ResponseEntity<Resultado> listar() {
        return respuesta(200, "CLIENTES_LISTADOS", "Listado de clientes.", clientes.listar());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Resultado> consultar(@PathVariable("id") long id) {
        return respuesta(200, "CLIENTE_ENCONTRADO", "Cliente encontrado.", clientes.obtener(id));
    }
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Resultado> crear(@RequestBody Datos datos) throws IOException {
        Cliente cliente = clientes.guardar(null, datos.nombre(), datos.correo(), datos.telefono());
        return respuesta(201, "CLIENTE_CREADO", "Cliente creado.", cliente);
    }
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<Resultado> actualizar(@PathVariable("id") long id, @RequestBody Datos datos) throws IOException {
        return respuesta(200, "CLIENTE_ACTUALIZADO", "Cliente actualizado.",
                clientes.guardar(id, datos.nombre(), datos.correo(), datos.telefono()));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Resultado> eliminar(@PathVariable("id") long id) throws IOException {
        clientes.eliminar(id);
        return respuesta(200, "CLIENTE_ELIMINADO", "Cliente eliminado.", null);
    }
    @ExceptionHandler(ClienteRepository.NoExiste.class)
    ResponseEntity<Resultado> noExiste() {
        return respuesta(404, "CLIENTE_NO_ENCONTRADO", "El cliente no existe.", null);
    }
    @ExceptionHandler(ClienteRepository.Duplicado.class)
    ResponseEntity<Resultado> duplicado() {
        return respuesta(409, "CORREO_DUPLICADO", "Ese correo ya pertenece a otro cliente.", null);
    }
    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class})
    ResponseEntity<Resultado> invalido(Exception ex) {
        String mensaje = ex instanceof IllegalArgumentException ? ex.getMessage() : "Revisa el JSON y el identificador del cliente.";
        return respuesta(400, "DATOS_INVALIDOS", mensaje, null);
    }
    private static ResponseEntity<Resultado> respuesta(int estado, String codigo, String mensaje, Object datos) {
        return ResponseEntity.status(estado).header("Cache-Control", "no-store")
                .header("X-Content-Type-Options", "nosniff")
                .body(new Resultado(estado < 300, codigo, mensaje, datos));
    }
}
