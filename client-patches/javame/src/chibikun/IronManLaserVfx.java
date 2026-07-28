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
    private static final int CHARGE_MS = 110;
    private static final int BEAM_GROW_MS = 120;
    private static final int COLOR_GLOW = 0x008CFF;
    private static final int COLOR_ENERGY = 0x00E8FF;
    private static final int COLOR_WHITE = 0xFFFFFF;
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
        IronManSkillClientState.reset();
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
            int pulse = 5 + elapsed / 45 % 3;
            drawEnergyOrb(graphics, startX, startY, pulse);
            if (elapsed < CHARGE_MS) {
                return;
            }

            int growMs = elapsed - CHARGE_MS;
            if (growMs > BEAM_GROW_MS) {
                growMs = BEAM_GROW_MS;
            }
            int beamEndX = startX + scaleRound(
                    endX - startX, growMs, BEAM_GROW_MS);
            int beamEndY = startY + scaleRound(
                    endY - startY, growMs, BEAM_GROW_MS);
            drawBeam(
                    graphics,
                    startX,
                    startY,
                    beamEndX,
                    beamEndY,
                    elapsed
            );
            drawEnergyOrb(
                    graphics,
                    beamEndX,
                    beamEndY,
                    11 + elapsed / 55 % 2
            );
        } finally {
            if (deltaX != 0 || deltaY != 0) {
                graphics.translate(-deltaX, -deltaY);
            }
        }
    }

    private static int scaleRound(int value, int numerator, int denominator) {
        long scaled = (long) value * numerator;
        long half = denominator / 2;
        if (scaled < 0) {
            return (int) ((scaled - half) / denominator);
        }
        return (int) ((scaled + half) / denominator);
    }

    private static void drawEnergyOrb(
            mGraphics graphics,
            int x,
            int y,
            int radius
    ) {
        drawDisc(graphics, x, y, radius + 4, COLOR_GLOW);
        drawDisc(graphics, x, y, radius, COLOR_ENERGY);
        int inner = radius - 3;
        if (inner < 2) {
            inner = 2;
        }
        drawDisc(graphics, x, y, inner, COLOR_WHITE);
    }

    private static void drawDisc(
            mGraphics graphics,
            int x,
            int y,
            int radius,
            int color
    ) {
        graphics.setColor(color);
        int radiusSquared = radius * radius;
        for (int row = -radius; row <= radius; row += 2) {
            int remaining = radiusSquared - row * row;
            if (remaining < 0) {
                remaining = 0;
            }
            int half = (int) Math.sqrt(remaining);
            graphics.fillRect(x - half, y + row, half * 2 + 1, 2);
        }
    }

    private static void drawBeam(
            mGraphics graphics,
            int x1,
            int y1,
            int x2,
            int y2,
            int elapsed
    ) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        if (dx == 0 && dy == 0) {
            return;
        }

        double length = Math.sqrt((double) dx * dx + (double) dy * dy);
        if (length < 1.0D) {
            return;
        }
        double ux = dx / length;
        double uy = dy / length;
        int offsetX = round(-dy / length);
        int offsetY = round(dx / length);
        if (offsetX == 0 && offsetY == 0) {
            offsetY = 1;
        }

        drawMuzzleFlare(
                graphics,
                x1,
                y1,
                ux,
                uy,
                offsetX,
                offsetY,
                elapsed
        );

        graphics.setColor(COLOR_GLOW);
        for (int offset = -10; offset <= 10; offset++) {
            drawRawLine(
                    graphics,
                    x1 + offsetX * offset,
                    y1 + offsetY * offset,
                    x2 + offsetX * offset,
                    y2 + offsetY * offset
            );
        }
        graphics.setColor(COLOR_ENERGY);
        for (int offset = -7; offset <= 7; offset++) {
            drawRawLine(
                    graphics,
                    x1 + offsetX * offset,
                    y1 + offsetY * offset,
                    x2 + offsetX * offset,
                    y2 + offsetY * offset
            );
        }
        graphics.setColor(COLOR_WHITE);
        for (int offset = -4; offset <= 4; offset++) {
            drawRawLine(
                    graphics,
                    x1 + offsetX * offset,
                    y1 + offsetY * offset,
                    x2 + offsetX * offset,
                    y2 + offsetY * offset
            );
        }
    }

    private static void drawMuzzleFlare(
            mGraphics graphics,
            int x,
            int y,
            double ux,
            double uy,
            int offsetX,
            int offsetY,
            int elapsed
    ) {
        int phase = elapsed / 35 % 4;
        graphics.setColor(0x00BFFF);
        for (int ray = -4; ray <= 4; ray++) {
            int spread = ray * 4;
            int tail = 13 + Math.abs(ray) * 3 + ((phase + ray + 4) & 3);
            int tailX = x - round(ux * tail) + offsetX * spread;
            int tailY = y - round(uy * tail) + offsetY * spread;
            int noseX = x + round(ux * 12) + offsetX * (spread / 4);
            int noseY = y + round(uy * 12) + offsetY * (spread / 4);
            drawRawLine(graphics, tailX, tailY, noseX, noseY);
        }

        graphics.setColor(COLOR_WHITE);
        for (int ray = -2; ray <= 2; ray++) {
            int spread = ray * 2;
            int tail = 9 + Math.abs(ray) * 2;
            drawRawLine(
                    graphics,
                    x - round(ux * tail) + offsetX * spread,
                    y - round(uy * tail) + offsetY * spread,
                    x + round(ux * 14),
                    y + round(uy * 14)
            );
        }
    }

    private static int round(double value) {
        if (value < 0.0D) {
            return (int) (value - 0.5D);
        }
        return (int) (value + 0.5D);
    }

    private static void drawRawLine(
            mGraphics graphics,
            int x1,
            int y1,
            int x2,
            int y2
    ) {
        graphics.a((float) x1, (float) y1, (float) x2, (float) y2, false);
    }
}
