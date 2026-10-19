package com.codivis.crm.clientes;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/** Persistencia local: publica cada cambio en memoria sólo después de guardarlo. */
@Repository
public class ClienteRepository {
    private final Path archivo;
    private final ObjectMapper mapper;
    private List<Cliente> clientes;
    public static final class Duplicado extends RuntimeException { }
    public static final class NoExiste extends RuntimeException { }

    public ClienteRepository(@Value("${crm.data.dir}") String carpeta, ObjectMapper mapper) throws IOException {
        this.mapper = mapper;
        archivo = Path.of(carpeta, "clientes.json").toAbsolutePath();
        clientes = Files.exists(archivo)
                ? new ArrayList<>(Arrays.asList(mapper.readValue(archivo.toFile(), Cliente[].class)))
                : new ArrayList<>();
    }

    public synchronized List<Cliente> listar() { return List.copyOf(clientes); }
    public synchronized Cliente obtener(long id) {
        return clientes.stream().filter(c -> c.id() == id).findFirst().orElseThrow(NoExiste::new);
    }
    public synchronized Cliente guardar(Long id, String nombre, String correo, String telefono) throws IOException {
        if (id != null) obtener(id);
        long numero = id == null ? clientes.stream().mapToLong(Cliente::id).max().orElse(0) + 1 : id;
        Cliente cliente = new Cliente(numero, nombre, correo, telefono);
        if (clientes.stream().anyMatch(c -> c.id() != numero && c.correo().equals(cliente.correo())))
            throw new Duplicado();
        List<Cliente> nuevos = new ArrayList<>(clientes);
        nuevos.removeIf(c -> c.id() == numero);
        nuevos.add(cliente);
        persistir(nuevos);
        return cliente;
    }
    public synchronized void eliminar(long id) throws IOException {
        obtener(id);
        List<Cliente> nuevos = new ArrayList<>(clientes);
        nuevos.removeIf(c -> c.id() == id);
        persistir(nuevos);
    }
    private void persistir(List<Cliente> nuevos) throws IOException {
        Files.createDirectories(archivo.getParent());
        Path temporal = Files.createTempFile(archivo.getParent(), "clientes-", ".tmp");
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(temporal.toFile(), nuevos);
            Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
            clientes = nuevos;
        } finally { Files.deleteIfExists(temporal); }
    }
}
