package SHAIF.model;

/**
 * Enemy Data class - chứa thông tin enemy từ database
 */
public class EnemyData {
    private double x;
    private double y;
    private String enemyType;

    public EnemyData(double x, double y, String type) {
        this.x = x;
        this.y = y;
        this.enemyType = type;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getEnemyType() {
        return enemyType;
    }

    /**
     * Convert string type từ DB sang EnemyType enum
     */
    public EnemyType getEnemyTypeEnum() {
        try {
            return EnemyType.valueOf(enemyType);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid enemy type: " + enemyType);
            return EnemyType.SHOOTER; // default
        }
    }
}