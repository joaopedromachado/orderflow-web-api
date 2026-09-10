CREATE TABLE tb_address (
    address_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    cep VARCHAR(8) NOT NULL,
    street VARCHAR(255) NOT NULL,
    complement VARCHAR(255),
    neighborhood VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state CHAR(2) NOT NULL,
    default_address BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_address_user
        FOREIGN KEY (user_id)
        REFERENCES tb_users(user_id)
        ON DELETE CASCADE
);

CREATE INDEX idx_address_user_id ON tb_address(user_id);

CREATE UNIQUE INDEX uq_address_default_per_user
    ON tb_address(user_id)
    WHERE default_address = TRUE;
