IF OBJECT_ID('dbo.t_user', 'U') IS NOT NULL
  DROP TABLE t_user;

IF OBJECT_ID('dbo.t_group', 'U') IS NOT NULL
  DROP TABLE t_group;

IF OBJECT_ID('dbo.t_address', 'U') IS NOT NULL
  DROP TABLE t_address;

IF OBJECT_ID('dbo.t_country', 'U') IS NOT NULL
  DROP TABLE t_country;

IF OBJECT_ID('dbo.t_asso_user_address', 'U') IS NOT NULL
  DROP TABLE t_asso_user_address;

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

CREATE TABLE t_group (
  id            VARCHAR(256),
  version       INT,
  user_id       VARCHAR(256),
  name          VARCHAR(256),
  created_date  DATETIME,
  created_by    VARCHAR(240),
  updated_date  DATETIME,
  updated_by    VARCHAR(240),
  canceled      BIT DEFAULT 0,
  canceled_date DATETIME,
  canceled_by   VARCHAR(240)
);


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
  canceled      BIT DEFAULT 0,
  canceled_date DATETIME,
  canceled_by   VARCHAR(240)
);

CREATE TABLE t_country (
  id            VARCHAR(256),
  version       INT,
  code          VARCHAR(256),
  name          VARCHAR(256),
  created_date  DATETIME,
  created_by    VARCHAR(240),
  updated_date  DATETIME,
  updated_by    VARCHAR(240),
  canceled      BIT DEFAULT 0,
  canceled_date DATETIME,
  canceled_by   VARCHAR(240)
);

CREATE TABLE t_asso_user_address (
  user_id    VARCHAR(256),
  address_id VARCHAR(256)
);

INSERT INTO t_country (id, version, code, name, created_date, created_by) VALUES ('1', 0, 'FRA', 'france', GETDATE(), 'GABY');
INSERT INTO t_country (id, version, code, name, created_date, created_by) VALUES ('2', 0, 'CHI', 'chine', GETDATE(), 'GABY');
INSERT INTO t_country (id, version, code, name, created_date, created_by) VALUES ('3', 0, 'ENG', 'england', GETDATE(), 'GABY');

INSERT INTO t_address (id, version, city, postal_zip, country_id, created_date, created_by) VALUES ('1', 0, 'Versailles', '78000', '1', GETDATE(), 'GABY');
INSERT INTO t_address (id, version, city, postal_zip, country_id, created_date, created_by) VALUES ('2', 0, 'Valence', '26000', '1', GETDATE(), 'GABY');
INSERT INTO t_address (id, version, city, postal_zip, country_id, created_date, created_by) VALUES ('3', 0, 'London', '??', '3', GETDATE(), 'GABY');
INSERT INTO t_address (id, version, city, postal_zip, country_id, created_date, created_by) VALUES ('4', 0, 'Xi''an', '??', '2', GETDATE(), 'GABY');

INSERT INTO t_user (id, version, login, country_code, country_id, address_id, created_date, created_by) VALUES ('1', 0, 'gabriel', 'FRA', '1', '2', GETDATE(), 'GABY');
INSERT INTO t_user (id, version, login, country_code, country_id, address_id, created_date, created_by) VALUES ('2', 0, 'sandra', 'CHI', '1', '1', GETDATE(), 'GABY');

INSERT INTO t_asso_user_address (user_id, address_id) VALUES ('1', '1');
INSERT INTO t_asso_user_address (user_id, address_id) VALUES ('1', '2');
INSERT INTO t_asso_user_address (user_id, address_id) VALUES ('1', '3');
INSERT INTO t_asso_user_address (user_id, address_id) VALUES ('2', '4');

INSERT INTO t_group (id, version, user_id, name, created_date, created_by) VALUES ('1', 0, '1', 'admin', GETDATE(), 'GABY');
INSERT INTO t_group (id, version, user_id, name, created_date, created_by) VALUES ('2', 0, '1', 'system', GETDATE(), 'GABY');
INSERT INTO t_group (id, version, user_id, name, created_date, created_by) VALUES ('3', 0, '2', 'user', GETDATE(), 'GABY');
INSERT INTO t_group (id, version, user_id, name, created_date, created_by, canceled, canceled_date, canceled_by) VALUES ('4', 0, '1', 'simple', GETDATE(), 'GABY', 1, GETDATE(), 'GABY');