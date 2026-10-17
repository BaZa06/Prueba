# Papelería Campus — Sistema de Gestión de Productos

## 1. Descripción del proyecto

Papelería Campus es una aplicación desarrollada en JavaScript con Node.js y PostgreSQL. Su objetivo es facilitar la administración de un catálogo de productos mediante operaciones de registro, consulta, actualización y eliminación.

El sistema permite gestionar productos de las categorías Papelería, Tecnología y Accesorios, consultar sus existencias y visualizar información almacenada en una base de datos relacional.

Este proyecto fue desarrollado como parte del Laboratorio TableView — CRUD de la asignatura Programación de Aplicaciones de Escritorio.

## 2. Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| JavaScript | Lenguaje de programación |
| Node.js | Entorno de ejecución |
| HTML y CSS | Diseño de la interfaz |
| PostgreSQL | Base de datos |
| node-postgres (`pg`) | Conexión entre JavaScript y PostgreSQL |
| Visual Studio Code | Entorno de desarrollo |
| npm | Gestión de dependencias |

**Versiones por confirmar:** Node.js, PostgreSQL y paquete `pg`.

## 3. Funcionalidades implementadas

### Gestión de productos (CRUD)

- Registrar nuevos productos.
- Consultar productos almacenados en PostgreSQL.
- Actualizar la información de un producto seleccionado.
- Eliminar productos mediante confirmación.
- Refrescar el listado de productos.
- Seleccionar productos de la tabla para cargar sus datos en el formulario.

### Visualización

La tabla presenta seis columnas:

1. ID del producto.
2. Código.
3. Nombre.
4. Categoría.
5. Precio.
6. Stock.

Los productos se muestran ordenados por código ascendente. También se incluye un contador de registros visibles.

### Consultas y filtros

El sistema incorpora siete casos de consulta, identificados como B1–B7:

| Caso | Función |
|---|---|
| B1 | Buscar productos relacionados con Cuaderno |
| B2 | Buscar códigos que comienzan por T00 |
| B3 | Consultar productos de Papelería |
| B4 | Mostrar productos con existencias |
| B5 | Consultar productos USB de Tecnología con existencias |
| B6 | Comprobar búsquedas sin coincidencias |
| B7 | Limpiar filtros y recuperar el listado completo |

También dispone de una casilla (checkbox) para filtrar productos con stock mayor que cero.

## 4. Validaciones

La aplicación incorpora las siguientes validaciones:

- V1: Código obligatorio, entre 1 y 10 caracteres.
- V2: Nombre entre 3 y 80 caracteres.
- V3: Categorías permitidas.
- V4: Rechazo de precios no numéricos.
- V5: Rechazo de precios iguales o inferiores a cero.
- V6: Máximo de dos decimales y límite de precio.
- V7: Stock exclusivamente entero.
- V8: Stock no negativo y dentro del rango permitido.
- V9: Prevención de códigos duplicados.
- V10: Validación de selección antes de actualizar o eliminar.

Los códigos se normalizan eliminando espacios laterales y convirtiéndolos a mayúsculas. Los precios se validan con máximo dos decimales para evitar errores de redondeo.

## 5. Base de datos

La aplicación utiliza PostgreSQL con la siguiente configuración:

- Base de datos: `papeleria_campus`
- Esquema: `inventario`
- Tabla: `producto`
- Puerto habitual: `5432`

### Estructura de la tabla

| Campo | Tipo | Restricción |
|---|---|---|
| id_producto | SERIAL | PRIMARY KEY |
| codigo | VARCHAR(10) | NOT NULL, UNIQUE |
| nombre | VARCHAR(80) | NOT NULL |
| categoria | VARCHAR(20) | NOT NULL |
| precio | NUMERIC(10,2) | NOT NULL |
| stock | INTEGER | NOT NULL |

Se incorporaron restricciones CHECK para validar el código, la longitud del nombre, la categoría, el precio y el stock.

La base contiene inicialmente 12 productos distribuidos entre las tres categorías.

## 6. Configuración y ejecución

### Requisitos

- Tener instalado Node.js y npm.
- Contar con PostgreSQL instalado y funcionando.
- Utilizar Visual Studio Code o un editor compatible.

### Preparación de la base de datos

1. Abrir pgAdmin o una herramienta compatible con PostgreSQL.
2. Crear la base de datos `papeleria_campus`.
3. Conectarse a la base creada.
4. Ejecutar el script de estructura para crear el esquema, la tabla y sus restricciones.
5. Ejecutar el script de datos para insertar los 12 productos iniciales.

### Configuración de conexión

El archivo `db.js` administra la conexión a PostgreSQL utilizando el paquete `pg`.

La cadena de conexión local es:

`postgresql://usuario:contraseña@localhost:5432/papeleria_campus`

El usuario y la contraseña deben configurarse según la instalación local de PostgreSQL, preferiblemente en un archivo `.env`. No se deben publicar contraseñas reales en GitHub; el archivo `.env` debe incluirse en `.gitignore`.

### Ejecución

1. Abrir el proyecto en Visual Studio Code.
2. Instalar las dependencias:

   ```bash
   npm install
   ```

3. Verificar que PostgreSQL esté funcionando.
4. Iniciar la aplicación:

   ```bash
   npm start
   ```

5. Comprobar que aparezcan los productos almacenados en la base de datos.

## 7. Organización del proyecto

El proyecto está organizado principalmente en los siguientes archivos y carpetas:

**controller:** contiene la lógica encargada de gestionar las validaciones, los eventos y las operaciones con los productos.

**model:** contiene la definición de los atributos de cada producto.

**db.js:** responsable de establecer la conexión con PostgreSQL.

**public:** contiene los archivos HTML, CSS y JavaScript utilizados para construir la interfaz gráfica.

El acceso a PostgreSQL se realiza mediante consultas parametrizadas para evitar inyección SQL.

## 8. Pruebas realizadas

Se realizaron pruebas funcionales sobre los siguientes componentes:

- Operaciones de registro, consulta, actualización y eliminación.
- Selección de productos y carga de datos al formulario.
- Consultas B1–B7.
- Validaciones V1–V10.
- Manejo de excepciones E1–E4.
- Sincronización de la tabla y del contador de registros.

Las pruebas fueron realizadas con una base de datos local.

La documentación detallada de cada prueba, sus resultados y capturas debe incluirse en el archivo `pruebas.md` y la carpeta `evidencias`.

## 9. Restauración de los datos iniciales

Para restaurar los 12 productos originales y reiniciar la numeración de los identificadores, se puede ejecutar:

```sql
TRUNCATE TABLE inventario.producto
RESTART IDENTITY;
```

Posteriormente debe ejecutarse nuevamente el script de inserción de los 12 productos.

**Advertencia:** esta operación elimina los registros existentes. Debe utilizarse únicamente sobre la base de pruebas.

## 10. Fuentes y herramientas de apoyo

- Guía del laboratorio TableView — CRUD proporcionada por el docente.
- Documentación de Node.js: https://nodejs.org/docs/
- Documentación de JavaScript (MDN): https://developer.mozilla.org/es/docs/Web/JavaScript
- Documentación de PostgreSQL: https://www.postgresql.org/docs/
- Documentación de node-postgres: https://node-postgres.com/
- Visual Studio Code y pgAdmin como herramientas de desarrollo.

## 11. Autora

**Estudiante:** Blanca Alejandra Zeledón Abea

**Asignatura:** Programación de Aplicaciones de Escritorio

**Proyecto:** Laboratorio TableView — CRUD

**Modalidad:** Individual

**Fecha:** 16/10/2026