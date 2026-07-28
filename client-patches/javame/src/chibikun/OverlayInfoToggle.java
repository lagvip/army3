package chibikun;

/** Bat/tat bang thong tin debug duoi man hinh bang lenh chat "show". */
public final class OverlayInfoToggle {

    private static boolean visible = true;

    private OverlayInfoToggle() {
    }

    public static synchronized boolean isVisible() {
        return visible;
    }

    /**
     * @return true neu day la lenh local va khong duoc gui len server.
     */
    public static synchronized boolean handleChat(String text) {
        if (text == null || !"show".equalsIgnoreCase(text.trim())) {
            return false;
        }
        visible = !visible;
        return true;
    }
}
