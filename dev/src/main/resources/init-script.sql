DROP TABLE t_user
IF EXISTS;

CREATE TABLE t_user (
  id           VARCHAR(256),
  version      INT,
  login        VARCHAR(256),
  country_code VARCHAR(256),
  country_id   VARCHAR(256),
  address_id   VARCHAR(256),
  created_date DATETIME,
  created_by   VARCHAR(240),
  updated_date DATETIME,
  updated_by   VARCHAR(240)
);

DROP TABLE t_group
IF EXISTS;

CREATE TABLE t_group (
  id            VARCHAR(256),
  version       INT,
  user_id       VARCHAR(256),
  name          VARCHAR(256),
  created_date  DATETIME,
  created_by    VARCHAR(240),
  updated_date  DATETIME,
  updated_by    VARCHAR(240),
  canceled      BOOLEAN DEFAULT FALSE,
  canceled_date DATETIME,
  canceled_by   VARCHAR(240)
);

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

DROP TABLE t_asso_user_address
IF EXISTS;

CREATE TABLE t_asso_user_address (
  user_id    VARCHAR(256),
  address_id VARCHAR(256)
);

DROP TABLE t_nls
IF EXISTS;

CREATE TABLE t_nls (
  table_name    VARCHAR(256),
  column_name   VARCHAR(256),
  language_code VARCHAR(3),
  table_id      VARCHAR(256),
  meaning       VARCHAR(256)
);

INSERT INTO t_nls (table_name, column_name, language_code, table_id, meaning) VALUES ('T_COUNTRY', 'NAME', 'eng', '1', 'Cheese');
INSERT INTO t_nls (table_name, column_name, language_code, table_id, meaning) VALUES ('T_COUNTRY', 'NAME', 'fra', '1', 'Fromage');
INSERT INTO t_nls (table_name, column_name, language_code, table_id, meaning) VALUES ('T_ADDRESS', 'MEANING', 'eng', '1', 'Nothing');

INSERT INTO t_country (id, version, code, name, created_date, created_by) VALUES ('1', 0, 'FRA', 'france', SYSDATE, 'GABY');
INSERT INTO t_country (id, version, code, name, created_date, created_by) VALUES ('2', 0, 'CHI', 'chine', SYSDATE, 'GABY');
INSERT INTO t_country (id, version, code, name, created_date, created_by) VALUES ('3', 0, 'ENG', 'england', SYSDATE, 'GABY');

INSERT INTO t_address (id, version, city, postal_zip, country_id, created_date, created_by) VALUES ('1', 0, 'Versailles', '78000', '1', SYSDATE, 'GABY');
INSERT INTO t_address (id, version, city, postal_zip, country_id, created_date, created_by) VALUES ('2', 0, 'Valence', '26000', '1', SYSDATE, 'GABY');
INSERT INTO t_address (id, version, city, postal_zip, country_id, created_date, created_by) VALUES ('3', 0, 'London', '??', '3', SYSDATE, 'GABY');
INSERT INTO t_address (id, version, city, postal_zip, country_id, created_date, created_by) VALUES ('4', 0, 'Xi''an', '??', '2', SYSDATE, 'GABY');

INSERT INTO t_user (id, version, login, country_code, country_id, address_id, created_date, created_by) VALUES ('1', 0, 'gabriel', 'FRA', '1', '2', SYSDATE, 'GABY');
INSERT INTO t_user (id, version, login, country_code, country_id, address_id, created_date, created_by) VALUES ('2', 0, 'sandra', 'CHI', '1', '1', SYSDATE, 'GABY');

INSERT INTO t_asso_user_address (user_id, address_id) VALUES ('1', '1');
INSERT INTO t_asso_user_address (user_id, address_id) VALUES ('1', '2');
INSERT INTO t_asso_user_address (user_id, address_id) VALUES ('1', '3');
INSERT INTO t_asso_user_address (user_id, address_id) VALUES ('2', '4');

INSERT INTO t_group (id, version, user_id, name, created_date, created_by) VALUES ('1', 0, '1', 'admin', SYSDATE, 'GABY');
INSERT INTO t_group (id, version, user_id, name, created_date, created_by) VALUES ('2', 0, '1', 'system', SYSDATE, 'GABY');
INSERT INTO t_group (id, version, user_id, name, created_date, created_by) VALUES ('3', 0, '2', 'user', SYSDATE, 'GABY');
INSERT INTO t_group (id, version, user_id, name, created_date, created_by, canceled, canceled_date, canceled_by) VALUES ('4', 0, '1', 'simple', SYSDATE, 'GABY', TRUE, SYSDATE, 'GABY');

