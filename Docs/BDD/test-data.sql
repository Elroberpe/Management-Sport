-- =========================================================================
-- SCRIPT DE DATOS DE PRUEBA PARA DESARROLLO (SQL SERVER)
-- =========================================================================
-- Nota: Este script asume que la base de datos está vacía y las tablas
-- han sido creadas previamente por el script SQL.txt.
-- =========================================================================
GO

-- 1. EMPRESA
INSERT INTO Empresa (nombre_comercial, razon_social, email_contacto, telefono_principal)
VALUES ('El Pelotero', 'El Pelotero S.A.C.', 'admin@elpelotero.com', '015551234');
GO

-- 2. SUCURSALES (2)
INSERT INTO Sucursal (empresa_id, nombre, direccion, telefono)
VALUES (1, 'Sede Miraflores', 'Av. Larco 123, Miraflores', '014455667');
INSERT INTO Sucursal (empresa_id, nombre, direccion, telefono)
VALUES (1, 'Sede Surco', 'Av. Primavera 456, Surco', '012233445');
GO

-- 3. USUARIOS (para asociar a las reservas)
-- La contraseña para todos es "admin123" (hasheada con BCrypt)
-- El hash válido para "admin123" es: $2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6
INSERT INTO Usuario (empresa_id, sucursal_id, username, nombre, email, password, rol)
VALUES (1, NULL, 'superadmin', 'Super Administrador', 'superadmin@elpelotero.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'SUPERADMIN');
INSERT INTO Usuario (empresa_id, sucursal_id, username, nombre, email, password, rol)
VALUES (1, 1, 'admin_mira', 'Admin Miraflores', 'admin.mira@elpelotero.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'ADMIN');
INSERT INTO Usuario (empresa_id, sucursal_id, username, nombre, email, password, rol)
VALUES (1, 2, 'recepcionista_surco', 'Recepcionista Surco', 'recep.surco@elpelotero.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', 'RECEPCIONISTA');
GO

-- 4. CLIENTES (4)
INSERT INTO Cliente (tip_documento, documento, nombre, email, telefono)
VALUES ('DNI', '76543210', 'Juan Pérez', 'juan.perez@email.com', '987654321');
INSERT INTO Cliente (tip_documento, documento, nombre, email, telefono)
VALUES ('DNI', '12345678', 'Maria García', 'maria.garcia@email.com', '912345678');
INSERT INTO Cliente (tip_documento, documento, nombre, email, telefono)
VALUES ('RUC', '20506070801', 'Empresa XYZ S.A.C.', 'contacto@xyz.com', '015006070');
INSERT INTO Cliente (tip_documento, documento, nombre, email, telefono)
VALUES ('PASAPORTE', 'A1B2C3D4', 'John Smith', 'john.smith@email.com', '999888777');
GO

-- 5. CANCHAS (2 por sucursal)
-- Sucursal 1 (Miraflores)
INSERT INTO Cancha (sucursal_id, nombre, precio_hora) VALUES (1, 'Cancha A (Fútbol 5)', 80.00);
INSERT INTO Cancha (sucursal_id, nombre, precio_hora) VALUES (1, 'Cancha B (Fútbol 7)', 120.00);
-- Sucursal 2 (Surco)
INSERT INTO Cancha (sucursal_id, nombre, precio_hora) VALUES (2, 'Cancha C (Multiusos)', 70.00);
INSERT INTO Cancha (sucursal_id, nombre, precio_hora) VALUES (2, 'Cancha D (Tenis)', 50.00);
GO

-- 6. EVENTO (1)
-- Evento "Torneo Relámpago" en la Sede Surco, organizado por Empresa XYZ
INSERT INTO Evento (sucursal_id, cliente_id, nombre, tipo_evento, monto_pactado, monto_pagado, saldo_pendiente, fecha_inicio, fecha_fin, estado)
VALUES (2, 3, 'Torneo Relámpago XYZ', 'TORNEO', 500.00, 100.00, 400.00, GETDATE() + 10, GETDATE() + 10, 'PROGRAMADO');
GO

-- Horarios y Reservas para el Evento (ID = 1)
DECLARE @EventoId INT = (SELECT evento_id FROM Evento WHERE nombre = 'Torneo Relámpago XYZ');
INSERT INTO evento_horario (evento_id, fecha, hora_inicio, hora_fin)
VALUES (@EventoId, GETDATE() + 10, '18:00:00', '22:00:00');
INSERT INTO Reserva (cancha_id, cliente_id, usuario_id, evento_id, tipo_reserva, fecha, hora_inicio, hora_fin, monto_total, monto_pagado, saldo_pendiente, estado_reserva)
VALUES (3, 3, 2, @EventoId, 'EVENTO', GETDATE() + 10, '18:00:00', '22:00:00', 0, 0, 0, 'PAGADA');
GO

-- 7. RESERVAS REGULARES (3) y sus PAGOS
-- Reserva 1: Pagada completamente
INSERT INTO Reserva (cancha_id, cliente_id, usuario_id, fecha, hora_inicio, hora_fin, monto_total, monto_pagado, saldo_pendiente, estado_reserva)
VALUES (1, 1, 1, GETDATE() + 2, '19:00:00', '20:00:00', 80.00, 80.00, 0.00, 'PAGADA');
INSERT INTO Pago (reserva_id, monto, metodo_pago) VALUES (SCOPE_IDENTITY(), 80.00, 'YAPE');
GO

-- Reserva 2: Pendiente con un adelanto
INSERT INTO Reserva (cancha_id, cliente_id, usuario_id, fecha, hora_inicio, hora_fin, monto_total, monto_pagado, saldo_pendiente, estado_reserva)
VALUES (2, 2, 1, GETDATE() + 3, '20:00:00', '21:30:00', 180.00, 50.00, 130.00, 'PENDIENTE');
INSERT INTO Pago (reserva_id, monto, metodo_pago) VALUES (SCOPE_IDENTITY(), 50.00, 'EFECTIVO');
GO

-- Reserva 3: Cancelada (previamente tuvo un pago y fue reembolsado)
INSERT INTO Reserva (cancha_id, cliente_id, usuario_id, fecha, hora_inicio, hora_fin, monto_total, monto_pagado, saldo_pendiente, estado_reserva, motivo_cancelacion)
VALUES (4, 4, 2, GETDATE() + 5, '10:00:00', '11:00:00', 50.00, 0.00, 50.00, 'REEMBOLSADO', 'Cancelado por el cliente.');
DECLARE @ReservaCanceladaId INT = SCOPE_IDENTITY();
INSERT INTO Pago (reserva_id, monto, metodo_pago, tipo_transaccion) VALUES (@ReservaCanceladaId, 20.00, 'TARJETA', 'INGRESO');
INSERT INTO Pago (reserva_id, monto, metodo_pago, tipo_transaccion, nota) VALUES (@ReservaCanceladaId, 20.00, 'TARJETA', 'SALIDA', 'Reembolso por cancelación');
GO

-- 8. MANTENIMIENTO (1)
-- Mantenimiento programado para la Cancha A que debería impedir nuevas reservas
INSERT INTO Mantenimiento (cancha_id, hora_inicio, hora_fin, tipo_mantenimiento, motivo)
VALUES (1, DATEADD(day, 1, GETDATE()), DATEADD(day, 1, GETDATE()), 'PREVENTIVO', 'Pintado de líneas');
GO
