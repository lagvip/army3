ALTER TABLE `players`
    ADD COLUMN IF NOT EXISTS `inventory_revision`
        BIGINT NOT NULL DEFAULT 0 AFTER `stats_revision`;
