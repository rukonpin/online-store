DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS cart_items CASCADE;
DROP TABLE IF EXISTS carts CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS products CASCADE;

CREATE TABLE IF NOT EXISTS products (
    product_uuid UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS users (
    user_uuid UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS carts (
    cart_uuid UUID PRIMARY KEY,
    user_uuid UUID UNIQUE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_carts_user FOREIGN KEY (user_uuid)
        REFERENCES users (user_uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS cart_items (
    item_uuid UUID PRIMARY KEY,
    cart_uuid UUID NOT NULL,
    product_uuid UUID NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity >= 1),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_uuid)
        REFERENCES carts (cart_uuid) ON DELETE CASCADE,

    CONSTRAINT fk_cart_items_product FOREIGN KEY (product_uuid)
        REFERENCES products (product_uuid) ON DELETE CASCADE,

    CONSTRAINT uq_cart_product UNIQUE (cart_uuid, product_uuid)
);

CREATE TABLE IF NOT EXISTS orders (
    order_uuid UUID PRIMARY KEY,
    user_uuid UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_orders_user FOREIGN KEY (user_uuid)
        REFERENCES users (user_uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS order_items (
    order_item_uuid UUID PRIMARY KEY,
    order_uuid UUID NOT NULL,
    product_uuid UUID NOT NULL,
    price_at_purchase DECIMAL(10, 2) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity >= 1),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_order_items_orders FOREIGN KEY (order_uuid)
        REFERENCES orders (order_uuid) ON DELETE CASCADE,

    CONSTRAINT fk_order_items_product FOREIGN KEY (product_uuid)
        REFERENCES products (product_uuid) ON DELETE RESTRICT
);