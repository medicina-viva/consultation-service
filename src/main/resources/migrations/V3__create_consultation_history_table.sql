CREATE TABLE IF NOT EXISTS t_consultation_histories
(
    id                  SERIAL PRIMARY KEY                  NOT NULL,
    consultation_id     INT REFERENCES t_consultations (id) NOT NULL,
    user_id             VARCHAR(32)                         NOT NULL,
    description         TEXT                                NULL,
    consultation_status VARCHAR(15)                         NOT NULL,
    is_active           BOOLEAN                             NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP                           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP                           NOT NULL DEFAULT CURRENT_TIMESTAMP
);