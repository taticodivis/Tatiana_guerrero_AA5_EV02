package com.codivis.crm.clientes;

import java.util.Locale;
import java.util.Objects;

/** Datos del cliente y validaciones compartidas por creación y actualización. */
public record Cliente(long id, String nombre, String correo, String telefono) {
    public Cliente {
        nombre = Objects.requireNonNullElse(nombre, "").strip();
        correo = Objects.requireNonNullElse(correo, "").strip().toLowerCase(Locale.ROOT);
        telefono = Objects.requireNonNullElse(telefono, "").strip();
        if (nombre.length() < 2 || nombre.length() > 80)
            throw new IllegalArgumentException("El nombre debe tener entre 2 y 80 caracteres.");
        if (correo.length() > 120 || !correo.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))
            throw new IllegalArgumentException("Escribe un correo válido.");
        if (!telefono.isEmpty() && !telefono.matches("[+()0-9 .-]{7,25}"))
            throw new IllegalArgumentException("Escribe un teléfono válido.");
    }
}
