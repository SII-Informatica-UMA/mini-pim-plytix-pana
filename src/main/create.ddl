create table cuenta (id bigint not null, nif varchar(255), dir_fiscal varchar(255), dueno_id bigint, fecha_alta date, nombre varchar(255), usuarios bigint array, plan bigint, primary key (id));
create table plan (id bigint not null, max_activos bigint, max_almacenamiento bigint, max_categorias_activos bigint, max_categorias_productos bigint, max_productos bigint, max_relaciones bigint, nombre varchar(255), precio float(53), primary key (id));
alter table if exists cuenta add constraint fk_cuenta_plan foreign key (plan) references plan;
