-- Chay mot lan tren database cu truoc khi bat xac minh email/MFA.
ALTER TABLE `accounts`
  ADD COLUMN IF NOT EXISTS `password_changed_at` datetime DEFAULT NULL
  AFTER `last_login_at`;

CREATE TABLE IF NOT EXISTS `account_security_tokens` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` int(11) NOT NULL,
  `purpose` varchar(32) NOT NULL,
  `token_hash` binary(32) NOT NULL,
  `attempts_left` tinyint(3) UNSIGNED NOT NULL,
  `expires_at` datetime NOT NULL,
  `consumed_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `idx_security_token_active`
    (`account_id`,`purpose`,`consumed_at`,`expires_at`),
  CONSTRAINT `fk_security_token_account`
    FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`)
    ON UPDATE RESTRICT ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
