# Evidencia GA7-220501096-AA5-EV02 — API

Tatiana Guerrero · SENA · Análisis y Desarrollo de Software

Esta entrega prueba el servicio de registro y autenticación desarrollado en AA5-EV01. Incluye su código fuente. Se usa el puerto **18086** para evitar conflictos con la instancia anterior en 8086. Los parámetros del formulario se nombran explícitamente en el controlador para admitir compilación desde Maven y VS Code.

## Realizar las pruebas y capturas

1. Ejecutar `Iniciar.cmd` y esperar el mensaje de inicio. Mantener abierta la consola. Dirección: http://localhost:18086/crm/api/estado.
2. Abrir Postman Desktop. Pulsar **Import → Files** y seleccionar `postman/Codivis-AA5-EV02.postman_collection.json`.
3. En la colección, pulsar **Run collection**, seleccionar las 17 solicitudes y ejecutar **una iteración en el orden original**. La primera petición genera usuarios nuevos. No hace falta crear cuentas manualmente ni configurar Authorization.
4. Comprobar el resultado: 17 solicitudes, 34 aserciones aprobadas y cero fallos. Los HTTP 400, 401, 405, 409 y 415 son resultados esperados de los casos negativos.
5. Capturar el resumen de la ejecución. Para documentar cada petición, ejecutar desde la colección en orden: abrir método/URL y **Body → x-www-form-urlencoded**, pulsar **Send** y capturar la respuesta JSON, estado y **Test Results**. Al ejecutar individualmente, empezar por 01 y 02; no volver a ejecutar 01 antes de terminar, pues cambiaría el usuario de prueba.
6. Guardar las capturas en `evidencias/capturas`, preferiblemente como `01.png` a `17.png` y `18-resumen.png`. Insertarlas en las secciones correspondientes de `evidencias/Documento-pruebas-AA5-EV02.docx` y registrar los resultados de Postman Desktop. El documento diferencia la comprobación CLI ya realizada de la evidencia visual pendiente.
7. Grabar el video siguiendo `evidencias/Guion-video.md`; guardarlo como `evidencias/video/Tatiana_Guerrero_AA5_EV02.mp4`.
8. Revisar el documento y ejecutar `Empaquetar.ps1` para generar el ZIP dentro de esta misma carpeta. El empaquetador exige el video, las capturas y la confirmación escrita de la revisión del documento.

## Contenido exigido por la guía 7, apartado 3.1.5

- Archivos del proyecto: `src`, `pom.xml` e iniciadores.
- Video real de las pruebas en Postman.
- Documento con pantallazos y resultados de las pruebas.
- `ENDPOINT.txt` con métodos, rutas, cuerpos y respuestas.
- `enlace-repositorio.txt`: repositorio propio de esta evidencia: https://github.com/taticodivis/Tatiana_guerrero_AA5_EV02.

## Comprobación técnica reproducible

Con la API iniciada, ejecutar `Ejecutar-pruebas.ps1`. Usa Postman CLI instalado y guarda salida, fecha, objetivo y código de salida en `evidencias`. No publica resultados en Postman Cloud. La CLI puede mostrar un aviso de inicio de sesión; una ejecución local aprobada no requiere publicar sus detalles. Esta comprobación no sustituye las capturas ni el video de Postman Desktop exigidos por la guía.

El servicio recibe formularios y devuelve JSON. No emite tokens ni crea sesiones. Usa datos de demostración; `data`, `target`, `.git` y otros ZIP quedan fuera de la entrega.

La carpeta de esta evidencia es `proyecto-aa5-ev02-api`. Los borradores anteriores quedan fuera del repositorio y del ZIP de entrega.
