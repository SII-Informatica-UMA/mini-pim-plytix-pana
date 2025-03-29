create table cuenta (id bigint not null, nif varchar(255), dir_fiscal varchar(255), fecha_alta date, nombre varchar(255), plan bigint, primary key (id));
create table plan (id bigint not null, max_activos bigint, max_almacenamiento bigint, max_categorias_activos bigint, max_categorias_productos bigint, max_productos bigint, max_relaciones bigint, nombre varchar(255), precio float(53), primary key (id));
create table usuario (id bigint not null, apellido1 varchar(255), apellido2 varchar(255), email varchar(255), nombre varchar(255), role varchar(255), cuenta bigint, primary key (id));
alter table if exists cuenta add constraint FK9icu60vl8r5stpqt2dstpuv5w foreign key (plan) references plan;
alter table if exists usuario add constraint FKgn35ts0sf913wv8s7e2epi2r foreign key (cuenta) references cuenta;
