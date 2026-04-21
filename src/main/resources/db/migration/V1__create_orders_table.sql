CREATE TABLE orders (
    id                BIGSERIAL PRIMARY KEY,
    order_id          BIGINT UNIQUE NOT NULL,
    account_id        BIGINT NOT NULL,
    isin              VARCHAR(12) NOT NULL,
    ticker            VARCHAR(10),
    side              VARCHAR(4) NOT NULL,
    trade_currency    VARCHAR(3) NOT NULL,
    quantity          INT NOT NULL,
    order_type        VARCHAR(3) NOT NULL,
    limit_price       NUMERIC(12,2),
    expires_at        BIGINT,
    status            VARCHAR(20) NOT NULL,
    execution_price   NUMERIC(12,2),
    registration_time BIGINT,
    executed_time     BIGINT,
    mic               VARCHAR(4) NOT NULL,
    created_at        TIMESTAMP DEFAULT NOW(),
    updated_at        TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_orders_account_id ON orders(account_id);
CREATE INDEX idx_orders_status ON orders(status);

