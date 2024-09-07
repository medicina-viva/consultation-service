CREATE OR REPLACE FUNCTION fn_add_consultation_history()
    RETURNS TRIGGER AS
$$
BEGIN
    IF NEW.consultation_status = 'PENDING' THEN
        INSERT INTO t_consultation_histories (consultation_id, user_id, description, consultation_status)
        VALUES (NEW.id, NEW.patient_id, 'Patient schedule a consultation.', NEW.consultation_status);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_after_consultation_insert
    AFTER INSERT
    ON t_consultations
    FOR EACH ROW
EXECUTE FUNCTION fn_add_consultation_history();