CREATE TABLE IF NOT EXISTS "roles"(
    "uuid" UUID NOT NULL DEFAULT UUID_GENERATE_V4(),
    "name" CHARACTER VARYING(250) NOT NULL,
    "created" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "roles_uuid_pk"
        PRIMARY KEY("uuid"),
    CONSTRAINT "roles_name_unique"
        UNIQUE("name")
);

CREATE TABLE IF NOT EXISTS "categories"(
    "uuid" UUID NOT NULL DEFAULT UUID_GENERATE_V4(),
    "name" CHARACTER VARYING(250) NOT NULL,
    "created" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated" TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "categories_uuid_pk"
        PRIMARY KEY("uuid"),
    CONSTRAINT "categories_name_unique"
        UNIQUE("name")
);

CREATE TABLE IF NOT EXISTS "roles_categories"(
    "uuid" UUID NOT NULL DEFAULT UUID_GENERATE_V4(),
    "role_uuid" UUID NOT NULL,
    "category_uuid" UUID NOT NULL,
    "privilage" BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT "roles_categories_uuid_pk"
        PRIMARY KEY("uuid"),
    CONSTRAINT "roles_categories_role_uuid_category_uuid_unique"
        UNIQUE("role_uuid", "category_uuid"),
    CONSTRAINT "roles_categoires_role_uuid_fk"
        FOREIGN KEY("role_uuid")
            REFERENCES "roles"("uuid")
                ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT "roles_categories_category_uuid_fk"
        FOREIGN KEY("category_uuid")
            REFERENCES "categories"("uuid")
                ON UPDATE CASCADE ON DELETE CASCADE
);