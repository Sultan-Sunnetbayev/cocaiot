CREATE OR REPLACE FUNCTION raise_exception_for_type_activities() RETURNS TRIGGER AS $$
DECLARE
    can_be_delete BOOLEAN;
BEGIN
    SELECT "amount_company">0 FROM "type_activities" INTO can_be_delete WHERE uuid = OLD.uuid;

    IF (can_be_delete) THEN
        RAISE EXCEPTION 'can not delete the type activity';
    END IF;

    RETURN OLD;
END; $$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER "can_be_delete_type_activity"
    BEFORE DELETE ON "type_activities"
        FOR EACH ROW EXECUTE PROCEDURE raise_exception_for_type_activities();


CREATE OR REPLACE FUNCTION raise_exception_for_organizations() RETURNS TRIGGER AS $$
DECLARE
    can_be_delete BOOLEAN;
BEGIN
    SELECT "amount_company">0 FROM "organizations" INTO can_be_delete WHERE uuid = OLD.uuid;

    IF (can_be_delete) THEN
        RAISE EXCEPTION 'can not delete the organization';
    END IF;

    RETURN OLD;
END; $$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER "can_be_delete_organization"
    BEFORE DELETE ON "organizations"
        FOR EACH ROW EXECUTE PROCEDURE raise_exception_for_organizations();


CREATE OR REPLACE FUNCTION raise_exception_for_countries() RETURNS TRIGGER AS $$
DECLARE
    can_be_delete BOOLEAN;
BEGIN
    SELECT ("amount_company">0) OR (LOWER(name) = 'türkmenistan') FROM "countries" INTO can_be_delete WHERE uuid = OLD.uuid;

    IF (can_be_delete) OR (SELECT EXISTS(SELECT country_uuid FROM "persons" WHERE country_uuid = OLD.uuid)) THEN
        RAISE EXCEPTION 'can not delete the country';
    END IF;

    RETURN OLD;
END; $$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER "can_be_delete_country"
    BEFORE DELETE ON "countries"
        FOR EACH ROW EXECUTE PROCEDURE raise_exception_for_countries();


CREATE OR REPLACE FUNCTION raise_exception_for_regions() RETURNS TRIGGER AS $$
DECLARE
    can_be_delete BOOLEAN;
BEGIN
    SELECT "amount_company">0 FROM "regions" INTO can_be_delete WHERE uuid = OLD.uuid;

    IF (can_be_delete) OR (SELECT EXISTS(SELECT region_uuid FROM "persons" WHERE region_uuid = OLD.uuid)) THEN
        RAISE EXCEPTION 'can not delete the region';
    END IF;

    RETURN OLD;
END; $$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER "can_be_delete_region"
    BEFORE DELETE ON "regions"
        FOR EACH ROW EXECUTE PROCEDURE raise_exception_for_regions();


CREATE OR REPLACE FUNCTION raise_exception_for_persons() RETURNS TRIGGER AS $$
DECLARE
    can_be_delete BOOLEAN;
BEGIN
    SELECT is_entrepreneur FROM "persons" INTO can_be_delete WHERE uuid = OLD.uuid;

    IF (can_be_delete) OR (SELECT EXISTS(SELECT director_uuid FROM "companies" WHERE director_uuid = OLD.uuid)) OR
                    (SELECT EXISTS(SELECT founder_uuid FROM "founders_companies" WHERE "founder_uuid" = OLD.uuid)) THEN
        RAISE EXCEPTION 'can not delete the person';
    END IF;

    RETURN OLD;
END; $$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER "can_be_delete_person"
    BEFORE DELETE ON "persons"
        FOR EACH ROW EXECUTE PROCEDURE raise_exception_for_persons();


CREATE OR REPLACE FUNCTION raise_exception_for_entrepreneurs() RETURNS TRIGGER AS $$
DECLARE
    can_be_delete BOOLEAN;
BEGIN
    SELECT is_cocaiot_member FROM "entrepreneurs" INTO can_be_delete WHERE uuid = OLD.uuid;

    IF can_be_delete THEN
        RAISE EXCEPTION 'can not delete the entrepreneur';
    END IF;

    RETURN OLD;
END; $$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER "can_be_delete_entrepreneur"
    BEFORE DELETE ON "entrepreneurs"
        FOR EACH ROW EXECUTE PROCEDURE raise_exception_for_entrepreneurs();


CREATE OR REPLACE FUNCTION raise_exception_for_companies() RETURNS TRIGGER AS $$
DECLARE
    can_be_delete BOOLEAN;
BEGIN
    SELECT is_cocaiot_member FROM "companies" INTO can_be_delete WHERE uuid = OLD.uuid;

    IF can_be_delete THEN
        RAISE EXCEPTION 'can not delete the company';
    END IF;

    RETURN OLD;
END; $$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER "can_be_delete_company"
    BEFORE DELETE ON "companies"
        FOR EACH ROW EXECUTE PROCEDURE raise_exception_for_companies();
