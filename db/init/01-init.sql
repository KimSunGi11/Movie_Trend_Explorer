CREATE DATABASE IF NOT EXISTS mte CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE mte;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL DEFAULT 'USER'
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 테스트 사용자 계정 생성 (비밀번호: 1234)
INSERT INTO users (email, name, password, username, role) 
VALUES ('test@example.com', '테스트', '$2y$05$M9IADdYJAKdUjhcBKNvor.VbJ54b.U0s55YNAkcVjQsTS1AzbmQ5i', 'testuser', 'USER');

-- 관리자 계정 생성 (비밀번호: 1234)
INSERT INTO users (username, name, email, password, role) 
VALUES ('admin', '관리자', 'admin@example.com', '$2y$05$M9IADdYJAKdUjhcBKNvor.VbJ54b.U0s55YNAkcVjQsTS1AzbmQ5i', 'ADMIN');

CREATE USER IF NOT EXISTS 'mte_user'@'%' IDENTIFIED BY 'mte_password';
GRANT ALL PRIVILEGES ON mte.* TO 'mte_user'@'%';
FLUSH PRIVILEGES;