CREATE TABLE IF NOT EXISTS "entrepreneurs"(
    "uuid" UUID NOT NULL DEFAULT UUID_GENERATE_V4(),
    "person_uuid" UUID NOT NULL ,
    "membership_application_uuid" UUID,
    "patent_certifying_payment_uuid" UUID,
    "entrepreneur_statistical_codes_uuid" UUID,
    "certificate_of_foreign_economic_relations_uuid" UUID,
    "registration_certificate_of_entrepreneur_uuid" UUID,
    "certificate_of_tax_registration_uuid" UUID,
    "is_cocaiot_member" BOOLEAN NOT NULL DEFAULT FALSE,
    "created" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "entrepreneurs_uuid_pk"
        PRIMARY KEY("uuid"),
    CONSTRAINT "entrepreneurs_person_uuid_unique"
        UNIQUE("person_uuid"),
    CONSTRAINT "entrepreneurs_person_uuid_fk"
        FOREIGN KEY ("person_uuid")
            REFERENCES "persons"("uuid")
                ON UPDATE CASCADE,
    CONSTRAINT "entrepreneurs_membership_application_uuid_fk"
        FOREIGN KEY("membership_application_uuid")
            REFERENCES "files"("uuid")
                ON UPDATE CASCADE,
    CONSTRAINT "entrepreneurs_patent_certifying_payment_uuid_fk"
        FOREIGN KEY("patent_certifying_payment_uuid")
            REFERENCES "files"("uuid")
                ON UPDATE CASCADE,
    CONSTRAINT "entrepreneurs_entrepreneur_statistical_codes_uuid_fk"
        FOREIGN KEY("entrepreneur_statistical_codes_uuid")
            REFERENCES "files"("uuid")
                ON UPDATE CASCADE,
    CONSTRAINT "entrepreneurs_certificate_of_foreign_economic_relations_uuid_fk"
        FOREIGN KEY("certificate_of_foreign_economic_relations_uuid")
            REFERENCES "files"("uuid")
                ON UPDATE CASCADE,
    CONSTRAINT "entrepreneurs_registration_certificate_of_entrepreneur_uuid_fk"
        FOREIGN KEY("registration_certificate_of_entrepreneur_uuid")
            REFERENCES "files"("uuid")
                ON UPDATE CASCADE,
    CONSTRAINT "entrepreneurs_certificate_of_tax_registration_uuid_fk"
        FOREIGN KEY("certificate_of_tax_registration_uuid")
            REFERENCES "files"("uuid")
                ON UPDATE CASCADE
);