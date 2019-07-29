CREATE SCHEMA projdb;

CREATE TABLE projdb.USERS (
                                  UID int not null primary key,
                                  USERNAME varchar(15) not null,
                                  FIRST_NAME varchar(20),
                                  LAST_NAME varchar(20),
                                  EMAIL varchar(40),
                                  PASSWORD varchar(40),
                                  ROLE varchar(10),
                                  IS_DELETED bool

);
CREATE TABLE projdb.GROUPS (
                              GID int not null primary key,
                              OWNER_UID int not null ,
                              GROUP_NAME varchar(60) not null,
                           FOREIGN KEY(OWNER_UID) REFERENCES projdb.USERS(UID)
);
CREATE TABLE projdb.GROUPMEMBER (
                               GID int not null,
                               UID int not null,
                               FOREIGN KEY (GID) REFERENCES projdb.GROUPS(GID),
                               FOREIGN KEY (UID) REFERENCES projdb.USERS(UID),
                               primary key (GID, UID)
);
INSERT INTO projdb.USERS ( UID, USERNAME, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD, ROLE, IS_DELETED)
VALUES (0, 'admin', 'admin','admin','admin','admin','admin',0);
INSERT INTO projdb.USERS ( UID, USERNAME, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD, ROLE, IS_DELETED)
VALUES (1, 'user', 'user','user','user','user','user',0);
INSERT INTO projdb.GROUPS ( GID, OWNER_UID, GROUP_NAME)
VALUES (0, 0, 'default_group');
INSERT INTO projdb.GROUPMEMBER (GID, UID)
VALUES (0, 0);