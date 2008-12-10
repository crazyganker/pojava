-- This creates a table, group role and user role for a postgresql database.

CREATE ROLE test_users
  NOSUPERUSER NOINHERIT NOCREATEDB NOCREATEROLE;

CREATE ROLE pojava LOGIN
  NOSUPERUSER NOINHERIT NOCREATEDB NOCREATEROLE;
GRANT test_users TO pojava;

CREATE TABLE dao_test
(
  test_id integer NOT NULL,
  test_bigint bigint,
  test_boolean boolean,
  test_character1 character(1),
  test_character5 character(5),
  test_varchar1 character varying(1),
  test_varchar5 character varying(5),
  test_date date,
  test_double double precision,
  test_real real,
  test_numeric_10x4 numeric(10,4),
  test_smallint smallint,
  test_time_with_tz time with time zone,
  test_time_without_tz time without time zone,
  test_timestamp_with_tz timestamp with time zone,
  test_timestamp_without_tz timestamp without time zone,
  CONSTRAINT dao_test_pkey PRIMARY KEY (test_id)
)
WITH (OIDS=FALSE);
ALTER TABLE dao_test OWNER TO test_users;
GRANT ALL ON TABLE dao_test TO test_users;
GRANT SELECT, UPDATE, INSERT, DELETE, TRIGGER ON TABLE dao_test TO pojava;

