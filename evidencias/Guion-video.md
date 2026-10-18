# Guion del video — Tatiana Guerrero — AA5-EV02

Duración orientativa: 5 a 8 minutos; la guía no fija una duración.

1. Presentación: «Soy Tatiana Guerrero, del programa Análisis y Desarrollo de Software. Presento la evidencia GA7-220501096-AA5-EV02: pruebas de la API de registro e inicio de sesión desarrollada en AA5-EV01».
2. Mostrar la consola del servidor y `ENDPOINT.txt`. Explicar las tres rutas: estado, registro y login. La base es `http://localhost:18086/crm/api`.
3. En Postman, mostrar la colección y ejecutar 01 Estado. Identificar GET, URL, HTTP 200 y respuesta JSON.
4. Ejecutar 02 Registrar usuario con Body x-www-form-urlencoded. Mostrar usuario, clave y confirmación de demostración, HTTP 201 y USUARIO_CREADO.
5. Ejecutar 03 Usuario duplicado. Explicar que HTTP 409 es esperado y que no debe permitir dos cuentas iguales.
6. Ejecutar 04 Login correcto (200), 05 Clave incorrecta (401) y 06 Usuario inexistente (401). El servicio verifica credenciales; no entrega token ni abre sesión.
7. Mostrar los casos 07 a 11: datos obligatorios, clave corta, confirmación diferente y usuario inválido. Explicar las respuestas 400.
8. Mostrar 12 y 13: GET sobre endpoints que requieren POST devuelve 405. Caso 14: JSON devuelve 415 porque la API recibe formularios. Caso 15: POST sobre estado devuelve 405. Caso 16: falta confirmación, 400. Caso 17: normalización del usuario, 200.
9. Ejecutar la colección completa en Run collection, una iteración. Mostrar el resumen real y comprobar 17 solicitudes, 34 aserciones aprobadas y cero fallos. Si falla, corregir y volver a ejecutar antes de grabar el cierre.
10. Mostrar brevemente el documento con capturas. Cerrar indicando los resultados observados y el repositorio en enlace-repositorio.txt.

Grabar la pantalla real y la explicación. Puede usarse OBS Studio, ya instalado. Guardar el MP4 en esta carpeta. No usar contraseñas personales.
