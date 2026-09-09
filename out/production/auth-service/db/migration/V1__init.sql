-- Auth service schema (SQL Server)

CREATE TABLE users (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    email           NVARCHAR(255) NOT NULL,
    username        NVARCHAR(100) NOT NULL,
    password_hash   NVARCHAR(255) NOT NULL,
    full_name       NVARCHAR(255) NULL,
    status          NVARCHAR(20) NOT NULL CONSTRAINT DF_users_status DEFAULT 'ACTIVE',
    created_at      DATETIME2 NOT NULL CONSTRAINT DF_users_created_at DEFAULT SYSUTCDATETIME(),
    updated_at      DATETIME2 NULL,
    CONSTRAINT UQ_users_email UNIQUE (email),
    CONSTRAINT UQ_users_username UNIQUE (username)
);

CREATE TABLE roles (
    id      INT IDENTITY(1,1) PRIMARY KEY,
    name    NVARCHAR(50) NOT NULL,
    CONSTRAINT UQ_roles_name UNIQUE (name)
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    CONSTRAINT PK_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT FK_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT FK_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE login_audit (
    id          BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id     BIGINT NULL,
    ip_address  VARCHAR(45) NULL,
    user_agent  NVARCHAR(500) NULL,
    success     BIT NOT NULL,
    created_at  DATETIME2 NOT NULL CONSTRAINT DF_login_audit_created_at DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_login_audit_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX IX_login_audit_user_created ON login_audit (user_id, created_at DESC);

INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
