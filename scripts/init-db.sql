-- FX Deals Database Initialization Script
-- This script sets up the database schema and inserts sample data

-- Set timezone
SET timezone = 'UTC';

-- Create extension for UUID generation (if needed in future)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Sample data will be inserted by the application on startup
-- The application uses Hibernate to create the schema automatically

-- Additional indexes for performance (if not created by Hibernate)
-- These will be created by the application, but listed here for reference:
-- CREATE INDEX IF NOT EXISTS idx_deal_unique_id ON deals(deal_unique_id);
-- CREATE INDEX IF NOT EXISTS idx_deal_timestamp ON deals(deal_timestamp);
-- CREATE INDEX IF NOT EXISTS idx_deal_currencies ON deals(from_currency, to_currency);

-- Log successful initialization
SELECT 'FX Deals database initialized successfully' AS status; 