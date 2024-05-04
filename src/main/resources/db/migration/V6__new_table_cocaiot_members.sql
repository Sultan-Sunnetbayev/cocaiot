CREATE TABLE IF NOT EXISTS "cocaiot_members"(
    "uuid" UUID NOT NULL DEFAULT UUID_GENERATE_V4(),
    "company_uuid" UUID,
    "entrepreneur_uuid" UUID,
    "file_uuid" UUID,
    "status_payment" STATUS_PAYMENT NOT NULL,
    "initial_date" TIMESTAMP,
    "final_date" TIMESTAMP,
    "created" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "cocaiot_members_uuid_pk"
        PRIMARY KEY("uuid"),
    CONSTRAINT "cocaiot_members_company_uuid_unique"
        UNIQUE("company_uuid"),
    CONSTRAINT "cocaiot_members_entrepreneur_uuid_unique"
        UNIQUE("entrepreneur_uuid"),
    CONSTRAINT "cocaiot_members_file_uuid_unique"
        UNIQUE("file_uuid"),
    CONSTRAINT "cocaiot_members_initial_date_final_date_check"
        CHECK("initial_date" <= "final_date"),
    CONSTRAINT "cocaiot_members_company_uuid_fk"
        FOREIGN KEY("company_uuid")
            REFERENCES "companies"("uuid")
                ON UPDATE CASCADE,
    CONSTRAINT "cocaiot_members_entrepreneur_uuid_fk"
        FOREIGN KEY("entrepreneur_uuid")
            REFERENCES "entrepreneurs"("uuid")
                ON UPDATE CASCADE,
    CONSTRAINT "cocaiot_members_file_uuid_fk"
        FOREIGN KEY("file_uuid")
            REFERENCES "files"("uuid")
                ON UPDATE CASCADE
);