CREATE DATABASE IF NOT EXISTS shopping_cart_localization
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE shopping_cart_localization;

CREATE TABLE IF NOT EXISTS cart_records (
                                            id INT AUTO_INCREMENT PRIMARY KEY,
                                            total_items INT NOT NULL,
                                            total_cost DOUBLE NOT NULL,
                                            language VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS cart_items (
                                          id INT AUTO_INCREMENT PRIMARY KEY,
                                          cart_record_id INT,
                                          item_number INT NOT NULL,
                                          price DOUBLE NOT NULL,
                                          quantity INT NOT NULL,
                                          subtotal DOUBLE NOT NULL,
                                          FOREIGN KEY (cart_record_id) REFERENCES cart_records(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS localization_strings (
                                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                                    `key` VARCHAR(100) NOT NULL,
    value VARCHAR(255) NOT NULL,
    language VARCHAR(10) NOT NULL,
    CONSTRAINT uq_localization_key_language UNIQUE (`key`, language)
    );

INSERT INTO localization_strings (`key`, value, language)
SELECT * FROM (
                  SELECT 'prompt.item.count' AS `key`, 'Enter the number of items to purchase:' AS value, 'en' AS language
                  UNION ALL SELECT 'prompt.item.price', 'Enter the price for item', 'en'
                  UNION ALL SELECT 'prompt.item.quantity', 'Enter the quantity for item', 'en'
                  UNION ALL SELECT 'label.item.total', 'Item total', 'en'
                  UNION ALL SELECT 'label.cart.total', 'Total cost:', 'en'

                  UNION ALL SELECT 'prompt.item.count', 'Syötä ostettavien tuotteiden määrä:', 'fi'
                  UNION ALL SELECT 'prompt.item.price', 'Syötä tuotteen hinta', 'fi'
                  UNION ALL SELECT 'prompt.item.quantity', 'Syötä tuotteen määrä', 'fi'
                  UNION ALL SELECT 'label.item.total', 'Tuotteen kokonaishinta', 'fi'
                  UNION ALL SELECT 'label.cart.total', 'Kokonaishinta:', 'fi'

                  UNION ALL SELECT 'prompt.item.count', 'Ange antalet varor att köpa:', 'sv'
                  UNION ALL SELECT 'prompt.item.price', 'Ange priset för varan', 'sv'
                  UNION ALL SELECT 'prompt.item.quantity', 'Ange mängden varor', 'sv'
                  UNION ALL SELECT 'label.item.total', 'Varans totalkostnad', 'sv'
                  UNION ALL SELECT 'label.cart.total', 'Total kostnad:', 'sv'

                  UNION ALL SELECT 'prompt.item.count', '購入する商品の数を入力してください:', 'ja'
                  UNION ALL SELECT 'prompt.item.price', '商品の価格を入力してください', 'ja'
                  UNION ALL SELECT 'prompt.item.quantity', '商品の数量を入力してください', 'ja'
                  UNION ALL SELECT 'label.item.total', '商品の合計金額', 'ja'
                  UNION ALL SELECT 'label.cart.total', '合計金額:', 'ja'
              ) AS new_values
WHERE NOT EXISTS (
    SELECT 1
    FROM localization_strings ls
    WHERE ls.`key` = new_values.`key`
      AND ls.language = new_values.language
);