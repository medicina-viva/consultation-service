CREATE TABLE IF NOT EXISTS t_schedules
(
    id             SERIAL PRIMARY KEY NOT NULL,
    doctor_id      VARCHAR(100)       NOT NULL,
    available_date DATE               NOT NULL,
    start_time     TIME               NOT NULL,
    end_time       TIME               NOT NULL,
    is_active      BOOLEAN            NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP
);