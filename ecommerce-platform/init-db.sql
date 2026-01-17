-- Create databases for each service (if you want separate DBs per service)
-- For simplicity, we'll use one database with different schemas

CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS product;
CREATE SCHEMA IF NOT EXISTS orders;

-- Grant permissions
GRANT ALL PRIVILEGES ON SCHEMA auth TO ecommerce_user;
GRANT ALL PRIVILEGES ON SCHEMA product TO ecommerce_user;
GRANT ALL PRIVILEGES ON SCHEMA orders TO ecommerce_user;

-- Set default schema
ALTER DATABASE ecommerce_db SET search_path TO public, auth, product, orders;
