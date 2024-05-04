CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS "type_activities"(
    "uuid" UUID NOT NULL DEFAULT UUID_GENERATE_V4(),
    "name" CHARACTER VARYING(350) NOT NULL ,
    "amount_company" INTEGER NOT NULL DEFAULT 0,
    "created" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "type_acitivities_uuid_pk"
        PRIMARY KEY("uuid"),
    CONSTRAINT "type_activities_name_unique"
        UNIQUE("name")
);

CREATE TABLE IF NOT EXISTS "files"(
    "uuid" UUID NOT NULL DEFAULT UUID_GENERATE_V4() ,
    "name" CHARACTER VARYING(350) ,
    "path" CHARACTER VARYING(500) NOT NULL ,
    "extension" CHARACTER VARYING(150) ,
    "size" BIGINT NOT NULL DEFAULT 0,
    "is_confirmed" BOOLEAN NOT NULL DEFAULT FALSE ,
    "created" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP ,
    "updated" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP ,

    CONSTRAINT "files_uuid_pk"
        PRIMARY KEY("uuid"),
    CONSTRAINT "files_path_unique"
        UNIQUE("path")
);

CREATE TABLE IF NOT EXISTS "countries"(
    "uuid" UUID NOT NULL DEFAULT UUID_GENERATE_V4(),
    "name" CHARACTER VARYING(100) NOT NULL ,
    "amount_company" INTEGER NOT NULL DEFAULT 0,
    "created" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP ,
    "updated" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP ,

    CONSTRAINT "countries_uuid_pk"
        PRIMARY KEY("uuid"),
    CONSTRAINT "countries_name_unique"
        UNIQUE("name")
);

CREATE TABLE IF NOT EXISTS "regions"(
    "uuid" UUID NOT NULL DEFAULT UUID_GENERATE_V4(),
    "name" CHARACTER VARYING(150) NOT NULL ,
    "amount_company" INTEGER NOT NULL DEFAULT 0,
    "country_uuid" UUID NOT NULL ,
    "created" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "regions_uuid_pk"
        PRIMARY KEY("uuid"),
    CONSTRAINT "regions_name_country_uuid_unique"
        UNIQUE("name", "country_uuid"),
    CONSTRAINT "regions_country_uuid_fk"
        FOREIGN KEY("country_uuid")
            REFERENCES "countries"("uuid")
                ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "organizations"(
    "uuid" UUID NOT NULL DEFAULT UUID_GENERATE_V4(),
    "name" CHARACTER VARYING(400) NOT NULL ,
    "amount_company" INTEGER NOT NULL DEFAULT 0,
    "created" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "organizations_uuid_pk"
        PRIMARY KEY("uuid"),
    CONSTRAINT "organization_name_unique"
        UNIQUE ("name")
);

CREATE TABLE IF NOT EXISTS "persons"(
    "uuid" UUID NOT NULL DEFAULT UUID_GENERATE_V4(),
    "name" CHARACTER VARYING(50) NOT NULL,
    "surname" CHARACTER VARYING(65) NOT NULL,
    "patronomic_name" CHARACTER VARYING(75),
    "birth_place" CHARACTER VARYING(600),
    "birth_date" TIMESTAMP,
    "country_uuid" UUID,
    "region_uuid" UUID,
    "full_address_of_residence" CHARACTER VARYING (750),
    "phone_number" CHARACTER VARYING(40),
    "fax" CHARACTER VARYING (50),
    "email" CHARACTER VARYING(75),
    "education" CHARACTER VARYING(750),
    "experience" CHARACTER VARYING(750),
    "knowledge_of_languages" CHARACTER VARYING(300),
    "is_entrepreneur" BOOLEAN NOT NULL DEFAULT FALSE,
    "created" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "persons_uuid_pk"
        PRIMARY KEY("uuid"),
--     CONSTRAINT "persons_email_unique"
--         UNIQUE(email),
    CONSTRAINT "persons_country_uuid_fk"
        FOREIGN KEY("country_uuid")
            REFERENCES "countries"("uuid")
                ON UPDATE CASCADE,
    CONSTRAINT "persons_region_uuid_fk"
        FOREIGN KEY("region_uuid")
            REFERENCES "regions"("uuid")
                ON UPDATE CASCADE
);
