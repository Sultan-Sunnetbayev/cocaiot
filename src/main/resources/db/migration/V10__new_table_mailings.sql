DO $$
BEGIN
    IF NOT EXISTS(SELECT 1 FROM pg_type WHERE typname = 'type_mailing') THEN
        CREATE TYPE "type_mailing" AS ENUM(
                'SMS', 'EMAIL'
        );
    END IF;

END$$;

CREATE TABLE IF NOT EXISTS "mailings"(
    "uuid" UUID NOT NULL DEFAULT UUID_GENERATE_V4(),
    "name" CHARACTER VARYING (500) NOT NULL ,
    "text" TEXT,
    "file_uuid" UUID,
    "type_mailing" TYPE_MAILING NOT NULL,
    "created" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "mailings_uuid_pk"
        PRIMARY KEY("uuid"),
    CONSTRAINT "mailings_file_uuid_unique"
        UNIQUE("file_uuid"),
    CONSTRAINT "mailings_file_uuid_fk"
        FOREIGN KEY("file_uuid")
            REFERENCES "files"("uuid")
                ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS "mailings_companies"(
    "uuid" UUID NOT NULL DEFAULT UUID_GENERATE_V4(),
    "mailing_uuid" UUID NOT NULL,
    "company_uuid" UUID NOT NULL,
    "created" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "mailings_companies_uuid_pk"
        PRIMARY KEY("uuid"),
    CONSTRAINT "mailings_companies_company_uuid_mailing_uuid_unique"
        UNIQUE("company_uuid", "mailing_uuid"),
    CONSTRAINT "mailings_companies_mailing_uuid_fk"
        FOREIGN KEY("mailing_uuid")
            REFERENCES "mailings"("uuid")
                ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT "mailings_companies_company_uuid_fk"
        FOREIGN KEY("company_uuid")
            REFERENCES "companies"("uuid")
                ON UPDATE CASCADE ON DELETE CASCADE
);
