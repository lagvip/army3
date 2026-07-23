-- Bổ sung an toàn cho hệ thống Biệt đội, Bạn bè, Xếp hạng.
ALTER TABLE `clans`
    ADD COLUMN IF NOT EXISTS `icon_id` SMALLINT NOT NULL DEFAULT 0;

ALTER TABLE `clan_members`
    ADD UNIQUE KEY IF NOT EXISTS `uk_clan_member_player` (`player_id`),
    ADD KEY IF NOT EXISTS `idx_clan_members_clan` (`clan_id`);

ALTER TABLE `player_friends`
    ADD UNIQUE KEY IF NOT EXISTS `uk_player_friend` (`player_id`, `friend_id`),
    ADD KEY IF NOT EXISTS `idx_player_friends_friend` (`friend_id`);
