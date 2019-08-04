CREATE SCHEMA projdb;

CREATE TABLE projdb.USERS (
                                  UID int not null primary key AUTO_INCREMENT,
                                  USERNAME varchar(15) not null unique,
                                  FIRST_NAME varchar(20),
                                  LAST_NAME varchar(20),
                                  EMAIL varchar(40),
                                  PASSWORD varchar(40),
                                  ROLE varchar(10),
                                  IS_DELETED bool

);
CREATE TABLE projdb.GRROUPS (
                              GID int not null primary key AUTO_INCREMENT,
                              OWNER_UID int not null ,
                              GROUP_NAME varchar(60) not null,
                           FOREIGN KEY(OWNER_UID) REFERENCES projdb.USERS(UID)
);
CREATE TABLE projdb.GROUPMEMBER (
                               GID int not null,
                               UID int not null,
                               FOREIGN KEY (GID) REFERENCES projdb.workgroups(GID),
                               FOREIGN KEY (UID) REFERENCES projdb.USERS(UID),
                               primary key (GID, UID)
);
INSERT INTO projdb.USERS ( USERNAME, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD, ROLE, IS_DELETED)
VALUES ('admin', 'admin','admin','admin','admin','admin',0);
INSERT INTO projdb.USERS ( USERNAME, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD, ROLE, IS_DELETED)
VALUES ('user', 'user','user','user','user','user',0);
INSERT INTO projdb.workgroups ( OWNER_UID, GROUP_NAME)
VALUES (1, 'default_group');
INSERT INTO projdb.GROUPMEMBER (GID, UID)
VALUES (2, 1);

CREATE TABLE projdb.AUDITLOG (
                                    ID int PRIMARY KEY auto_increment,
                                    ACTION_NAME varchar(50),
                                    ACTION_TIME datetime,
                                    ACTION_AUTHOR varchar(50),
                                    ENTITY_DETAILS varchar(1000),
                                    ACTION_STATUS varchar(50)
);