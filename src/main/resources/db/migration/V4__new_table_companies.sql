CREATE TABLE IF NOT EXISTS "companies"(
    "uuid" UUID NOT NULL DEFAULT UUID_GENERATE_V4(),
    "name" CHARACTER VARYING(350) NOT NULL,
    "full_address" CHARACTER VARYING (750),
    "phone_number" CHARACTER VARYING(40),
    "email" CHARACTER VARYING(75),
    "fax" CHARACTER VARYING (50),
    "web_site" CHARACTER VARYING(250),
    "country_uuid" UUID,
    "region_uuid" UUID,
    "director_uuid" UUID,
    "organization_uuid" UUID,
    "type_ownership" TYPE_OWNERSHIP NOT NULL,
    "membership_application_uuid" UUID,
    "extract_from_usreo_uuid" UUID,
    "charter_of_the_enterprise_uuid" UUID,
    "certificate_of_foreign_economic_relations_uuid" UUID,
    "certificate_of_state_registration_uuid" UUID,
    "payment_of_the_entrance_membership_fee_uuid" UUID,
    "is_cocaiot_member" BOOLEAN NOT NULL DEFAULT FALSE,
    "created" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "companies_uuid_pk"
        PRIMARY KEY("uuid"),
--     CONSTRAINT "companies_name_type_ownership_uuid_unique"
--         UNIQUE("name", "type_ownership"),
--     CONSTRAINT "companies_email_type_ownership_uuid_unique"
--         UNIQUE("email", "type_ownership"),
--     CONSTRAINT "companies_fax_type_ownership_uuid_unique"
--         UNIQUE("fax", "type_ownership"),
--     CONSTRAINT "companies_web_site_type_ownership_uuid_unique"
--         UNIQUE("web_site", "type_ownership"),
    CONSTRAINT "companies_country_uuid_fk"
        FOREIGN KEY("country_uuid")
            REFERENCES "countries"("uuid")
                ON UPDATE CASCADE,
    CONSTRAINT "companies_region_uuid_fk"
        FOREIGN KEY("region_uuid")
            REFERENCES "regions"("uuid")
                ON UPDATE CASCADE,
    CONSTRAINT "companies_director_uuid_fk"
        FOREIGN KEY("director_uuid")
            REFERENCES "persons"("uuid")
                ON UPDATE CASCADE,
    CONSTRAINT "companies_organization_uuid_fk"
        FOREIGN KEY("organization_uuid")
            REFERENCES "organizations"("uuid")
                ON UPDATE CASCADE,
    CONSTRAINT "companies_membership_application_uuid_fk"
        FOREIGN KEY("membership_application_uuid")
            REFERENCES "files"("uuid")
                ON UPDATE CASCADE,
    CONSTRAINT "companies_extract_from_usreo_uuid_fk"
        FOREIGN KEY("extract_from_usreo_uuid")
            REFERENCES "files"("uuid")
                ON UPDATE CASCADE,
    CONSTRAINT "companies_charter_of_the_enterprise_uuid_fk"
        FOREIGN KEY("charter_of_the_enterprise_uuid")
            REFERENCES "files"("uuid")
                ON UPDATE CASCADE,
    CONSTRAINT "companies_certificate_of_foreign_economic_relations_uuid_fk"
        FOREIGN KEY("certificate_of_foreign_economic_relations_uuid")
            REFERENCES "files"("uuid")
                ON UPDATE CASCADE,
    CONSTRAINT "companies_certificate_of_state_registration_uuid_fk"
        FOREIGN KEY("certificate_of_state_registration_uuid")
            REFERENCES "files"("uuid")
                ON UPDATE CASCADE,
    CONSTRAINT "companies_payment_of_the_entrance_membership_fee_uuid_fk"
        FOREIGN KEY("payment_of_the_entrance_membership_fee_uuid")
            REFERENCES "files"("uuid")
                ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS "founders_companies"(
    "company_uuid" UUID NOT NULL,
    "founder_uuid" UUID NOT NULL,

    CONSTRAINT "founders_companies_company_uuid_founder_uuid_unique"
        UNIQUE("company_uuid", "founder_uuid"),
    CONSTRAINT "founders_companies_company_uuid_fk"
        FOREIGN KEY("company_uuid")
            REFERENCES "companies"("uuid")
                ON UPDATE CASCADE ON DELETE CASCADE ,
    CONSTRAINT "founders_companies_founder_uuid_fk"
        FOREIGN KEY("founder_uuid")
            REFERENCES "persons"("uuid")
                ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "type_activities_companies"(
    "company_uuid" UUID NOT NULL,
    "type_activity_uuid" UUID NOT NULL,

    CONSTRAINT "type_activities_companies_company_uuid_type_activity_uuid_unique"
        UNIQUE("company_uuid", "type_activity_uuid"),
    CONSTRAINT "type_activities_companies_company_uuid_fk"
        FOREIGN KEY("company_uuid")
            REFERENCES "companies"("uuid")
                ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT "type_activities_companies_type_activity_uuid_fk"
        FOREIGN KEY("type_activity_uuid")
            REFERENCES "type_activities"("uuid")
                ON UPDATE CASCADE ON DELETE CASCADE
);