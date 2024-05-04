CREATE TABLE IF NOT EXISTS "entrepreneurs_type_activities"(
    "entrepreneur_uuid" UUID NOT NULL,
    "type_activity_uuid" UUID NOT NULL,

    CONSTRAINT "entrepreneurs_type_activities_entrepreneur_uuid_type_activity_uuid_unique"
        UNIQUE("entrepreneur_uuid", "type_activity_uuid"),
    CONSTRAINT "entrepreneurs_type_activities_entrepreneur_uuid_fk"
        FOREIGN KEY("entrepreneur_uuid")
            REFERENCES "entrepreneurs"("uuid")
                ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT "entrepreneurs_type_activities_type_activity_uuid_fk"
        FOREIGN KEY("type_activity_uuid")
            REFERENCES "type_activities"("uuid")
                ON UPDATE CASCADE ON DELETE CASCADE
);