import java.awt.*;

public enum Theme {
    LIGHT("Light", new Color(0xFFFFFF), new Color(0x111111), new Color(0xF2F2F2), new Color(0xFFFFFF), new Color(0x0A5BD3)),
    DARK("Dark", new Color(0x1E1E1E), new Color(0xD71408), new Color(0x2A2A2A), new Color(0x242424), new Color(0x4EA1FF)),
    BLUE("Blue", new Color(0x0E1A2B), new Color(0x2DFB21), new Color(0x13233B), new Color(0x0F1F35), new Color(0x7BB6FF)),
    HIGH_CONTRAST("High Contrast", Color.BLACK, Color.MAGENTA, new Color(0x202020), Color.BLACK, Color.YELLOW);

    public final String label;
    public final Color windowBg;
    public final Color textFg;
    public final Color panelBg;
    public final Color inputBg;
    public final Color accent;

    Theme(String label, Color windowBg, Color textFg, Color panelBg, Color inputBg, Color accent) {
        this.label = label;
        this.windowBg = windowBg;
        this.textFg = textFg;
        this.panelBg = panelBg;
        this.inputBg = inputBg;
        this.accent = accent;
    }

    @Override public String toString() { return label; }
}
