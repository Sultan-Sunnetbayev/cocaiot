DO $$
BEGIN
    IF NOT EXISTS(SELECT 1 FROM pg_type WHERE typname = 'type_ownership') THEN
        CREATE TYPE "type_ownership" AS ENUM(
                'STATE_ORGANIZATION', 'COMMERCIAL_SOCIETY', 'PERSONAL_ENTERPRISE', 'FOREIGN_COMPANY', 'ENTREPRENEUR'
        );
    END IF;
    IF NOT EXISTS(SELECT 1 FROM pg_type WHERE typname = 'status_payment') THEN
        CREATE TYPE "status_payment" AS ENUM(
            'PAID', 'PAYMENT_TIME_HAS_EXPIRED'
        );
    END IF;

END$$;
