CREATE OR REPLACE FUNCTION decrement_count_entrepreneur_type_activities() RETURNS TRIGGER AS
$$BEGIN
UPDATE type_activities SET amount_company = amount_company - 1 WHERE uuid = OLD.type_activity_uuid;

RETURN OLD;
END; $$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER "after_delete_update_companies_for_type_activity"
    AFTER DELETE OR UPDATE ON "entrepreneurs_type_activities"
        FOR EACH ROW EXECUTE PROCEDURE decrement_count_entrepreneur_type_activities();
