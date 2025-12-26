package SHAIF.model;

// Platform Data class
public class PlatformData {
    private double x, y, width, height;
    private String platformType;

    public PlatformData(double x, double y, double width, double height, String type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.platformType = type;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public String getPlatformType() { return platformType; }
}
