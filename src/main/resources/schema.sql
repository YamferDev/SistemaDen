CREATE TABLE IF NOT EXISTS ciudadano (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         nombre VARCHAR(100) NOT NULL,
    dni VARCHAR(8) NOT NULL,
    telefono VARCHAR(15) NOT NULL,
    correo VARCHAR(100) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    genero VARCHAR(15)
    );

ALTER TABLE ciudadano ADD COLUMN IF NOT EXISTS correo VARCHAR(100);

CREATE TABLE IF NOT EXISTS funcionario (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           dni VARCHAR(8),
    nombre VARCHAR(100) NOT NULL,
    cargo VARCHAR(50) NOT NULL,
    especialidad VARCHAR(50),
    credenciales VARCHAR(50) NOT NULL
    );

CREATE TABLE IF NOT EXISTS tipo_denuncia (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             nombre VARCHAR(100) NOT NULL,
    area_encargada VARCHAR(50) NOT NULL
    );

CREATE TABLE IF NOT EXISTS denuncia (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        descripcion TEXT NOT NULL,
                                        fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        ubicacion VARCHAR(200) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    observacion TEXT,
    ciudadano_id BIGINT NOT NULL,
    tipo_id BIGINT NOT NULL,
    funcionario_id BIGINT,
    FOREIGN KEY (ciudadano_id) REFERENCES ciudadano(id),
    FOREIGN KEY (tipo_id) REFERENCES tipo_denuncia(id),
    FOREIGN KEY (funcionario_id) REFERENCES funcionario(id)
    );

ALTER TABLE denuncia ADD COLUMN IF NOT EXISTS observacion TEXT;

MERGE INTO funcionario (dni, nombre, cargo, especialidad, credenciales)
    KEY(dni)
    VALUES (
    '00000000',
    'admin',
    'ADMINISTRADOR',
    NULL,
    'admin'
    );
MERGE INTO tipo_denuncia (id, nombre, area_encargada)
    KEY(id) VALUES
    (1, 'Robo / Asalto', 'POLICIAL'),
    (2, 'Accidente de Tránsito', 'TRANSITO'),
    (3, 'Vandalismo', 'POLICIAL'),
    (4, 'Violencia Familiar', 'PSICOLOGIA'),
    (5, 'Fraude / Estafa', 'LEGAL');