-- Chạy đúng một lần trên database Chicken LT cũ.
-- Giữ nguyên tài khoản/nhân vật thật; chỉ xóa guest nvn_ chưa có nhân vật.

START TRANSACTION;

DELETE a
FROM `accounts` a
LEFT JOIN `players` p ON p.`account_id` = a.`id`
WHERE a.`username` LIKE 'nvn\_%'
  AND p.`id` IS NULL;

COMMIT;

ALTER TABLE `accounts`
  CHANGE COLUMN `password` `password_hash` VARCHAR(255) NOT NULL,
  ADD COLUMN `email` VARCHAR(254) NULL AFTER `password_hash`,
  ADD COLUMN `phone` VARCHAR(20) NULL AFTER `email`,
  ADD COLUMN `email_verified` TINYINT(1) NOT NULL DEFAULT 0 AFTER `phone`,
  ADD COLUMN `phone_verified` TINYINT(1) NOT NULL DEFAULT 0 AFTER `email_verified`,
  ADD COLUMN `failed_login_attempts` INT NOT NULL DEFAULT 0 AFTER `is_admin`,
  ADD COLUMN `locked_until` DATETIME NULL AFTER `failed_login_attempts`,
  ADD COLUMN `last_login_at` DATETIME NULL AFTER `locked_until`,
  ADD COLUMN `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `last_login_at`,
  ADD COLUMN `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
      ON UPDATE CURRENT_TIMESTAMP AFTER `created_at`,
  ADD UNIQUE KEY `uk_accounts_username` (`username`),
  ADD UNIQUE KEY `uk_accounts_email` (`email`),
  ADD UNIQUE KEY `uk_accounts_phone` (`phone`);

ALTER TABLE `players`
  ADD UNIQUE KEY `uk_players_account_id` (`account_id`),
  ADD UNIQUE KEY `uk_players_name` (`name`),
  ADD CONSTRAINT `fk_players_account`
      FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`)
      ON UPDATE RESTRICT ON DELETE RESTRICT;
