ALTER TABLE meet
    ADD COLUMN if not exists start_date_time TIMESTAMP,
    ADD COLUMN if not exists end_date_time TIMESTAMP;