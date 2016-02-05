DROP TABLE t_group
IF EXISTS;

DROP TABLE t_user
IF EXISTS;

CREATE TABLE t_user (
  id    BIGINT,
  login VARCHAR(256)
);

CREATE TABLE t_group (
  id     BIGINT,
  userId BIGINT,
  name   VARCHAR(256)
);

INSERT INTO t_user (id, login) VALUES (1, 'gabriel');
INSERT INTO t_user (id, login) VALUES (2, 'sandra');

INSERT INTO t_group (id, userId, name) VALUES (1, 1, 'admin');
INSERT INTO t_group (id, userId, name) VALUES (2, 1, 'system');
INSERT INTO t_group (id, userId, name) VALUES (3, 2, 'user');