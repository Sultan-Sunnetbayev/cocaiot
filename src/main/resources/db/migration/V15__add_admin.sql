INSERT INTO "roles"("name") VALUES('Admin') ON CONFLICT DO NOTHING;

INSERT INTO "users"("name", "surname", "email", "password", "role_uuid")
    VALUES('Admin', 'Adminow', 'admin', '$2a$12$2l36XmVOk0lw9QAsVAY2s./L9h7EiMV7GYGMGM4WK0xK.wCzCRAGO',
                                            (SELECT "uuid" FROM "roles" WHERE "name"='Admin')) ON CONFLICT DO NOTHING;

INSERT INTO "roles_categories"("role_uuid", "category_uuid", "privilage")
    SELECT (SELECT role.uuid FROM roles role WHERE role.name='Admin') AS role_uuid, category.uuid AS category_uuid, TRUE AS privilage
        FROM categories category
        LEFT JOIN roles_categories role_category ON role_category.category_uuid =category.uuid ON CONFLICT DO NOTHING
