CREATE TABLE  IF NOT EXISTS  t_consultations (
    id SERIAL PRIMARY KEY NOT NULL,
    patient_id VARCHAR(32) NOT NULL,
    doctor_id  VARCHAR(2) NOT NULL ,
    consultation_date DATE NULL,
    consultation_time TIME NULL,
    consultation_status VARCHAR(15) NOT NULL,
    is_tele_consultation BOOLEAN
);

CREATE TABLE IF NOT EXISTS t_consultation_histories (
    id  SERIAL PRIMARY KEY NOT NULL,
    consultation_id INT REFERENCES t_consultations(id) NOT NULL,
    user_id VARCHAR(32) NOT NULL,
    description TEXT NULL,
    consultation_status VARCHAR(15) NOT NULL
);