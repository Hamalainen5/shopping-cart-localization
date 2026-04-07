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
                  -- English
                  SELECT 'prompt.item.count' AS `key`, 'Enter the number of items to purchase:' AS value, 'en' AS language
                  UNION ALL SELECT 'prompt.item.price', 'Enter the price for item', 'en'
                  UNION ALL SELECT 'prompt.item.quantity', 'Enter the quantity for item', 'en'
                  UNION ALL SELECT 'label.item.total', 'Item total', 'en'
                  UNION ALL SELECT 'label.cart.total', 'Total cost:', 'en'
                  UNION ALL SELECT 'label.language', 'Language:', 'en'
                  UNION ALL SELECT 'label.item', 'Item', 'en'
                  UNION ALL SELECT 'button.generate', 'Generate Rows', 'en'
                  UNION ALL SELECT 'button.calculate', 'Calculate', 'en'
                  UNION ALL SELECT 'button.save', 'Save', 'en'
                  UNION ALL SELECT 'button.clear', 'Clear', 'en'
                  UNION ALL SELECT 'status.calculated', 'Cart calculated successfully.', 'en'
                  UNION ALL SELECT 'status.saved', 'Cart saved to database successfully.', 'en'
                  UNION ALL SELECT 'status.error', 'Error:', 'en'
                  UNION ALL SELECT 'title.app', 'Shopping Cart', 'en'

                  -- Finnish
                  UNION ALL SELECT 'prompt.item.count', 'Syötä ostettavien tuotteiden määrä:', 'fi'
                  UNION ALL SELECT 'prompt.item.price', 'Syötä tuotteen hinta', 'fi'
                  UNION ALL SELECT 'prompt.item.quantity', 'Syötä tuotteen määrä', 'fi'
                  UNION ALL SELECT 'label.item.total', 'Tuotteen kokonaishinta', 'fi'
                  UNION ALL SELECT 'label.cart.total', 'Kokonaishinta:', 'fi'
                  UNION ALL SELECT 'label.language', 'Kieli:', 'fi'
                  UNION ALL SELECT 'label.item', 'Tuote', 'fi'
                  UNION ALL SELECT 'button.generate', 'Luo rivit', 'fi'
                  UNION ALL SELECT 'button.calculate', 'Laske', 'fi'
                  UNION ALL SELECT 'button.save', 'Tallenna', 'fi'
                  UNION ALL SELECT 'button.clear', 'Tyhjennä', 'fi'
                  UNION ALL SELECT 'status.calculated', 'Ostoskorin summa laskettiin onnistuneesti.', 'fi'
                  UNION ALL SELECT 'status.saved', 'Ostoskori tallennettiin tietokantaan onnistuneesti.', 'fi'
                  UNION ALL SELECT 'status.error', 'Virhe:', 'fi'
                  UNION ALL SELECT 'title.app', 'Ostoskori', 'fi'

                  -- Swedish
                  UNION ALL SELECT 'prompt.item.count', 'Ange antalet varor att köpa:', 'sv'
                  UNION ALL SELECT 'prompt.item.price', 'Ange priset för varan', 'sv'
                  UNION ALL SELECT 'prompt.item.quantity', 'Ange mängden varor', 'sv'
                  UNION ALL SELECT 'label.item.total', 'Varans totalkostnad', 'sv'
                  UNION ALL SELECT 'label.cart.total', 'Total kostnad:', 'sv'
                  UNION ALL SELECT 'label.language', 'Språk:', 'sv'
                  UNION ALL SELECT 'label.item', 'Artikel', 'sv'
                  UNION ALL SELECT 'button.generate', 'Skapa rader', 'sv'
                  UNION ALL SELECT 'button.calculate', 'Beräkna', 'sv'
                  UNION ALL SELECT 'button.save', 'Spara', 'sv'
                  UNION ALL SELECT 'button.clear', 'Rensa', 'sv'
                  UNION ALL SELECT 'status.calculated', 'Varukorgens summa har beräknats.', 'sv'
                  UNION ALL SELECT 'status.saved', 'Varukorgen har sparats i databasen.', 'sv'
                  UNION ALL SELECT 'status.error', 'Fel:', 'sv'
                  UNION ALL SELECT 'title.app', 'Varukorg', 'sv'

                  -- Japanese
                  UNION ALL SELECT 'prompt.item.count', '購入する商品の数を入力してください:', 'ja'
                  UNION ALL SELECT 'prompt.item.price', '商品の価格を入力してください', 'ja'
                  UNION ALL SELECT 'prompt.item.quantity', '商品の数量を入力してください', 'ja'
                  UNION ALL SELECT 'label.item.total', '商品の合計金額', 'ja'
                  UNION ALL SELECT 'label.cart.total', '合計金額:', 'ja'
                  UNION ALL SELECT 'label.language', '言語:', 'ja'
                  UNION ALL SELECT 'label.item', '商品', 'ja'
                  UNION ALL SELECT 'button.generate', '行を作成', 'ja'
                  UNION ALL SELECT 'button.calculate', '計算', 'ja'
                  UNION ALL SELECT 'button.save', '保存', 'ja'
                  UNION ALL SELECT 'button.clear', 'クリア', 'ja'
                  UNION ALL SELECT 'status.calculated', 'カートの合計が正常に計算されました。', 'ja'
                  UNION ALL SELECT 'status.saved', 'カートがデータベースに正常に保存されました。', 'ja'
                  UNION ALL SELECT 'status.error', 'エラー:', 'ja'
                  UNION ALL SELECT 'title.app', 'ショッピングカート', 'ja'
              ) AS new_values
WHERE NOT EXISTS (
    SELECT 1
    FROM localization_strings ls
    WHERE ls.`key` = new_values.`key`
      AND ls.language = new_values.language
);