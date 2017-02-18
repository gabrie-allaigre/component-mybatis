DROP TABLE t_nls
IF EXISTS;

CREATE TABLE t_nls (
  table_name    VARCHAR(256),
  column_name   VARCHAR(256),
  language_code VARCHAR(3),
  table_id      VARCHAR(256),
  meaning       VARCHAR(256)
);

INSERT INTO t_nls (table_name, column_name, language_code, table_id, meaning)
VALUES ('T_COUNTRY', 'NAME', 'eng', '1', 'Cheese');
INSERT INTO t_nls (table_name, column_name, language_code, table_id, meaning)
VALUES ('T_COUNTRY', 'NAME', 'fra', '1', 'Fromage');
INSERT INTO t_nls (table_name, column_name, language_code, table_id, meaning)
VALUES ('T_COUNTRY', 'NAME', 'fra', '6', 'United states of america');
INSERT INTO t_nls (table_name, column_name, language_code, table_id, meaning)
VALUES ('T_ADDRESS', 'MEANING', 'eng', '1', 'Nothing');

DROP TABLE t_address
IF EXISTS;

CREATE TABLE t_address (
  id            VARCHAR(256),
  version       INT,
  city          VARCHAR(256),
  postal_zip    VARCHAR(256),
  country_id    VARCHAR(256),
  created_date  DATETIME,
  created_by    VARCHAR(240),
  updated_date  DATETIME,
  updated_by    VARCHAR(240),
  canceled      BOOLEAN DEFAULT FALSE,
  canceled_date DATETIME,
  canceled_by   VARCHAR(240)
);

DROP TABLE t_country
IF EXISTS;

CREATE TABLE t_country (
  id            VARCHAR(256),
  version       INT,
  code          VARCHAR(256),
  name          VARCHAR(256),
  created_date  DATETIME,
  created_by    VARCHAR(240),
  updated_date  DATETIME,
  updated_by    VARCHAR(240),
  canceled      BOOLEAN DEFAULT FALSE,
  canceled_date DATETIME,
  canceled_by   VARCHAR(240)
);

INSERT INTO t_country (id, version, code, name, created_date, created_by)
VALUES ('1', 0, 'FRA', 'france', SYSDATE, 'GABY');
INSERT INTO t_country (id, version, code, name, created_date, created_by)
VALUES ('2', 0, 'CHI', 'chine', SYSDATE, 'GABY');
INSERT INTO t_country (id, version, code, name, created_date, created_by)
VALUES ('3', 0, 'ENG', 'england', SYSDATE, 'GABY');
INSERT INTO t_country (id, version, code, name, created_date, created_by)
VALUES ('4', 0, 'ESP', 'espagne', SYSDATE, 'GABY');
INSERT INTO t_country (id, version, code, name, created_date, created_by)
VALUES ('5', 0, 'ITA', 'italie', SYSDATE, 'GABY');
INSERT INTO t_country (id, version, code, name, created_date, created_by)
VALUES ('6', 0, 'USA', 'etat-unis', SYSDATE, 'GABY');
INSERT INTO t_country (id, version, code, name, created_date, created_by)
VALUES ('7', 0, 'F%', 'f', SYSDATE, 'GABY');

INSERT INTO t_address (id, version, city, postal_zip, country_id, created_date, created_by)
VALUES ('1', 0, 'Versailles', '78000', '1', SYSDATE, 'GABY');
INSERT INTO t_address (id, version, city, postal_zip, country_id, created_date, created_by)
VALUES ('2', 0, 'Valence', '26000', '1', SYSDATE, 'GABY');
INSERT INTO t_address (id, version, city, postal_zip, country_id, created_date, created_by)
VALUES ('3', 0, 'London', '??', '3', SYSDATE, 'GABY');
INSERT INTO t_address (id, version, city, postal_zip, country_id, created_date, created_by)
VALUES ('4', 0, 'Xi''an', '??', '2', SYSDATE, 'GABY');
INSERT INTO t_address (id, version, city, postal_zip, country_id, created_date, created_by)
VALUES ('5', 0, 'Noisy-le-roi', '78230', '1', SYSDATE, 'GABY');

DROP TABLE t_person
IF EXISTS;

CREATE TABLE t_person (
  id             VARCHAR(256),
  version        INT,
  first_name     VARCHAR(256),
  last_name      VARCHAR(256),
  age            INT,
  height         FLOAT,
  birthday       DATE,
  address_id     VARCHAR(256),
  address_bis_id VARCHAR(256),
  created_date   DATETIME,
  created_by     VARCHAR(240),
  updated_date   DATETIME,
  updated_by     VARCHAR(240)
);

INSERT INTO t_person (id, version, first_name, last_name, age, height, birthday, address_id, created_date, created_by)
VALUES ('1', 0, 'Gabriel', 'Allaigre', 36, 186, DATE '1980-02-07', '1', SYSDATE, 'GABY');
INSERT INTO t_person (id, version, first_name, last_name, age, height, birthday, address_id, created_date, created_by)
VALUES ('2', 0, 'Sandra', 'Allaigre', 38, 150, DATE '1978-04-05', '2', SYSDATE, 'GABY');
INSERT INTO t_person (id, version, first_name, last_name, age, height, birthday, address_id, created_date, created_by)
VALUES ('3', 0, 'Laureline', 'Allaigre', 4, 105, DATE '2012-05-17', '1', SYSDATE, 'GABY');
INSERT INTO t_person (id, version, first_name, last_name, age, height, birthday, address_id, created_date, created_by)
VALUES ('4', 0, 'Raphael', 'Allaigre', 1, 80, DATE '2015-05-13', '3', SYSDATE, 'GABY');
INSERT INTO t_person (id, version, first_name, last_name, age, height, birthday, address_id, created_date, created_by)
VALUES ('5', 0, 'David', 'Allaigre', 40, 180, DATE '1976-05-13', null, SYSDATE, 'GABY');


DROP TABLE T_ASSO_INT_ADDRESS
IF EXISTS;

CREATE TABLE T_ASSO_INT_ADDRESS (
  address_id VARCHAR(256),
  int_id     VARCHAR(256)
);

DROP TABLE T_ASSO_PERSON_INT
IF EXISTS;

CREATE TABLE T_ASSO_PERSON_INT (
  person_id VARCHAR(256),
  int_id    VARCHAR(256)
);

DROP TABLE T_ASSO_PERSON_ADDRESS
IF EXISTS;

CREATE TABLE T_ASSO_PERSON_ADDRESS (
  person_id  VARCHAR(256),
  address_id VARCHAR(256)
);