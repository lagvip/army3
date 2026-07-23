INSERT INTO accounts (id, username, password, is_banned, is_online, is_admin)
VALUES (1, 'admin', '1', 0, 0, 1)
ON DUPLICATE KEY UPDATE username='admin', password='1', is_banned=0, is_online=0, is_admin=1;

INSERT INTO accounts (id, username, password, is_banned, is_online, is_admin)
VALUES (2, 'admin1', '1', 0, 0, 1)
ON DUPLICATE KEY UPDATE username='admin1', password='1', is_banned=0, is_online=0, is_admin=1;

UPDATE players SET name='ChickenAdmin' WHERE account_id=1 AND id=3;

INSERT INTO players (id, account_id, name, gold, cup, gem, stats_json, inventory_json, equipped_json, pocket_json, storage_json)
SELECT 4, 2, 'ChickenAdmin1', 1000000, 0, 1000, '{\"power\":100,\"avenger\":100,\"kill\":0,\"dead\":1,\"assist\":0,\"trainingSuccess\":1,\"trainingWins\":0,\"busyHammer\":0,\"nHammer\":2,\"exp\":1000000,\"point\":0,\"pointAdd\":[1000,0,0,0,0,0]}', '[]', '[{\"id\":1,\"quantity\":1,\"HP\":100,\"index\":0,\"options\":[{\"id\":8,\"param\":5}]},{\"id\":25,\"quantity\":1,\"HP\":100,\"index\":1,\"options\":[{\"id\":3,\"param\":10}]},{\"id\":50,\"quantity\":1,\"HP\":100,\"index\":2,\"options\":[{\"id\":3,\"param\":10}]},{\"id\":75,\"quantity\":1,\"HP\":100,\"index\":3,\"options\":[{\"id\":3,\"param\":10}]},{\"id\":100,\"quantity\":1,\"HP\":100,\"index\":4,\"options\":[{\"id\":3,\"param\":10},{\"id\":13,\"param\":2}]},{\"id\":130,\"quantity\":1,\"HP\":100,\"index\":5,\"options\":[{\"id\":1,\"param\":350},{\"id\":14,\"param\":350}]}]', '[-1,-1]', '[]'
WHERE NOT EXISTS (SELECT 1 FROM players WHERE account_id=2);
