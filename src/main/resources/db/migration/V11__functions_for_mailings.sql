CREATE OR REPLACE FUNCTION change_status_confirm_for_mailing() RETURNS TRIGGER AS $$
BEGIN
    UPDATE "files" SET "is_confirmed" = FALSE
        WHERE "uuid" = OLD.file_uuid;

    RETURN OLD;
END; $$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER "after_delete_mailing"
    AFTER DELETE ON "mailings"
        FOR EACH ROW EXECUTE PROCEDURE change_status_confirm_for_mailing();
