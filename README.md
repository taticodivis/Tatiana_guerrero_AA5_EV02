# Codivis — GA7-220501096-AA5-EV02. API.

Tatiana Guerrero — SENA — Análisis y Desarrollo de Software.

Servicio de registro y autenticación basado en AA5-EV01, con gestión de clientes protegida por sesión. Incluye **9 operaciones HTTP y 20 pruebas en Postman**.

## Ejecutar

Requisitos: JDK 17 o superior y Maven. El iniciador utiliza Maven de la carpeta hermana `herramientas` si está disponible; en otros equipos requiere `mvn.cmd` en PATH. Para regenerar el informe o las colecciones se necesita Node.js. Postman Desktop permite ejecutar las pruebas manuales; Postman CLI permite repetir la comprobación automática.

Abrir esta carpeta en VS Code con soporte Java/Maven y ejecutar `src/main/java/Run.java` con Run Java, o abrir `Iniciar.cmd`. Puerto 18086. Al cambiar el código del servidor, detener la ejecución anterior antes de iniciarlo nuevamente. No se distribuyen usuarios ni clientes precreados; el registro de la colección crea la cuenta de demostración.

## Postman

La colección está en `postman/collections/Codivis API - AA5-EV02 - Pruebas de AA5-EV01`. En la vista del cubo, desplegarla con doble clic y abrir las solicitudes. También puede importarse `postman/Codivis-AA5-EV02.postman_collection.json`.

Ejecutar en orden, una iteración:

| Prueba | Resultado esperado |
|---|---|
| 01 Estado | 200 |
| 02 Listar sin login | 401 |
| 03 Crear sin login | 401 |
| 04 Registrar pedro | 201 si es nuevo, 409 si ya existe |
| 05 Login incorrecto | 401 |
| 06 Login correcto | 200; recibe cookie JSESSIONID |
| 07 Crear cliente Pedro | 201 |
| 08 Ver clientes | 200 |
| 09 Ver cliente Pedro | 200 |
| 10 Actualizar cliente Pedro | 200 |
| 11 Correo inválido | 400 |
| 12 Correo duplicado | 409 |
| 13 Crear cliente Luis | 201 |
| 14 Eliminar cliente Luis | 200 |
| 15 Luis ya no existe | 404 |
| 16 Eliminar cliente Pedro | 200 |
| 17 Pedro ya no existe | 404 |
| 18 Cerrar sesión | 200 |
| 19 Listar después de salir | 401 |
| 20 Crear después de salir | 401 |

Usuario: **pedro**. Contraseña de demostración: **PedroLuis2026!**. Registro y login usan Body → x-www-form-urlencoded. Crear y actualizar clientes usan Body → raw → JSON. Las solicitudes ya llevan los datos preparados y capturan los ID de los clientes automáticamente.

Postman debe conservar su cookie jar habilitado: transmite JSESSIONID automáticamente tras el login. No usar Authorization Bearer. La sesión dura hasta el cierre, el reinicio del servidor o 30 minutos de inactividad. Antes de repetir toda la colección, ejecutar Cerrar sesión para que las pruebas 02 y 03 comiencen sin autenticación. Las pruebas 14 y 16 borran los clientes de demostración creados en 07 y 13. Si se interrumpe antes, pueden quedar registrados: continuar desde la prueba interrumpida antes de volver a empezar.

## Evidencia y entrega

Guardar capturas reales del método, URL, Body, estado, respuesta y Test Results en `evidencias/capturas`. El documento de pruebas es `evidencias/Informe-pruebas-AA5-EV02.html`, con capturas incorporadas, ampliación e impresión a PDF. Ejecutar `node Crear-Informe.js` para regenerarlo. Las diez capturas están incorporadas y el login correcto está revisado: HTTP 200, AUTENTICACION_CORRECTA. La revisión queda vinculada al contenido de la imagen. Grabar el video real con Postman y guardarlo en `evidencias/video/Tatiana_Guerrero_AA5_EV02.mp4`. Ejecutar la colección completa: se esperan 20 solicitudes y 40 aserciones aprobadas. La prueba 04 permite un usuario ya existente para repetir el ejercicio, sin declarar que un 409 creó una cuenta.

`Ejecutar-pruebas.ps1` guarda la comprobación CLI. `Crear-Documento.ps1` genera el informe HTML con las capturas reales. La comprobación CLI se presenta separada de los resultados visibles en las imágenes. `Empaquetar.ps1` prepara el ZIP después de completar documento y video; no incluye data, target, .git ni otros ZIP.

La guía exige proyecto, video, documento con pruebas, archivo ENDPOINT y enlace al repositorio. Repositorio: https://github.com/taticodivis/Tatiana_guerrero_AA5_EV02. El informe HTML y las diez capturas están incluidos. **El video está pendiente; el ZIP final se prepara después de incorporarlo.**

Git excluye usuarios, clientes, compilaciones, ZIP y la vinculación local de la cuenta de Postman. La carpeta `postman/collections` contiene las solicitudes portables y sí forma parte del repositorio.

La actividad original de AA5-EV01 se conserva en su carpeta sin estas ampliaciones. Los clientes se guardan en `data/clientes.json`, separados de las cuentas de acceso de `data/usuario.properties`. Registrar un usuario no crea un cliente, y crear un cliente no crea credenciales de acceso.
