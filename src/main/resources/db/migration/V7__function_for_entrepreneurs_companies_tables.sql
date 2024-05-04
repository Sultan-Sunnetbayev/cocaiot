CREATE OR REPLACE FUNCTION add_or_update_entrepreneur_or_company_to_cocaiot_member() RETURNS TRIGGER AS
$$BEGIN
    UPDATE "entrepreneurs" SET "is_cocaiot_member" = TRUE WHERE "uuid" = NEW.entrepreneur_uuid;

    UPDATE "companies" SET "is_cocaiot_member" = TRUE WHERE "uuid" = NEW.company_uuid;

    UPDATE "files" SET "is_confirmed" = TRUE WHERE "uuid" = NEW.file_uuid;

    RETURN NEW;
END; $$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER "after_update_or_insert_cocaiot_members"
    AFTER INSERT OR UPDATE ON "cocaiot_members"
        FOR EACH ROW EXECUTE PROCEDURE add_or_update_entrepreneur_or_company_to_cocaiot_member();

CREATE OR REPLACE FUNCTION delete_or_update_entrepreneur_or_company_in_cocaiot_member() RETURNS TRIGGER AS
$$BEGIN
    UPDATE "entrepreneurs" SET "is_cocaiot_member" =  FALSE WHERE "uuid" = OLD.entrepreneur_uuid;

    UPDATE "companies" SET "is_cocaiot_member" = FALSE WHERE "uuid" = OLD.company_uuid;

    UPDATE "files" SET "is_confirmed" = FALSE WHERE "uuid" = OLD.file_uuid;

    RETURN OLD;
END; $$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER "after_delete_or_update_cocaiot_members"
    AFTER DELETE OR UPDATE ON "cocaiot_members"
        FOR EACH ROW EXECUTE PROCEDURE delete_or_update_entrepreneur_or_company_in_cocaiot_member();

