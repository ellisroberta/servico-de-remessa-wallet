-- Define o esquema para a tabela de carteiras
CREATE TABLE tb_wallet (
    id UUID PRIMARY KEY,
    balance_brl DECIMAL,
    balance_usd DECIMAL,
    email VARCHAR(255) UNIQUE NOT NULL,
);

-- Define o esquema para a tabela de transações
CREATE TABLE tb_transaction (
    id UUID PRIMARY KEY,
    from_user_id UUID,
    to_user_id UUID,
    amount_brl DECIMAL,
    amount_usd DECIMAL,
    exchange_rate DECIMAL,
    transaction_date TIMESTAMP,
    CONSTRAINT FK_from_user FOREIGN KEY (from_user_id) REFERENCES tb_user(id),
    CONSTRAINT FK_to_user FOREIGN KEY (to_user_id) REFERENCES tb_user(id)
);