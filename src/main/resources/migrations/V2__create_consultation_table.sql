CREATE TABLE IF NOT EXISTS t_consultations
(
    id                   SERIAL PRIMARY KEY NOT NULL,
    patient_id           VARCHAR(32)        NOT NULL,
    doctor_id            VARCHAR(2)         NOT NULL,
    consultation_date    DATE               NULL,
    consultation_time    TIME               NULL,
    consultation_status  VARCHAR(15)        NOT NULL,
    is_tele_consultation BOOLEAN,
    is_active            BOOLEAN            NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP
);
