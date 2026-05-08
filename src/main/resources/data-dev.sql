-- Вставляем товары только если таблица пустая
INSERT INTO products (product_uuid, name, description, image_url, price, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'Apple iPhone 17 Pro 512GB, Cosmic Orange',
    'Имеется недостаток товара: невозможно установить и использовать RuStore',
    '/images/Apple iPhone 17 Pro 512GB, Cosmic Orange.jpg',
    159990.00,
    NOW(),
    NOW();

INSERT INTO products (product_uuid, name, description, image_url, price, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'Apple iPhone 17 Pro Max 512GB, Deep Blue',
    NULL,
    '/images/Apple iPhone 17 Pro Max 512GB, Deep Blue.jpg',
    169990.00,
    NOW(),
    NOW();

INSERT INTO products (product_uuid, name, description, image_url, price, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'Apple iPhone 17 Pro Max 256GB, Deep Blue',
    NULL,
    '/images/Apple iPhone 17 Pro Max 256GB, Deep Blue.jpg',
    144990.00,
    NOW(),
    NOW();

INSERT INTO products (product_uuid, name, description, image_url, price, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'Apple iPhone 17 Pro 256GB, Deep Blue',
    'Имеется недостаток товара: невозможно установить и использовать RuStore',
    '/images/Apple iPhone 17 Pro 256GB, Deep Blue.jpg',
    132990.00,
    NOW(),
    NOW();

INSERT INTO products (product_uuid, name, description, image_url, price, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'Apple MacBook Neo 13" (A18 Pro, 6C СPU/5С GPU, 2026), 8 ГБ, 256 ГБ SSD, серебристый',
    NULL,
    '/images/Apple MacBook Neo 13, 8 ГБ, 256 ГБ, SSD, серебристый.jpg',
    79990.00,
    NOW(),
    NOW();

INSERT INTO products (product_uuid, name, description, image_url, price, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'Apple MacBook Neo 13" (A18 Pro, 6C СPU/5С GPU, 2026), 8 ГБ, 256 ГБ SSD, розовый румянец',
    NULL,
    '/images/Apple MacBook Neo 13 8 ГБ 256 ГБ SSD розовый румянец.jpg',
    79990.00,
    NOW(),
    NOW();

INSERT INTO products (product_uuid, name, description, image_url, price, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'Apple MacBook Pro 16" (M5 Pro 18C CPU, 20C GPU, 2026) 48 ГБ, 1 ТБ SSD, «черный космос»',
    NULL,
    '/images/Apple MacBook Pro 16 48ГБ 1ТБ SSD черный космос.jpg',
    399990.00,
    NOW(),
    NOW();

INSERT INTO products (product_uuid, name, description, image_url, price, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'Apple iMac 24" Retina 4,5K (M4 10C CPU, 10C GPU, 2024), 24 ГБ, 512 ГБ SSD, серебристый',
    NULL,
    '/images/Apple iMac 24 Retina 4-5K 24 ГБ 512 ГБ SSD серебристый.jpg',
    269990.00,
    NOW(),
    NOW();

INSERT INTO products (product_uuid, name, description, image_url, price, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'Apple iMac 24" Retina 4,5K (M4 8C CPU, 8C GPU, 2024), 16 ГБ, 256 ГБ SSD, розовый',
    NULL,
    '/images/Apple iMac 24 Retina 4-5K 16 ГБ 256 ГБ SSD розовый.jpg',
    179990.00,
    NOW(),
    NOW();

INSERT INTO products (product_uuid, name, description, image_url, price, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'Apple iMac 24" Retina 4,5K (M4 10C CPU, 10C GPU, 2024), 32 ГБ, 2 ТБ SSD, оранжевый',
    NULL,
    '/images/Apple iMac 24 Retina 4-5K 32 ГБ 2 ТБ SSD оранжевый.jpg',
    499990.00,
    NOW(),
    NOW();

INSERT INTO products (product_uuid, name, description, image_url, price, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'Умные часы Apple Watch Ultra 3, 49 мм, Natural Titanium Milanese Natural Loop M',
    NULL,
    '/images/Умные часы Apple Watch Ultra 3 49 мм Natural Titanium Milanese Natural Loop M.jpg',
    119990.00,
    NOW(),
    NOW();

INSERT INTO products (product_uuid, name, description, image_url, price, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'Умные часы Apple Watch Series 11, 46 mm, Rose Gold Aluminium Light Blush Sport Band M/L',
    NULL,
    '/images/Умные часы Apple Watch Series 11 46 mm Rose Gold Aluminium Light Blush Sport Band ML.jpg',
    44990.00,
    NOW(),
    NOW();

INSERT INTO products (product_uuid, name, description, image_url, price, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'Apple iPad Air (2026) M4 11" Wi-Fi + Cellular 128 ГБ, фиолетовый',
    NULL,
    '/images/Apple iPad Air M4 11 Wi-Fi + Cellular 128 ГБ фиолетовый.jpg',
    94990.00,
    NOW(),
    NOW();

INSERT INTO products (product_uuid, name, description, image_url, price, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'Apple iPad Air (2026) M4 11" Wi-Fi 256 ГБ, «сияющая звезда»',
    NULL,
    '/images/Apple iPad Air M4 11 Wi-Fi 256 ГБ сияющая звезда.jpg',
    89990.00,
    NOW(),
    NOW();

INSERT INTO products (product_uuid, name, description, image_url, price, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'Наушники Apple AirPods Max USB-C, синий',
    NULL,
    '/images/Наушники Apple AirPods Max USB-C синий.jpg',
    59990.00,
    NOW(),
    NOW();

INSERT INTO products (product_uuid, name, description, image_url, price, created_at, updated_at)
SELECT
    gen_random_uuid(),
    'Беспроводные наушники Apple AirPods Pro (3-го поколения), оранжевый',
    NULL,
    '/images/Беспроводные наушники Apple AirPods Pro оранжевый.jpg',
    45990.00,
    NOW(),
    NOW();