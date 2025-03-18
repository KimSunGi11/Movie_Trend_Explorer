CREATE DATABASE IF NOT EXISTS mte CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE mte;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

INSERT INTO users (email, name, password, username) 
VALUES ('test@example.com', '테스트', '$2a$10$8H4.NV3Fw9YGBdHHgx4XeukqXdKUASx3S9h.62MwE93GjPXpGXHLm', 'testuser'); 