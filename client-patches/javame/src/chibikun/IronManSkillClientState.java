package chibikun;

import java.io.DataInputStream;

/**
 * Trang thai hien thi skill laser nguc Iron Man tren client Java ME.
 *
 * CMD 124 trung voi mot lenh native cua client cu. Vi vay packet phai duoc
 * chan trong MessageHandler truoc switch goc; neu roi vao nhanh goc, client
 * se goi mSystem.f() va cho vo han tren luong nhan packet.
 */
public final class IronManSkillClientState {

    public static final byte COMMAND = 124;
    private static final int PROTOCOL_VERSION = 1;
    private static boolean armed;

    private IronManSkillClientState() {
    }

    public static synchronized void handle(Message message) {
        try {
            DataInputStream reader = message.reader();
            int version = reader.readUnsignedByte();
            if (version != PROTOCOL_VERSION || reader.available() < 1) {
                armed = false;
                return;
            }
            armed = reader.readBoolean();
        } catch (Exception ignored) {
            armed = false;
        }
    }

    public static synchronized boolean isArmed() {
        return armed;
    }

    public static synchronized void reset() {
        armed = false;
    }
}
