-- Cria tabela de pedidos
CREATE TABLE tb_products (
     product_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
     name VARCHAR(50) NOT NULL,
     price NUMERIC(15,2) NOT NULL CHECK ( price >= 0 ),
     stock INTEGER NOT NULL CHECK ( stock >= 0 ),
     active BOOLEAN NOT NULL
);

-- Cria tabela de items do pedido
CREATE TABLE tb_orders (
    order_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    total_amount NUMERIC(15,2) NOT NULL CHECK ( total_amount >= 0 ),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    receipt_url VARCHAR(255) NOT NULL,

    CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id)
        REFERENCES tb_users(user_id)
        ON DELETE RESTRICT
);

-- Cria tabela de produtos (1 Product ---- N OrderItem)
CREATE TABLE tb_orders_items (
    item_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    unit_price NUMERIC(15,2) NOT NULL CHECK ( unit_price >= 0 ),
    quantity INTEGER NOT NULL CHECK ( quantity > 0 ),

    CONSTRAINT fk_orders_items_order
        FOREIGN KEY (order_id)
        REFERENCES tb_orders(order_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_orders_items_product
        FOREIGN KEY (product_id)
        REFERENCES tb_products(product_id)
        ON DELETE RESTRICT
);

-- Criando indexes para os ids para facilitar na busca futuramente
CREATE INDEX idx_orders_user_id ON tb_orders(user_id);
CREATE INDEX idx_orders_items_order_id ON tb_orders_items(order_id);
CREATE INDEX idx_orders_items_product_id ON tb_orders_items(product_id);
