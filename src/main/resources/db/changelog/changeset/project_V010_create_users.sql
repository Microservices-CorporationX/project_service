CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     username VARCHAR(64) NOT NULL UNIQUE,
    email VARCHAR(64) NOT NULL UNIQUE,
    phone VARCHAR(32) UNIQUE,
    password VARCHAR(128) NOT NULL,
    active BOOLEAN NOT NULL,
    about_me VARCHAR(4096),
    country_id BIGINT NOT NULL,
    city VARCHAR(64),
    experience INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    profile_pic_file_id VARCHAR(255),
    profile_pic_small_file_id VARCHAR(255)
    );