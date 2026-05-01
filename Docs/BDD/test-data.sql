-- =========================================================================
-- SCRIPT DE DATOS DE PRUEBA MÍNIMOS PARA DESARROLLO
-- =========================================================================
-- Nota: Este script asume que las tablas han sido creadas previamente
-- por el script SQL.txt.
-- =========================================================================
GO

-- 1. EMPRESA
INSERT INTO Empresa (nombre_comercial, razon_social, email_contacto, telefono_principal)
VALUES ('El Pelotero', 'El Pelotero S.A.C.', 'admin@elpelotero.com', '015551234');
GO

-- 2. SUCURSALES
-- Obtener el ID de la empresa recién creada
DECLARE @EmpresaId INT = (SELECT empresa_id FROM Empresa WHERE nombre_comercial = 'El Pelotero');

INSERT INTO Sucursal (empresa_id, nombre, direccion, telefono)
VALUES (@EmpresaId, 'Sede Principal', 'Av. Central 123, Lima', '012233445');
INSERT INTO Sucursal (empresa_id, nombre, direccion, telefono)
VALUES (@EmpresaId, 'Sede Secundaria', 'Av. Norte 456, Lima', '014455667');
GO

-- 3. USUARIOS
-- La contraseña para todos es "admin123" (hasheada con BCrypt)
-- Hash: $2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6
DECLARE @EmpresaIdUsuarios INT = (SELECT empresa_id FROM Empresa WHERE nombre_comercial = 'El Pelotero');
DECLARE @SucursalPrincipalId INT = (SELECT sucursal_id FROM Sucursal WHERE nombre = 'Sede Principal');

-- SUPERADMIN no está asociado a ninguna sucursal específica
INSERT INTO Usuario (empresa_id, sucursal_id, username, nombre, email, password, rol)
VALUES (@EmpresaIdUsuarios, NULL, 'superadmin', 'Super Administrador', 'superadmin@elpelotero.com', '$2a$12$Txi9HcU5tqGwMiB3zxiXSeEF6oMeoYDKbZu6AfuYMBqnqay4/eDgu', 'SUPERADMIN');

-- ADMIN está asociado a la Sede Principal
INSERT INTO Usuario (empresa_id, sucursal_id, username, nombre, email, password, rol)
VALUES (@EmpresaIdUsuarios, @SucursalPrincipalId, 'admin', 'Admin Principal', 'admin@elpelotero.com', '$2a$12$Txi9HcU5tqGwMiB3zxiXSeEF6oMeoYDKbZu6AfuYMBqnqay4/eDgu', 'ADMIN');

-- RECEPCIONISTA está asociado a la Sede Principal
INSERT INTO Usuario (empresa_id, sucursal_id, username, nombre, email, password, rol)
VALUES (@EmpresaIdUsuarios, @SucursalPrincipalId, 'recepcionista', 'Recepcionista Principal', 'recepcionista@elpelotero.com', '$2a$12$Txi9HcU5tqGwMiB3zxiXSeEF6oMeoYDKbZu6AfuYMBqnqay4/eDgu', 'RECEPCIONISTA');
GO

-- 4. CLIENTE
INSERT INTO Cliente (tip_documento, documento, nombre, email, telefono)
VALUES ('DNI', '76543210', 'Juan Pérez', 'juan.perez@email.com', '987654321');
GO

-- 5. CANCHA
DECLARE @SucursalPrincipalIdCancha INT = (SELECT sucursal_id FROM Sucursal WHERE nombre = 'Sede Principal');
INSERT INTO Cancha (sucursal_id, nombre, precio_hora)
VALUES (@SucursalPrincipalIdCancha, 'Cancha A (Fútbol 5)', 80.00);
GO

PRINT 'Datos de prueba mínimos insertados correctamente.';
GO
