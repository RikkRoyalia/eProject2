package SHAIF.model;

public class PlatformData {
    private double x, y, width, height;
    private String platformType;
    private boolean isGround;

    public PlatformData(double x, double y, double width, double height, String type, boolean isGround) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.platformType = type;
        this.isGround = isGround;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public String getPlatformType() { return platformType; }
    public boolean isGround() { return isGround; }
}