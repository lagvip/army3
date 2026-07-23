#!/data/data/com.termux/files/usr/bin/bash
set -euo pipefail
cd "$(dirname "$0")"

for cmd in java gradle mariadb; do
    if ! command -v "$cmd" >/dev/null 2>&1; then
        echo "Thieu lenh $cmd. Cai bang: pkg install openjdk-21 gradle mariadb -y"
        exit 1
    fi
done

DB_DIR="${PREFIX:-/data/data/com.termux/files/usr}/var/lib/mysql"
if [ ! -d "$DB_DIR/mysql" ]; then
    mariadb-install-db --datadir="$DB_DIR" >/dev/null
fi

if ! mariadb-admin -u root ping --silent >/dev/null 2>&1; then
    SAFE_CMD=""
    if command -v mariadbd-safe >/dev/null 2>&1; then
        SAFE_CMD="mariadbd-safe"
    elif command -v mysqld_safe >/dev/null 2>&1; then
        SAFE_CMD="mysqld_safe"
    fi
    if [ -z "$SAFE_CMD" ]; then
        echo "Khong tim thay mariadbd-safe/mysqld_safe. Cai lai mariadb."
        exit 1
    fi
    "$SAFE_CMD" --datadir="$DB_DIR" >"$HOME/chicken_lt_mariadb.log" 2>&1 &
    for _ in $(seq 1 30); do
        mariadb-admin -u root ping --silent >/dev/null 2>&1 && break
        sleep 1
    done
fi

if ! mariadb-admin -u root ping --silent >/dev/null 2>&1; then
    echo "MariaDB chua khoi dong. Xem log: $HOME/chicken_lt_mariadb.log"
    exit 1
fi

mariadb -u root <<'SQL'
CREATE DATABASE IF NOT EXISTS chicken3 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'chicken'@'localhost' IDENTIFIED BY '123456';
ALTER USER 'chicken'@'localhost' IDENTIFIED BY '123456';
CREATE USER IF NOT EXISTS 'chicken'@'127.0.0.1' IDENTIFIED BY '123456';
ALTER USER 'chicken'@'127.0.0.1' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON chicken3.* TO 'chicken'@'localhost';
GRANT ALL PRIVILEGES ON chicken3.* TO 'chicken'@'127.0.0.1';
FLUSH PRIVILEGES;
SQL

TABLE_COUNT="$(mariadb -h 127.0.0.1 -u chicken --password=123456 -Nse "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='chicken3' AND table_name='accounts';")"
if [ "$TABLE_COUNT" = "0" ]; then
    echo "Dang import du lieu chicken3 lan dau..."
    mariadb -h 127.0.0.1 -u chicken --password=123456 chicken3 < sql/chicken3.sql
fi

mariadb -h 127.0.0.1 -u chicken --password=123456 chicken3 < sql/chicken_lt_accounts.sql

echo "Tai khoan test: admin / 1 va admin1 / 1"
echo "Dang build Chicken_lt..."
gradle clean build

echo "Dang chay Chicken_lt..."
exec gradle run
