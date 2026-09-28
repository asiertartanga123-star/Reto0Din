create database volaredb;
use volaredb;

create table aerolinea(
id_A integer primary key,
nombre_A varchar(50),
pais varchar(50),
codigoIATA varchar(50));

create table cliente(
id_C integer primary key,
nombre_C varchar(50),
email varchar(50),
telefono varchar(50),
ruta varchar(50));




