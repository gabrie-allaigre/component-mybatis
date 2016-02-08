DROP TABLE t_group
IF EXISTS;

DROP TABLE t_user
IF EXISTS;

CREATE TABLE t_user (
  id      VARCHAR(256),
  version INT,
  login   VARCHAR(256)
);

CREATE TABLE t_group (
  id      VARCHAR(256),
  version INT,
  userId  VARCHAR(256),
  name    VARCHAR(256)
);

INSERT INTO t_user (id, version, login) VALUES ('1', 0, 'gabriel');
INSERT INTO t_user (id, version, login) VALUES ('2', 0, 'sandra');

INSERT INTO t_group (id, version, userId, name) VALUES ('1', 0, '1', 'admin');
INSERT INTO t_group (id, version, userId, name) VALUES ('2', 0, '1', 'system');
INSERT INTO t_group (id, version, userId, name) VALUES ('3', 0, '2', 'user');