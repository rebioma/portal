CREATE TABLE info_shape(
	id serial primary key,
	shapetable character varying(100) not null,
	shapeprecision double precision default 0.01,
	shapelabel varchar(100),
	nom_champ_gid varchar(100),
	nom_champ_libelle varchar(100),
	nom_champ_geometrique varchar(100)
);