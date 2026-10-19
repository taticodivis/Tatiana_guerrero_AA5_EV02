// Regenera la colección JSON importable y su versión local de Postman.
const fs = require('fs');
const path = require('path');
const root = __dirname;
const title = 'Codivis API - AA5-EV02 - Pruebas de AA5-EV01';
const folder = path.join(root, 'postman', 'collections', title);
const credentials = { usuario: 'pedro', clave: 'PedroLuis2026!' };
const pedro = { nombre: 'Pedro', correo: 'pedro@example.com', telefono: '3001234567' };
const luis = { nombre: 'Luis', correo: 'luis@example.com', telefono: '3007654321' };
const cases = [];
function add(name, method, route, status, code, body, extra = []) {
  const request = { method, header: [], url: '{{baseUrl}}' + route };
  if (body) {
    if (route === '/registro' || route === '/login') {
      request.body = { mode: 'urlencoded', urlencoded: Object.entries(body).map(([key, value]) => ({ key, value, type: 'text' })) };
    } else {
      request.header = [{ key: 'Content-Type', value: 'application/json' }];
      request.body = { mode: 'raw', raw: JSON.stringify(body, null, 2), options: { raw: { language: 'json' } } };
    }
  }
  const statuses = Array.isArray(status) ? status : [status];
  const codes = Array.isArray(code) ? code : [code];
  const tests = [
    `pm.test("HTTP ${statuses.join(' o ')}", () => pm.expect(pm.response.code).to.be.oneOf(${JSON.stringify(statuses)}));`,
    'pm.test("Respuesta JSON y contrato", () => {',
    '  pm.expect(pm.response.headers.get("Content-Type")).to.include("application/json");',
    '  const data = pm.response.json();',
    `  pm.expect(data.codigo).to.be.oneOf(${JSON.stringify(codes)});`,
    '  pm.expect(data.ok).to.eql(pm.response.code < 300);',
    '  pm.expect(data.mensaje).to.be.a("string").and.not.empty;',
    '  pm.expect(data).not.to.have.property("clave");',
    '  pm.expect(data).not.to.have.property("hash");',
    ...(statuses.length > 1 ? ['  pm.expect(data.codigo).to.eql(pm.response.code === 201 ? "USUARIO_CREADO" : "USUARIO_DUPLICADO");'] : []),
    ...extra.map(line => '  ' + line),
    '});'
  ];
  cases.push({ name, request, event: [{ listen: 'test', script: { type: 'text/javascript', exec: tests } }] });
}
add('01 - Estado', 'GET', '/estado', 200, 'API_DISPONIBLE');
add('02 - Listar sin login', 'GET', '/clientes', 401, 'AUTENTICACION_REQUERIDA');
add('03 - Crear sin login', 'POST', '/clientes', 401, 'AUTENTICACION_REQUERIDA', pedro);
add('04 - Registrar pedro', 'POST', '/registro', [201,409], ['USUARIO_CREADO','USUARIO_DUPLICADO'], { ...credentials, confirmacion: credentials.clave });
add('05 - Login incorrecto', 'POST', '/login', 401, 'CREDENCIALES_INVALIDAS', { ...credentials, clave: 'Incorrecta12345!' });
add('06 - Login correcto', 'POST', '/login', 200, 'AUTENTICACION_CORRECTA', credentials, [
  'pm.expect(pm.cookies.get("JSESSIONID")).to.be.a("string").and.not.empty;'
]);
add('07 - Crear cliente Pedro', 'POST', '/clientes', 201, 'CLIENTE_CREADO', pedro, [
  'pm.expect(data.datos.id).to.be.a("number").and.above(0);',
  'pm.expect(data.datos.nombre).to.eql("Pedro");',
  'pm.expect(data.datos.correo).to.eql("pedro@example.com");',
  'pm.collectionVariables.set("clientePedroId", String(data.datos.id));'
]);
add('08 - Ver clientes', 'GET', '/clientes', 200, 'CLIENTES_LISTADOS', null, [
  'pm.expect(data.datos).to.be.an("array");',
  'pm.expect(data.datos.some(c => c.id === Number(pm.collectionVariables.get("clientePedroId")) && c.nombre === "Pedro")).to.eql(true);'
]);
add('09 - Ver cliente Pedro', 'GET', '/clientes/{{clientePedroId}}', 200, 'CLIENTE_ENCONTRADO', null, [
  'pm.expect(data.datos.nombre).to.eql("Pedro");',
  'pm.expect(data.datos.id).to.eql(Number(pm.collectionVariables.get("clientePedroId")));'
]);
add('10 - Actualizar cliente Pedro', 'PUT', '/clientes/{{clientePedroId}}', 200, 'CLIENTE_ACTUALIZADO', { ...pedro, telefono: '3101234567' }, [
  'pm.expect(data.datos.telefono).to.eql("3101234567");'
]);
add('11 - Correo invalido', 'POST', '/clientes', 400, 'DATOS_INVALIDOS', { ...luis, correo: 'correo-invalido' });
add('12 - Correo duplicado', 'POST', '/clientes', 409, 'CORREO_DUPLICADO', { ...luis, correo: pedro.correo });
add('13 - Crear cliente Luis', 'POST', '/clientes', 201, 'CLIENTE_CREADO', luis, [
  'pm.expect(data.datos.nombre).to.eql("Luis");',
  'pm.expect(data.datos.correo).to.eql("luis@example.com");',
  'pm.collectionVariables.set("clienteLuisId", String(data.datos.id));'
]);
add('14 - Eliminar cliente Luis', 'DELETE', '/clientes/{{clienteLuisId}}', 200, 'CLIENTE_ELIMINADO');
add('15 - Luis ya no existe', 'GET', '/clientes/{{clienteLuisId}}', 404, 'CLIENTE_NO_ENCONTRADO');
add('16 - Eliminar cliente Pedro', 'DELETE', '/clientes/{{clientePedroId}}', 200, 'CLIENTE_ELIMINADO');
add('17 - Pedro ya no existe', 'GET', '/clientes/{{clientePedroId}}', 404, 'CLIENTE_NO_ENCONTRADO');
add('18 - Cerrar sesion', 'POST', '/logout', 200, 'SESION_CERRADA');
add('19 - Listar despues de salir', 'GET', '/clientes', 401, 'AUTENTICACION_REQUERIDA');
add('20 - Crear despues de salir', 'POST', '/clientes', 401, 'AUTENTICACION_REQUERIDA', pedro);
const collection = {
  info: { name: title, schema: 'https://schema.getpostman.com/json/collection/v2.1.0/collection.json',
    description: '20 pruebas: sesión, acceso protegido y CRUD de clientes. Usuario pedro. Ejecutar en orden. Postman conserva automáticamente la cookie JSESSIONID. Registro y login usan formularios; clientes usa JSON. Las pruebas 14 y 16 eliminan los dos clientes de demostración creados por la colección.' },
  variable: [ { key: 'baseUrl', value: 'http://localhost:18086/crm/api' }, { key: 'clientePedroId', value: '' }, { key: 'clienteLuisId', value: '' } ],
  event: [{ listen: 'prerequest', script: { type: 'text/javascript', exec: [
    'if (pm.info.requestName === "01 - Estado") {',
    '  pm.collectionVariables.set("clientePedroId", "");',
    '  pm.collectionVariables.set("clienteLuisId", "");',
    '}'
  ] } }],
  item: cases
};
fs.mkdirSync(path.join(folder, '.resources'), { recursive: true });
// Retira sólo las solicitudes anteriores de esta misma colección.
for (const file of fs.readdirSync(folder)) {
  if (file.endsWith('.request.yaml')) fs.unlinkSync(path.join(folder, file));
}
const q = JSON.stringify;
let definition = `$kind: collection\nname: ${q(title)}\ndescription: ${q(collection.info.description)}\nvariables:\n`;
for (const v of collection.variable) definition += `  - key: ${q(v.key)}\n    value: ${q(v.value)}\n`;
definition += 'scripts:\n  - type: http:beforeRequest\n    language: text/javascript\n    code: |-\n';
definition += collection.event[0].script.exec.map(s => '      ' + s).join('\n') + '\n';
fs.writeFileSync(path.join(folder, '.resources', 'definition.yaml'), definition);
cases.forEach((item, index) => {
  let yaml = `$kind: http-request\nmethod: ${item.request.method}\nurl: ${q(item.request.url)}\norder: ${(index + 1) * 1000}\n`;
  if (item.request.body?.mode === 'urlencoded') {
    yaml += 'body:\n  type: urlencoded\n  content:\n';
    for (const field of item.request.body.urlencoded) yaml += `    - key: ${q(field.key)}\n      value: ${q(field.value)}\n`;
  } else if (item.request.body?.mode === 'raw') {
    yaml += `headers:\n  - key: Content-Type\n    value: application/json\nbody:\n  type: json\n  content: ${q(item.request.body.raw)}\n`;
  }
  yaml += 'scripts:\n  - type: afterResponse\n    language: text/javascript\n    code: |-\n';
  yaml += item.event[0].script.exec.map(s => '      ' + s).join('\n') + '\n';
  fs.writeFileSync(path.join(folder, item.name + '.request.yaml'), yaml);
});
fs.writeFileSync(path.join(root, 'postman', 'Codivis-AA5-EV02.postman_collection.json'), JSON.stringify(collection, null, 2) + '\n');
console.log('Preparadas 20 pruebas en JSON y YAML.');
