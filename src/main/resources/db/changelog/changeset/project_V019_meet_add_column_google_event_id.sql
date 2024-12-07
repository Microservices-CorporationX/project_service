ALTER TABLE meet
    ADD COLUMN if not exists google_calendar_event_link VARCHAR(255) UNIQUE,
    ADD COLUMN if not exists google_event_id VARCHAR(255) UNIQUE;
