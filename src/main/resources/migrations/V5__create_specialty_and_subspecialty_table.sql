CREATE TABLE IF NOT EXISTS t_specialties
(
    id                  SERIAL PRIMARY KEY                  NOT NULL,
    name                VARCHAR(100)                        NOT NULL,
    description         TEXT                                NULL,
    is_active           BOOLEAN                             NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP                           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP                           NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS t_subspecialties
(
    id                  SERIAL PRIMARY KEY                  NOT NULL,
    name                VARCHAR(100)                        NOT NULL,
    description         TEXT                                NULL,
    is_active           BOOLEAN                             NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP                           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP                           NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE t_specialty_subspecialty (
    specialty_id BIGINT NOT NULL,
    subspecialty_id BIGINT NOT NULL,
    PRIMARY KEY (specialty_id, subspecialty_id),
    FOREIGN KEY (specialty_id) REFERENCES t_specialties(id) ON DELETE CASCADE,
    FOREIGN KEY (subspecialty_id) REFERENCES t_subspecialties(id) ON DELETE CASCADE
);
