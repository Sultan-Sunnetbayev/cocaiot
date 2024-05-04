ALTER TABLE IF EXISTS "cocaiot_members"
    ADD COLUMN IF NOT EXISTS "initial_date_last_payment" TIMESTAMP,

    DROP CONSTRAINT IF EXISTS "cocaiot_members_check_initial_date_final_date",
    ADD CONSTRAINT "cocaiot_members_check_initial_date_final_date"
        CHECK("initial_date"<="final_date"),

    DROP CONSTRAINT IF EXISTS "cocaiot_members_check_initial_date_initial_date_last_payment",
    ADD CONSTRAINT "cocaiot_members_check_initial_date_initial_date_last_payment"
        CHECK("initial_date"<="initial_date_last_payment"),

    DROP CONSTRAINT IF EXISTS "cocaiot_members_check_initial_date_last_payment_final_date",
    ADD CONSTRAINT "cocaiot_members_check_initial_date_last_payment_final_date"
        CHECK("initial_date_last_payment"<="final_date");