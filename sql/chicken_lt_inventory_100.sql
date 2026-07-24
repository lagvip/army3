-- Cho phep JSON cua 100 o tui do va 100 o ruong lon hon gioi han TEXT 64 KiB.
ALTER TABLE `players`
    MODIFY COLUMN `inventory_json` MEDIUMTEXT NOT NULL,
    MODIFY COLUMN `storage_json` MEDIUMTEXT NOT NULL;
