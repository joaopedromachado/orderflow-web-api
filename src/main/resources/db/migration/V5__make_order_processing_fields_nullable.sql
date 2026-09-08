ALTER TABLE tb_orders
    ALTER COLUMN processed_at DROP NOT NULL,
    ALTER COLUMN receipt_url DROP NOT NULL;
