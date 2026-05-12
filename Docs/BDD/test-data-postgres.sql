-- =========================================================================
-- SCRIPT DE DATOS DE PRUEBA MÍNIMOS PARA POSTGRESQL (RENDER)
-- =========================================================================

-- 1. EMPRESA
INSERT INTO Empresa (nombre_comercial, razon_social, email_contacto, telefono_principal, created_at)
VALUES ('El Pelotero', 'El Pelotero S.A.C.', 'admin@elpelotero.com', '015551234', CURRENT_TIMESTAMP);

-- 2. SUCURSALES
-- PostgreSQL no usa variables locales igual que SQL Server en scripts simples.
-- Usaremos subconsultas para obtener los IDs generados.
INSERT INTO Sucursal (empresa_id, nombre, direccion, telefono, created_at, activo)
VALUES (
    (SELECT empresa_id FROM Empresa WHERE nombre_comercial = 'El Pelotero'),
    'Sede Principal', 'Av. Central 123, Lima', '012233445', CURRENT_TIMESTAMP, true
);

INSERT INTO Sucursal (empresa_id, nombre, direccion, telefono, created_at, activo)
VALUES (
    (SELECT empresa_id FROM Empresa WHERE nombre_comercial = 'El Pelotero'),
    'Sede Secundaria', 'Av. Norte 456, Lima', '014455667', CURRENT_TIMESTAMP, true
);

-- 3. USUARIOS
-- La contraseña para todos es "admin123" (hasheada con BCrypt)
-- Hash: $2a$12$Txi9HcU5tqGwMiB3zxiXSeEF6oMeoYDKbZu6AfuYMBqnqay4/eDgu

-- SUPERADMIN no está asociado a ninguna sucursal específica
INSERT INTO Usuario (empresa_id, sucursal_id, username, nombre, email, password, rol, created_at)
VALUES (
    (SELECT empresa_id FROM Empresa WHERE nombre_comercial = 'El Pelotero'),
    NULL,
    'superadmin', 'Super Administrador', 'superadmin@elpelotero.com',
    '$2a$12$Txi9HcU5tqGwMiB3zxiXSeEF6oMeoYDKbZu6AfuYMBqnqay4/eDgu',
    'SUPERADMIN', CURRENT_TIMESTAMP
);

-- ADMIN está asociado a la Sede Principal
INSERT INTO Usuario (empresa_id, sucursal_id, username, nombre, email, password, rol, created_at)
VALUES (
    (SELECT empresa_id FROM Empresa WHERE nombre_comercial = 'El Pelotero'),
    (SELECT sucursal_id FROM Sucursal WHERE nombre = 'Sede Principal'),
    'admin', 'Admin Principal', 'admin@elpelotero.com',
    '$2a$12$Txi9HcU5tqGwMiB3zxiXSeEF6oMeoYDKbZu6AfuYMBqnqay4/eDgu',
    'ADMIN', CURRENT_TIMESTAMP
);

-- RECEPCIONISTA está asociado a la Sede Principal
INSERT INTO Usuario (empresa_id, sucursal_id, username, nombre, email, password, rol, created_at)
VALUES (
    (SELECT empresa_id FROM Empresa WHERE nombre_comercial = 'El Pelotero'),
    (SELECT sucursal_id FROM Sucursal WHERE nombre = 'Sede Principal'),
    'recepcionista', 'Recepcionista Principal', 'recepcionista@elpelotero.com',
    '$2a$12$Txi9HcU5tqGwMiB3zxiXSeEF6oMeoYDKbZu6AfuYMBqnqay4/eDgu',
    'RECEPCIONISTA', CURRENT_TIMESTAMP
);

-- 4. CLIENTE
INSERT INTO Cliente (tip_documento, documento, nombre, email, telefono, created_at)
VALUES ('DNI', '76543210', 'Juan Pérez', 'juan.perez@email.com', '987654321', CURRENT_TIMESTAMP);

-- 5. CANCHA
INSERT INTO Cancha (sucursal_id, nombre, precio_hora, created_at, estado_cancha)
VALUES (
    (SELECT sucursal_id FROM Sucursal WHERE nombre = 'Sede Principal'),
    'Cancha A (Fútbol 5)', 80.00, CURRENT_TIMESTAMP, 'DISPONIBLE'
);