-- ════════════════════════════════════════════════════════════
--  SISTEMA WEB DE GESTIÓN DE BIBLIOTECA
--  Script SQL - Base de datos: biblioteca_db
-- ════════════════════════════════════════════════════════════

CREATE DATABASE IF NOT EXISTS biblioteca_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE biblioteca_db;

-- ── Tabla usuarios ────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS usuarios (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(100)  NOT NULL,
    correo       VARCHAR(150)  NOT NULL UNIQUE,
    contrasena   VARCHAR(255)  NOT NULL,
    tipo_usuario ENUM('ADMINISTRADOR','ESTUDIANTE','DOCENTE') NOT NULL DEFAULT 'ESTUDIANTE',
    creado_en    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ── Tabla libros ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS libros (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    titulo           VARCHAR(200)  NOT NULL,
    autor            VARCHAR(150)  NOT NULL,
    editorial        VARCHAR(150),
    anio_publicacion INT,
    isbn             VARCHAR(20)   UNIQUE,
    categoria        ENUM('CIENCIA','HISTORIA','LITERATURA','TECNOLOGIA','MATEMATICAS','ARTE','OTRO') NOT NULL DEFAULT 'OTRO',
    disponible       BOOLEAN       NOT NULL DEFAULT TRUE,
    stock            INT           NOT NULL DEFAULT 1 CHECK (stock >= 0),
    creado_en        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ── Tabla prestamos ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS prestamos (
    id                       INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id               INT NOT NULL,
    libro_id                 INT NOT NULL,
    fecha_prestamo           DATE NOT NULL,
    fecha_devolucion_esperada DATE NOT NULL,
    fecha_devolucion_real    DATE,
    estado                   ENUM('ACTIVO','DEVUELTO','VENCIDO') NOT NULL DEFAULT 'ACTIVO',
    creado_en                TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_prestamo_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_prestamo_libro   FOREIGN KEY (libro_id)   REFERENCES libros(id)
);

-- ── Índices para búsquedas frecuentes ────────────────────────
CREATE INDEX idx_libros_titulo    ON libros(titulo);
CREATE INDEX idx_libros_autor     ON libros(autor);
CREATE INDEX idx_libros_categoria ON libros(categoria);
CREATE INDEX idx_prestamos_estado ON prestamos(estado);
CREATE INDEX idx_prestamos_usuario ON prestamos(usuario_id);

-- ── Datos de prueba ───────────────────────────────────────────
INSERT INTO usuarios (nombre, correo, contrasena, tipo_usuario) VALUES
    ('Admin Biblioteca', 'admin@biblioteca.cl', 'admin123', 'ADMINISTRADOR'),
    ('Juan Pérez',       'juan@correo.cl',      'juan123',  'ESTUDIANTE'),
    ('María López',      'maria@correo.cl',     'maria123', 'DOCENTE');

INSERT INTO libros (titulo, autor, editorial, anio_publicacion, isbn, categoria, stock) VALUES
    ('Cien años de soledad',      'Gabriel García Márquez', 'Sudamericana',     1967, '978-0307474728', 'LITERATURA',  3),
    ('El principito',             'Antoine de Saint-Exupéry','Salamandra',      1943, '978-8498381498', 'LITERATURA',  2),
    ('Física Universitaria',      'Young & Freedman',        'Pearson',         2013, '978-6073215688', 'CIENCIA',     2),
    ('Algoritmos y Estructuras',  'Thomas Cormen',           'MIT Press',       2009, '978-0262033848', 'TECNOLOGIA',  1),
    ('Historia de Chile',         'Francisco Frías Valenzuela','Zig-Zag',       2000, '978-9561219526', 'HISTORIA',    2);
