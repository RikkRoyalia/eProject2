package SHAIF.model;

// Obstacle Data class
public class ObstacleData {
    private double x, y, width, height;
    private String obstacleType;

    public ObstacleData(double x, double y, double width, double height, String type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.obstacleType = type;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public String getObstacleType() { return obstacleType; }
}
