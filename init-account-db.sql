-- =====================================================
-- Account Database Schema
-- Banking System - NTT Data Technical Test
-- =====================================================

-- Create Customer Info Table (synced from Kafka events)
CREATE TABLE IF NOT EXISTS customer_info (
    customer_id UUID PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Accounts Table
CREATE TABLE IF NOT EXISTS accounts (
    account_id UUID PRIMARY KEY,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    account_type VARCHAR(50) NOT NULL,
    current_balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    customer_id UUID NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customer_info(customer_id)
);

-- Create Movements Table
CREATE TABLE IF NOT EXISTS movements (
    movement_id UUID PRIMARY KEY,
    date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    movement_type VARCHAR(50) NOT NULL,
    value DECIMAL(15, 2) NOT NULL,
    balance DECIMAL(15, 2) NOT NULL,
    account_id UUID NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_accounts_account_number ON accounts(account_number);
CREATE INDEX IF NOT EXISTS idx_accounts_customer_id ON accounts(customer_id);
CREATE INDEX IF NOT EXISTS idx_accounts_status ON accounts(status);
CREATE INDEX IF NOT EXISTS idx_movements_account_id ON movements(account_id);
CREATE INDEX IF NOT EXISTS idx_movements_date ON movements(date);
CREATE INDEX IF NOT EXISTS idx_movements_date_account ON movements(account_id, date);

-- =====================================================
-- VIEWS FOR REPORTING
-- =====================================================

-- View for account statement
CREATE OR REPLACE VIEW v_account_statement AS
SELECT 
    a.customer_id,
    a.customer_name,
    a.account_number,
    a.account_type,
    a.current_balance,
    m.movement_id,
    m.date,
    m.movement_type,
    m.value,
    m.balance
FROM accounts a
LEFT JOIN movements m ON a.account_id = m.account_id
ORDER BY a.customer_id, a.account_number, m.date DESC;

-- =====================================================
-- STORED PROCEDURES AND FUNCTIONS
-- =====================================================

-- Function to get account balance history
CREATE OR REPLACE FUNCTION get_account_balance_history(
    p_account_id UUID,
    p_start_date TIMESTAMP,
    p_end_date TIMESTAMP
)
RETURNS TABLE (
    movement_date TIMESTAMP,
    movement_type VARCHAR,
    value DECIMAL,
    balance DECIMAL
) AS $$
BEGIN
    RETURN QUERY
    SELECT m.date, m.movement_type, m.value, m.balance
    FROM movements m
    WHERE m.account_id = p_account_id
      AND m.date BETWEEN p_start_date AND p_end_date
    ORDER BY m.date DESC;
END;
$$ LANGUAGE plpgsql;
