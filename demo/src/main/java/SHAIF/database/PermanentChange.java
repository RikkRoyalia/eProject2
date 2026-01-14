package SHAIF.database;

/**
 * PermanentChange - Thay đổi vĩnh viễn trong world
 */
public class PermanentChange {
    private String changeKey;
    private String changeType;
    private double x;
    private double y;

    public PermanentChange(String changeKey, String changeType, double x, double y) {
        this.changeKey = changeKey;
        this.changeType = changeType;
        this.x = x;
        this.y = y;
    }

    // Getters
    public String getChangeKey() { return changeKey; }
    public String getChangeType() { return changeType; }
    public double getX() { return x; }
    public double getY() { return y; }
}
