ALTER TABLE IF EXISTS "persons"
    ADD COLUMN IF NOT EXISTS "image" UUID,
    ADD COLUMN IF NOT EXISTS "copy_passport" UUID,
    DROP CONSTRAINT IF EXISTS "persons_image_fk",
    ADD CONSTRAINT "persons_image_fk"
        FOREIGN KEY("image")
            REFERENCES "files"("uuid")
                ON UPDATE CASCADE,
    DROP CONSTRAINT IF EXISTS "persons_copy_passport_fk",
    ADD CONSTRAINT "persons_copy_passport_fk"
        FOREIGN KEY("copy_passport")
            REFERENCES "files"("uuid")
            ON UPDATE CASCADE,
    DROP CONSTRAINT IF EXISTS "persons_image_unique",
    ADD CONSTRAINT "persons_image_unique"
        UNIQUE("image"),
    DROP CONSTRAINT IF EXISTS "persons_copy_passport_unique",
    ADD CONSTRAINT "persons_copy_passport_unique"
        UNIQUE("copy_passport");


    ALTER TABLE IF EXISTS "entrepreneurs"
    ADD COLUMN IF NOT EXISTS "web_site" CHARACTER VARYING(250),
    ADD COLUMN IF NOT EXISTS "type_work" CHARACTER VARYING(750),
    ADD COLUMN IF NOT EXISTS "logo" UUID,
    DROP CONSTRAINT IF EXISTS "entrepreneurs_logo_fk",
    ADD CONSTRAINT "entrepreneurs_logo_fk"
        FOREIGN KEY("logo")
            REFERENCES "files"("uuid")
                ON UPDATE CASCADE,
    DROP CONSTRAINT IF EXISTS "entrepreneurs_logo_unique",
    ADD CONSTRAINT "entrepreneurs_logo_unique"
        UNIQUE("logo");


ALTER TABLE "companies"
    ADD COLUMN IF NOT EXISTS "logo" UUID,
    ADD COLUMN IF NOT EXISTS "type_work" CHARACTER VARYING(750),
    DROP CONSTRAINT IF EXISTS "companies_logo_fk",
    ADD CONSTRAINT "companies_logo_fk"
            FOREIGN KEY("logo")
                REFERENCES "files"("uuid")
                    ON UPDATE CASCADE,
    DROP CONSTRAINT IF EXISTS "companies_logo_unique",
    ADD CONSTRAINT "companies_logo_unique"
        UNIQUE("logo");
