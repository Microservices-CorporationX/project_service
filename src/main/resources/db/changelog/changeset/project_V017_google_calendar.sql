CREATE TABLE IF NOT EXISTS google_calendar_token (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGSERIAL UNIQUE NOT NULL,
    token VARCHAR(512) NOT NULL,
    CONSTRAINT fk_google_calendar_token_user FOREIGN KEY (user_id) REFERENCES users (id)
);

ALTER TABLE project ADD COLUMN IF NOT EXISTS calendar_id BIGSERIAL;