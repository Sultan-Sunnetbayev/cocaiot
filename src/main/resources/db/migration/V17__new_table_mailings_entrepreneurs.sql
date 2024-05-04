CREATE TABLE IF NOT EXISTS "mailings_entrepreneurs"(
    "mailing_uuid" UUID  NOT NULL,
    "entrepreneur_uuid" UUID NOT NULL,

    CONSTRAINT "mailings_entrepreneurs_mailing_uuid_entrepreneur_uuid_unique"
        UNIQUE("mailing_uuid", "entrepreneur_uuid"),
    CONSTRAINT "mailings_entrepreneurs_mailing_uuid_fk"
        FOREIGN KEY("mailing_uuid")
            REFERENCES "mailings"("uuid")
                ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT "mailings_entrepreneurs_entrepreneur_uuid_fk"
        FOREIGN KEY("entrepreneur_uuid")
            REFERENCES "entrepreneurs"("uuid")
                ON UPDATE CASCADE ON DELETE CASCADE
);