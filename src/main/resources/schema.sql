-- Table: public.country

CREATE TABLE IF NOT EXISTS public.country
(
  id     BIGINT                       NOT NULL,
  shape  GEOMETRY(MultiPolygon, 4326) NOT NULL,
  iso_a2 CHARACTER VARYING(2) COLLATE pg_catalog."default",
  iso_a3 CHARACTER VARYING(3) COLLATE pg_catalog."default",
  name   CHARACTER VARYING(40) COLLATE pg_catalog."default",
  CONSTRAINT country_pkey PRIMARY KEY (id)
)
WITH (
OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.country
  OWNER TO postgres;

-- Index: sidx_country_shape

-- DROP INDEX public.sidx_country_shape;

CREATE INDEX IF NOT EXISTS sidx_country_shape
  ON public.country USING GIST
  (shape)
TABLESPACE pg_default;

TRUNCATE TABLE public.country;

-- Table: public.location

-- DROP TABLE public.location;

CREATE TABLE IF NOT EXISTS public.location
(
  id       BIGINT   NOT NULL,
  geometry GEOMETRY NOT NULL,
  name     CHARACTER VARYING(255) COLLATE pg_catalog."default",
  user_id  CHARACTER VARYING(30) COLLATE pg_catalog."default",
  CONSTRAINT location_pkey PRIMARY KEY (id)
)
WITH (
OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.location
  OWNER TO postgres;

-- Index: sidx_location_geometry

-- DROP INDEX public.sidx_location_geometry;

CREATE INDEX IF NOT EXISTS sidx_location_geometry
  ON public.location USING GIST
  (geometry)
TABLESPACE pg_default;