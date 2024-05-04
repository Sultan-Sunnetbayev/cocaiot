CREATE OR REPLACE FUNCTION increment_count_company() RETURNS TRIGGER AS
$$BEGIN
    UPDATE "organizations" SET "amount_company" = "amount_company" + 1
        WHERE uuid = NEW.organization_uuid;
    UPDATE "countries" SET "amount_company" = "amount_company" + 1
        WHERE uuid = NEW.country_uuid;
    UPDATE "regions" SET "amount_company" = "amount_company" + 1
        WHERE uuid = NEW.region_uuid;

    RETURN NEW;
END; $$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER "after_insert_update_amount_companies"
    AFTER INSERT OR UPDATE ON "companies"
        FOR EACH ROW EXECUTE PROCEDURE increment_count_company();

CREATE OR REPLACE FUNCTION decrement_count_company() RETURNS TRIGGER AS
$$BEGIN
    UPDATE "organizations" SET "amount_company" = "amount_company" - 1
        WHERE uuid = OLD.organization_uuid;
    UPDATE "countries" SET "amount_company" = "amount_company" - 1
        WHERE uuid = OLD.country_uuid;
    UPDATE "regions" SET "amount_company" = "amount_company" - 1
        WHERE uuid = OLD.region_uuid;

    RETURN OLD;
END; $$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER "after_delete_update_amount_companies"
    AFTER DELETE OR UPDATE ON "companies"
        FOR EACH ROW EXECUTE PROCEDURE decrement_count_company();

CREATE OR REPLACE FUNCTION increment_count_company_for_type_activities() RETURNS TRIGGER AS
$$BEGIN
    UPDATE "type_activities" SET "amount_company" = "amount_company" + 1 WHERE uuid = NEW.type_activity_uuid;

    RETURN NEW;
END; $$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER "after_insert_update_companies_for_type_activity"
    AFTER INSERT OR UPDATE ON "type_activities_companies"
        FOR EACH ROW EXECUTE PROCEDURE increment_count_company_for_type_activities();

CREATE OR REPLACE FUNCTION decrement_count_company_for_type_activities() RETURNS TRIGGER AS
$$BEGIN
    UPDATE type_activities SET amount_company = amount_company - 1 WHERE uuid = OLD.type_activity_uuid;

    RETURN OLD;
END; $$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER "after_delete_update_companies_for_type_activity"
    AFTER DELETE OR UPDATE ON "type_activities_companies"
        FOR EACH ROW EXECUTE PROCEDURE decrement_count_company_for_type_activities();
