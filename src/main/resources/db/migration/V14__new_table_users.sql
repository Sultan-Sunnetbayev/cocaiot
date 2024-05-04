CREATE TABLE IF NOT EXISTS "users"(
    "uuid" UUID NOT NULL DEFAULT UUID_GENERATE_V4(),
    "name" CHARACTER VARYING(50) NOT NULL,
    "surname" CHARACTER VARYING(65) NOT NULL,
    "patronomic_name" CHARACTER VARYING(75),
    "email" CHARACTER VARYING(100) NOT NULL,
    "password" CHARACTER VARYING(250) NOT NULL,
    "role_uuid" UUID NOT NULL,
    "created" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "users_uuid_pk"
        PRIMARY KEY("uuid"),
    CONSTRAINT "users_email_unique"
        UNIQUE("email"),
    CONSTRAINT "users_role_uuid_fk"
        FOREIGN KEY("role_uuid")
            REFERENCES "roles"("uuid")
                ON UPDATE CASCADE
);