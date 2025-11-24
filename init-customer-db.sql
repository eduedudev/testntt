-- =====================================================
-- Customer Database Schema
-- Banking System - NTT Data Technical Test
-- =====================================================

-- Create Customers Table
CREATE TABLE IF NOT EXISTS customers (
    customer_id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    gender VARCHAR(50) NOT NULL,
    identification VARCHAR(50) NOT NULL UNIQUE,
    address VARCHAR(500) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    password VARCHAR(255) NOT NULL,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_customers_identification ON customers(identification);
CREATE INDEX IF NOT EXISTS idx_customers_status ON customers(status);
