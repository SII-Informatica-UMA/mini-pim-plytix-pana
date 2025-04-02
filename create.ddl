create sequence usuario_seq start with 1 increment by 50;
create table usuario (id integer not null, apellido1 varchar(255), apellido2 varchar(255), email varchar(255), nombre varchar(255), role varchar(255), primary key (id));
