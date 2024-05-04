CREATE OR REPLACE FUNCTION function_change_status_confirm_image_person() RETURNS TRIGGER AS
$$BEGIN

UPDATE "files" SET "is_confirmed" = FALSE WHERE ("uuid" = OLD.image) OR ("uuid" = OLD.copy_passport);

RETURN OLD;
END; $$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER "trigger_change_status_confirm_image_person"
    AFTER DELETE OR UPDATE ON "persons"
        FOR EACH ROW EXECUTE PROCEDURE function_change_status_confirm_image_person();


CREATE OR REPLACE FUNCTION function_change_status_confirm_logo_company() RETURNS TRIGGER AS
$$BEGIN

UPDATE "files" SET "is_confirmed" = FALSE WHERE "uuid" = OLD.logo;

RETURN OLD;
END; $$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER "trigger_change_status_confirm_logo_company"
    AFTER DELETE OR UPDATE ON "companies"
        FOR EACH ROW EXECUTE PROCEDURE function_change_status_confirm_logo_company();


CREATE OR REPLACE FUNCTION function_change_status_confirm_logo_entrepreneur() RETURNS TRIGGER AS
$$BEGIN

UPDATE "files" SET "is_confirmed" = FALSE WHERE "uuid" = OLD.logo;

RETURN OLD;
END; $$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER "trigger_chage_status_confirm_logo_entrepreneur"
    AFTER DELETE OR UPDATE ON "entrepreneurs"
        FOR EACH ROW EXECUTE PROCEDURE function_change_status_confirm_logo_entrepreneur();

