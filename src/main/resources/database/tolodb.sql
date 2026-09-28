create database tolodb;
use tolodb;

create table usuario(
dni char(9) primary key,
nombre varchar(20),
apellido varchar(20),
mail varchar(50),
contrasenia varchar(50),
pais varchar(20),
tlf integer,
tarjeta integer,
tipo enum ('CLIENTE', 'EMPLEADO', 'ADMIN'));

insert into usuario (dni, nombre, apellido, mail, contrasenia, pais, tlf, tarjeta, tipo) values
('12345678A', 'Ana', 'Garcia', 'ana.garcia@example.com', 'claveAna123', 'Espana', 600123456, 411111111, 'CLIENTE'),
('23456789B', 'Luis', 'Perez', 'luis.perez@example.com', 'claveLuis123', 'Mexico', 600234567, 422222222, 'CLIENTE'),
('34567890C', 'Marta', 'Lopez', 'marta.lopez@example.com', 'claveMarta123', 'Espana', 600345678, 433333333, 'EMPLEADO'),
('45678901D', 'Diego', 'Sanchez', 'diego.sanchez@example.com', 'claveDiego123', 'Argentina', 600456789, 444444444, 'EMPLEADO'),
('56789012E', 'Sofia', 'Martin', 'sofia.martin@example.com', 'claveSofia123', 'Chile', 600567890, 455555555, 'ADMIN'),
('67890123F', 'Pablo', 'Ruiz', 'pablo.ruiz@example.com', 'clavePablo123', 'Colombia', 600678901, 466666666, 'ADMIN');

