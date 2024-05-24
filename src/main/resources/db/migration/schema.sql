-- src/main/resources/db/migration/V1__schema.sql

-- Define o esquema para a tabela de carteiras
CREATE TABLE tb_wallet (
    id UUID PRIMARY KEY,
    balance_brl DECIMAL,
    balance_usd DECIMAL,
    user_id UUID, -- Referência ao usuário associado à carteira
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE CASCADE -- Relação com o usuário
);

-- Define o esquema para a tabela de transações
CREATE TABLE tb_transaction (
    id UUID PRIMARY KEY,
    wallet_id UUID, -- Carteira associada à transação
    from_user_id UUID, -- Usuário remetente da transação
    to_user_id UUID, -- Usuário destinatário da transação
    amount_brl DECIMAL,
    amount_usd DECIMAL,
    exchange_rate DECIMAL,
    status VARCHAR(50), -- ou ENUM conforme necessário
    transaction_date TIMESTAMP,
    FOREIGN KEY (wallet_id) REFERENCES tb_wallet(id),
    FOREIGN KEY (from_user_id) REFERENCES tb_user(id),
    FOREIGN KEY (to_user_id) REFERENCES tb_user(id)
);