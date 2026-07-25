package chibikun;

import java.io.DataInputStream;

/**
 * Hieu ung Iron Man rieng cho client Java ME.
 *
 * Lop nay chi doc toa do hien thi do server gui qua CMD 125. No khong tinh
 * va cham, damage hay thay doi state tran dau.
 */
public final class IronManLaserVfx {

    public static final byte COMMAND = 125;
    private static final int PROTOCOL_VERSION = 1;
    private static boolean active;
    private static long startAt;
    private static int durationMs;
    private static int startX;
    private static int startY;
    private static int endX;
    private static int endY;

    private IronManLaserVfx() {
    }

    public static synchronized void handle(Message message) {
        try {
            DataInputStream reader = message.reader();
            int version = reader.readUnsignedByte();
            if (version != PROTOCOL_VERSION || reader.available() < 11) {
                active = false;
                return;
            }

            reader.readByte(); // whoShoot: chi de debug/protocol, khong dung lam quyen.
            startX = reader.readShort();
            startY = reader.readShort();
            endX = reader.readShort();
            endY = reader.readShort();
            durationMs = reader.readUnsignedShort();
            if (durationMs < 120) {
                durationMs = 120;
            } else if (durationMs > 2000) {
                durationMs = 2000;
            }
            startAt = System.currentTimeMillis();
            active = true;
        } catch (Exception ignored) {
            active = false;
        }
    }

    public static synchronized void paint(mGraphics graphics) {
        if (!active || graphics == null) {
            return;
        }
        int elapsed = (int) (System.currentTimeMillis() - startAt);
        if (elapsed < 0) {
            elapsed = 0;
        }
        if (elapsed >= durationMs) {
            active = false;
            return;
        }

        int oldTranslateX = graphics.getTranslateX();
        int oldTranslateY = graphics.getTranslateY();
        int deltaX = -Camera.c - oldTranslateX;
        int deltaY = -Camera.d - oldTranslateY;
        if (deltaX != 0 || deltaY != 0) {
            graphics.translate(deltaX, deltaY);
        }
        try {
            int pulse = 3 + elapsed / 45 % 3;
            drawCore(graphics, startX, startY, pulse);
            if (elapsed < 90) {
                return;
            }

            int grow = elapsed - 90;
            if (grow > 100) {
                grow = 100;
            }
            int beamEndX = startX + (endX - startX) * grow / 100;
            int beamEndY = startY + (endY - startY) * grow / 100;
            drawBeam(graphics, startX, startY, beamEndX, beamEndY);
            if (grow == 100) {
                drawCore(graphics, endX, endY, 2);
            }
        } finally {
            if (deltaX != 0 || deltaY != 0) {
                graphics.translate(-deltaX, -deltaY);
            }
        }
    }

    private static void drawCore(
            mGraphics graphics,
            int x,
            int y,
            int radius
    ) {
        graphics.setColor(0xFF2A00);
        graphics.fillRect(
                x - radius - 1,
                y - radius - 1,
                radius * 2 + 3,
                radius * 2 + 3
        );
        graphics.setColor(0x13DCE8);
        graphics.fillRect(
                x - radius,
                y - radius,
                radius * 2 + 1,
                radius * 2 + 1
        );
        int inner = radius > 1 ? radius - 1 : 1;
        graphics.setColor(0xFFFFFF);
        graphics.fillRect(
                x - inner,
                y - inner,
                inner * 2 + 1,
                inner * 2 + 1
        );
    }

    private static void drawBeam(
            mGraphics graphics,
            int x1,
            int y1,
            int x2,
            int y2
    ) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        if (dx == 0 && dy == 0) {
            return;
        }

        int offsetX = Math.abs(dy) > Math.abs(dx) ? 1 : 0;
        int offsetY = offsetX == 0 ? 1 : 0;
        graphics.setColor(0xFF2A00);
        for (int offset = -3; offset <= 3; offset++) {
            graphics.a(
                    (float) (x1 + offsetX * offset),
                    (float) (y1 + offsetY * offset),
                    (float) (x2 + offsetX * offset),
                    (float) (y2 + offsetY * offset),
                    false
            );
        }
        graphics.setColor(0x13DCE8);
        for (int offset = -1; offset <= 1; offset++) {
            graphics.a(
                    (float) (x1 + offsetX * offset),
                    (float) (y1 + offsetY * offset),
                    (float) (x2 + offsetX * offset),
                    (float) (y2 + offsetY * offset),
                    false
            );
        }
        graphics.setColor(0xFFFFFF);
        graphics.a((float) x1, (float) y1, (float) x2, (float) y2, false);
    }
}
