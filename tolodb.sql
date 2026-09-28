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
tipo enum ('CLIENTE', 'EMPLEADO', 'JEFE'));

