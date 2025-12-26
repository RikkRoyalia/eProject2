package SHAIF.model;

// Enemy Data class
public class EnemyData {
    private double x, y;
    private String enemyType;

    public EnemyData(double x, double y, String type) {
        this.x = x;
        this.y = y;
        this.enemyType = type;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public String getEnemyType() { return enemyType; }
}
