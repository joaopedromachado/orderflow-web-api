-- Cria tabela de roles do usuario
CREATE TABLE tb_roles (
    role_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Cria tabela de usuarios
CREATE TABLE tb_users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Cria tabela mista com userId e roleId conectados com a primary/foreign key
CREATE TABLE tb_users_roles (
    user_id UUID NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES tb_users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES tb_roles(role_id) ON DELETE CASCADE
);

-- Inserção dos roles padrão
INSERT INTO tb_roles (role_id, name)
VALUES (1, 'ADMIN'), (2, 'BASIC')
    ON CONFLICT (role_id) DO
UPDATE SET name = EXCLUDED.name;

-- Índices
CREATE INDEX idx_users_username ON tb_users(username);
CREATE INDEX idx_users_roles_user_id ON tb_users_roles(user_id);
CREATE INDEX idx_users_roles_role_id ON tb_users_roles(role_id);