DELETE FROM item_reservation;

CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- ALTER TABLE product
-- ADD COLUMN real_price INT;
--
-- UPDATE product
-- SET real_price = COALESCE(discount_price, orig_price);