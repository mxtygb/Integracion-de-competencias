# Sistema de Gestión de Biblioteca — Java

**Autores:** Matías Garrido · Felipe Norambuena  
**Asignatura:** Integración de Competencias  
**Docente:** Diego Narváez

---

## Estructura del proyecto

```
biblioteca/
├── pom.xml
└── src/main/java/com/biblioteca/
    ├── Main.java                          ← Punto de entrada (menú de consola)
    ├── model/
    │   ├── Usuario.java                   ← Entidad Usuario
    │   ├── Libro.java                     ← Entidad Libro
    │   └── Prestamo.java                  ← Entidad Préstamo
    ├── dao/
    │   ├── UsuarioDAO.java                ← CRUD usuarios (SQL)
    │   ├── LibroDAO.java                  ← CRUD libros (SQL)
    │   └── PrestamoDAO.java               ← CRUD préstamos (SQL)
    ├── service/
    │   └── BibliotecaService.java         ← Lógica de negocio
    └── util/
        └── ConexionDB.java                ← Conexión MySQL singleton
src/main/resources/
    └── schema.sql                         ← Script de base de datos
```

---

## Requisitos

| Herramienta | Versión mínima |
|-------------|---------------|
| Java JDK    | 17            |
| Maven       | 3.8           |
| MySQL       | 8.0           |

---

## Configuración

### 1. Base de datos

Ejecuta el script SQL en MySQL:

```bash
mysql -u root -p < src/main/resources/schema.sql
```

### 2. Credenciales de conexión

Edita `src/main/java/com/biblioteca/util/ConexionDB.java` y ajusta:

```java
private static final String URL      = "jdbc:mysql://localhost:3306/biblioteca_db?...";
private static final String USUARIO  = "root";
private static final String PASSWORD = "tu_password";
```

---

## Compilar y ejecutar

```bash
# Compilar
mvn clean package

# Ejecutar
java -jar target/sistema-biblioteca.jar
```

---

## Funcionalidades implementadas

### Libros
- Agregar, listar y eliminar libros
- Búsqueda por título, autor o categoría
- Control automático de stock y disponibilidad

### Usuarios
- Registro, listado y eliminación de usuarios
- Tipos: `ADMINISTRADOR`, `ESTUDIANTE`, `DOCENTE`
- Autenticación con correo y contraseña

### Préstamos
- Registrar préstamo (valida disponibilidad del libro)
- Registrar devolución (actualiza stock automáticamente)
- Historial completo, préstamos activos y vencidos
- Plazo de devolución: 14 días por defecto

### Reportes
- Total de libros, disponibles y prestados
- Total de usuarios
- Préstamos activos y vencidos
- Distribución de libros por categoría

---

## Credenciales de prueba (datos incluidos en schema.sql)

| Correo               | Contraseña | Tipo          |
|----------------------|-----------|---------------|
| admin@biblioteca.cl  | admin123  | ADMINISTRADOR |
| juan@correo.cl       | juan123   | ESTUDIANTE    |
| maria@correo.cl      | maria123  | DOCENTE       |
